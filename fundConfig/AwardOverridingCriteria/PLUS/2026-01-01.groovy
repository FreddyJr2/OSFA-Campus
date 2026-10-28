/**
 * CUSTOMER ATTENTION GUIDE
 * ------------------------
 * Review this file in the following order:
 *
 * 1. Search for `OB3InterimException`
 *    This is where the script checks for the interim exception document and its
 *    metadata.
 *
 * 2. Search for `codEnrollmentStatusEffectiveDate`
 *    This value is set from the OB3 document override when provided; otherwise
 *    it uses the program start date sent in SAFI.
 *
 * 3. Search for `latestDatedEdSignal`
 *    This is where the script compares department of education loan limit exception flag from various sources.
 *
 * 4. Search for `withEdLoanLimitException`, `withLoanLimitException`, and
 *    `withCodEnrollmentStatusEffectiveDate`
 *    This is where the script writes the new OB3-related values back onto the
 *    PLUS award override object.
 *
 * 5. Everything below the optimizer section
 *    This is the underlying PLUS packaging logic that existed before OB3:
 *    disbursed protection, summer logic, graduate caps, R2T4 freezing, and
 *    zero/freeze rules.
 *
 * 6. Institutional Loan Limit functionality
 *    This does not currently exist in the script, but allows you to apply institution defined Direct Loan
 *    limits at the program level for the academic year.
 *    Example:
 *          def xplusAwardInfo = awardInfo.asXplus()
 *          .withInstitutionalLimit((fundCode == "PLUS" && program.isGraduate() && program.code == "JTA1") ? 20000.0 : null)
 *
 * Practical framing:
 * - Customers are most likely to review the OB3 interim document logic and any
 *   school-specific policy assumptions around exceptions.
 * - Customers who already customize PLUS packaging should also read the lower
 *   PLUS sections carefully, because that is where the award is capped, frozen,
 *   or zeroed in day-to-day packaging behavior.
 *
 * How to read this script:
 * - The top half establishes context.
 * - The middle writes OB3-related values back onto the award override object.
 * - The bottom half decides how the term is actually packaged, frozen, or zeroed.
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.AwardOverridingCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name
import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

//REQUIRED SUMMER START
final requiredSummerConfig = [
        "PROGRAM_A": [requiredForAllYears: true, summerFullTimeCredits: 12],
        "PROGRAM_B": [requiredSummerProgramYears: [2, 3, 4], summerFullTimeCredits: 12]
]

final requiredSummerOverrideSlots = [
        [slotLabel: "Program 1", programCode: "AC1500", summerRequired: "AC1501", summerFullTimeCredits: "AC1502"],
        [slotLabel: "Program 2", programCode: "AC1600", summerRequired: "AC1601", summerFullTimeCredits: "AC1602"],
        [slotLabel: "Program 3", programCode: "AC1700", summerRequired: "AC1701", summerFullTimeCredits: "AC1702"],
]

def summerIsOptionalInAcy = true
if (term) {
    def buildMatchingRequiredSummerOverride = { IReceivedDoc doc ->
        if (doc != null) {
            for (slot in requiredSummerOverrideSlots) {
                def documentProgramCode = doc.getDocumentField(slot.programCode)
                if (program.getCode() != documentProgramCode) {
                    continue
                }
                return [
                        summerRequired       : "YES".equalsIgnoreCase(doc.getDocumentField(slot.summerRequired)),
                        summerFullTimeCredits: doc.getDocumentField(slot.summerFullTimeCredits) as Integer]
            }
        }
        null
    }

    final requiredSummerOverrideDoc = {
        def requireSummerDocs = receivedDocuments.forAwardYear(awy)
                .getDocumentsForDocCode("RequiredSummerOverride", "Student")
        if (requireSummerDocs == null || requireSummerDocs.isEmpty()) {
            return null
        }
        return requireSummerDocs.max { it.getReceiptDate() }
    }()

    final nOfStandardTerms = acy.getTerms()
            .findAll { it.isStandard() }
            .size()
    final fullTimeStandardTermUnits = nOfStandardTerms == 0
            ? 0.0
            : program.getAydUnits() / nOfStandardTerms

    final requiredSummerInfo = {
        def curInfo = buildMatchingRequiredSummerOverride(requiredSummerOverrideDoc)
        if (curInfo == null) {
            curInfo = requiredSummerConfig[program.getCode()]
            if (curInfo != null) {
                curInfo.summerRequired = curInfo.requiredForAllYears || (curInfo.requiredSummerProgramYears ?: []).contains(acyNo)
            }
        }
        if (curInfo != null && curInfo.summerRequired && curInfo.summerFullTimeCredits == null) {
            if (nOfStandardTerms > 0) {
                curInfo.summerFullTimeCredits = fullTimeStandardTermUnits
            } else {
                curInfo = null
            }
        }
        curInfo
    }()

    final requiredSummerUnits = (requiredSummerInfo?.summerFullTimeCredits ?: 0.0) as BigDecimal
    summerIsOptionalInAcy = requiredSummerUnits == 0.0
    optimizer.setExtraProperty("requiredSummerFullTimeCredits", requiredSummerUnits)
}
//REQUIRED SUMMER END

// Reuse expensive lookups safely.
static <T> Closure<T> lazy(Closure<T> init) {
    T value
    boolean isInitDue = true
    return {
        if (isInitDue) {
            isInitDue = false
            value = init()
        }
        value
    }
}

// ============================================================================
// OB3 ADVANCED INTERIM EXCEPTION RULES - BEGIN
// ============================================================================

def documentCipRestrictedProgramTypes = [
        "Graduate",
        "Doctoral",
        "Certificate (Graduate)",
        "Certificate (Doctoral)",
        "Certificate (Doctorate)"
] as Set

def ob3ProgramSlots = [
        [slotLabel       : "Program 1", programCode: "AC1200", temporarySchoolInterimExceptionValue: "AC1201",
         credentialLevel : "AC1202", programCipCode: "AC1203", expirationDate: "AC1204",
         expirationReason: "AC1205", codEnrollmentStatusEffectiveDate: "AC1206", legacyPipeDelimited: true],
        [slotLabel       : "Program 2", programCode: "AC1300", temporarySchoolInterimExceptionValue: "AC1301",
         credentialLevel : "AC1302", programCipCode: "AC1303", expirationDate: "AC1304",
         expirationReason: "AC1305", codEnrollmentStatusEffectiveDate: "AC1306", legacyPipeDelimited: false],
        [slotLabel       : "Program 3", programCode: "AC1400", temporarySchoolInterimExceptionValue: "AC1401",
         credentialLevel : "AC1402", programCipCode: "AC1403", expirationDate: "AC1404",
         expirationReason: "AC1405", codEnrollmentStatusEffectiveDate: "AC1406", legacyPipeDelimited: false]
]

def ob3ReferenceDate = new LocalDate(2026, 7, 1)
def ob3FirstAllowedAwardYear = 2027

def ob3DocumentDate = { String documentValue ->
    documentValue ? LocalDate.parse(documentValue, DateTimeFormat.forPattern("yyyyMMdd")) : null
}

// Program-code matching:
// - the document can store up to 3 separate program-specific field groups
// - the script checks Program 1 first, then Program 2, then Program 3
// - Program 1 still accepts legacy pipe-delimited values in AC1200 during
//   transition, but the delivered shape is one program code per slot
def matchesCurrentProgramCode = { documentProgramCode, allowPipeDelimited ->
    def currentProgramCode = program.getCode()
    if (!documentProgramCode || !currentProgramCode) {
        return false
    }
    if (documentProgramCode == currentProgramCode) {
        return true
    }
    if (allowPipeDelimited) {
        return documentProgramCode.startsWith("${currentProgramCode}|") ||
                documentProgramCode.endsWith("|${currentProgramCode}") ||
                documentProgramCode.contains("|${currentProgramCode}|")
    }
    false
}

def buildMatchingOb3InterimExceptionRecord = { IReceivedDoc doc, Integer sourceAwardYear ->
    for (slot in ob3ProgramSlots) {
        def documentProgramCode = doc.getDocumentField(slot.programCode)
        if (!matchesCurrentProgramCode(documentProgramCode, slot.legacyPipeDelimited)) {
            continue
        }
        return [
                doc                                     : doc,
                sourceAwardYear                         : sourceAwardYear,
                receiptDate                             : doc.getReceiptDate(),
                reviewDate                              : doc.getReviewDate(),
                externalDocumentId                      : doc.getExternalDocumentId(),
                slotLabel                               : slot.slotLabel,
                matchedProgramCode                      : documentProgramCode,
                temporarySchoolInterimExceptionValue    : doc.getDocumentField(slot.temporarySchoolInterimExceptionValue),
                credentialLevel                         : doc.getDocumentField(slot.credentialLevel),
                programCipCode                          : doc.getDocumentField(slot.programCipCode),
                interimExceptionExpirationDateOverride  : ob3DocumentDate(doc.getDocumentField(slot.expirationDate)),
                codEnrollmentStatusEffectiveDateOverride: ob3DocumentDate(doc.getDocumentField(slot.codEnrollmentStatusEffectiveDate))
        ]
    }

    null
}

def ob3OverrideInterimExceptionRecord = lazy {
    def matchingRecords = []
    for (historicalAwardYear in awy..ob3FirstAllowedAwardYear) {
        def ob3Docs = receivedDocuments.forAwardYear(historicalAwardYear)
                .getDocumentsForDocCode("OB3InterimException", "Student")
        if (ob3Docs == null || ob3Docs.isEmpty()) {
            continue
        }
        matchingRecords.addAll(ob3Docs.collect { doc ->
            buildMatchingOb3InterimExceptionRecord(doc, historicalAwardYear)
        }.findAll { it != null })
    }

    // `getReceiptDate()` can collapse multiple same-day document events onto
    // the same day-level value. Use review timestamp and external document id
    // as deterministic tie-breakers so the latest school document wins.
    matchingRecords.max {
        [
                it.receiptDate?.getTime() ?: Long.MIN_VALUE,
                it.reviewDate?.getTime() ?: Long.MIN_VALUE,
                it.sourceAwardYear ?: Integer.MIN_VALUE,
                it.externalDocumentId ?: ""
        ]
    }
}

def ob3OverrideRecord = ob3OverrideInterimExceptionRecord()
def ob3OverrideMatchedProgramSlot = ob3OverrideRecord?.slotLabel
def ob3OverrideTemporarySchoolInterimExceptionValue =
        ob3OverrideRecord?.temporarySchoolInterimExceptionValue == "Yes"
                ? true
                : ob3OverrideRecord?.temporarySchoolInterimExceptionValue == "No"
                ? false
                : null
def ob3OverrideCredentialLevel = ob3OverrideRecord?.credentialLevel
def ob3OverrideProgramCipCode = ob3OverrideRecord?.programCipCode
def ob3OverrideInterimExceptionExpirationDate =
        ob3OverrideRecord?.interimExceptionExpirationDateOverride as LocalDate
def ob3OverrideCodEnrollmentStatusEffectiveDate =
        ob3OverrideRecord?.codEnrollmentStatusEffectiveDateOverride as LocalDate
def codEnrollmentStatusEffectiveDate =
        ob3OverrideCodEnrollmentStatusEffectiveDate ?: program.getStartDate()
def codEnrollmentStatusEffectiveDateSource =
        ob3OverrideCodEnrollmentStatusEffectiveDate != null
                ? "document"
                : "program.getStartDate()"

// ============================================================================
// CUSTOMER REVIEW AREA: school-side OB3 exception policy
// ============================================================================
//
// The interim-exception logic is active from the first packaged academic year
// that overlaps 2026-07-01, or the first packaged academic year after it when
// no academic year overlaps that date. Once active:
// - ED `Yes` is treated as proof that the historical qualifying criteria were met
// - the system-defined package schedule is the primary duration model
// - the document can shorten or extend that duration through one
//   school-reviewed expiration date
// - the document can supply an affirmative fallback only when ED data is missing

def ob3AmenableAcys = acys.filter {
    it.getStartDate() >= ob3ReferenceDate || it.getEndDate() >= ob3ReferenceDate
}

def continuationAnchorAcy = acys.findAll {
    ob3ReferenceDate > it.getEndDate() ||
            (it.getStartDate() < ob3ReferenceDate && ob3AmenableAcys.contains(it))
}.with { it ? last() : null }

def continuationAnchorAcyNo = continuationAnchorAcy?.getNumber()

def isOb3Amenable = awy >= ob3FirstAllowedAwardYear &&
        ob3AmenableAcys.contains(acy)

def isWithinActiveInterimExceptionWindow = isOb3Amenable

def threeAcademicYearCapDate = ob3AmenableAcys[2]?.getEndDate()

def systemExpirationDate = threeAcademicYearCapDate

def programType = program.getProgramType()
def programCipCode = program.getCipCode()
def currentProgramUsesCipRestrictiveValidation =
        programType != null && documentCipRestrictedProgramTypes.contains(programType)

def documentOverrideApplies = isWithinActiveInterimExceptionWindow && ob3OverrideRecord != null
def credentialLevelRestrictiveMismatch = false
def programCipRestrictiveMismatch = false

if (documentOverrideApplies && ob3OverrideCredentialLevel != null) {
    if (ob3OverrideCredentialLevel != programType) {
        credentialLevelRestrictiveMismatch = true
    }
}

if (documentOverrideApplies && currentProgramUsesCipRestrictiveValidation && ob3OverrideProgramCipCode != null) {
    if (programCipCode != ob3OverrideProgramCipCode) {
        programCipRestrictiveMismatch = true
    }
}

if (documentOverrideApplies && (credentialLevelRestrictiveMismatch || programCipRestrictiveMismatch)) {
    def restrictiveMismatchMessages = []
    if (credentialLevelRestrictiveMismatch) {
        restrictiveMismatchMessages << "Credential Level restrictive mismatch"
    }
    if (programCipRestrictiveMismatch) {
        restrictiveMismatchMessages << "Program CIP Code restrictive mismatch"
    }
    def restrictiveMismatchDetails = restrictiveMismatchMessages.join(" | ")
    log.debug("OB3Config PLUS award override script RESTRICTIVE_PROGRAM_MISMATCH ob3OverrideMatchedProgramSlot = {}, restrictiveMismatchDetails = {}",
            ob3OverrideMatchedProgramSlot,
            restrictiveMismatchDetails)
}

def appliedDocumentExpirationOverride = documentOverrideApplies
        ? ob3OverrideInterimExceptionExpirationDate
        : null
if (isWithinActiveInterimExceptionWindow &&
        appliedDocumentExpirationOverride != null &&
        systemExpirationDate != null &&
        appliedDocumentExpirationOverride > systemExpirationDate) {
    log.debug("OB3Config PLUS award override script EXPIRATION_OVERRIDE_EXTENDS_SYSTEM_DATE systemExpirationDate = {}, appliedDocumentExpirationOverride = {}",
            systemExpirationDate,
            appliedDocumentExpirationOverride)
}
def effectiveExpirationDate = isWithinActiveInterimExceptionWindow
        ? (appliedDocumentExpirationOverride ?: systemExpirationDate)
        : null

// ============================================================================
// CUSTOMER REVIEW AREA: federal evidence from ISIR and COD
// ============================================================================
//
// The script checks federal sources and then uses the latest available evidence.
// Current logic:
// - historical ISIR transactions can supply dated loan limit exception values
// - COD can also supply a dated value
// - if COD and ISIR land on the same date, COD wins the tie
// - the runtime does not read a separate direct NSLDS feed here
//
def datedEdSignals = []
if (awy >= ob3FirstAllowedAwardYear) {
    for (isirAwy in awy..ob3FirstAllowedAwardYear) {
        def awyIsirs = allAwardYearIsirs.forAwardYear(isirAwy)
        if (awyIsirs == null || awyIsirs.isEmpty()) {
            continue
        }
        for (isir in awyIsirs) {
            String isirLoanLimitExceptionFlag =
                    isir.getField(IsirField.NSLDS_LOANLIMITEXCEPTIONFLAG)?.getOrNull()
            def isirExceptionValue =
                    (isirLoanLimitExceptionFlag == null || isirLoanLimitExceptionFlag.isBlank())
                            ? null
                            : isirLoanLimitExceptionFlag == "Y"
            def isirReceivedDate = LocalDate.fromDateFields(isir.getReceivedDate())
            if (isirExceptionValue != null && isirReceivedDate != null) {
                datedEdSignals << [
                        source        : "ISIR",
                        awardYear     : isirAwy,
                        date          : isirReceivedDate.toDateTimeAtStartOfDay(),
                        value         : isirExceptionValue,
                        priority      : 1,
                        cpsTransNumber: isir.getCpsTransNumber() ?: ""
                ]
            }
        }
    }
}

if (dloan.getNsldsReportReceivedDate() != null && dloan.isNsldsLoanLimitException() != null) {
    datedEdSignals << [
            source        : "NSLDS",
            awardYear     : awy,
            date          : dloan.getNsldsReportReceivedDate(),
            value         : dloan.isNsldsLoanLimitException(),
            priority      : 2,
            cpsTransNumber: Integer.MAX_VALUE
    ]
}

if (cod.getLoanLimitExceptionDate() != null && cod.isLoanLimitException() != null) {
    datedEdSignals << [
            source        : "COD",
            awardYear     : awy,
            date          : cod.getLoanLimitExceptionDate(),
            value         : cod.isLoanLimitException(),
            priority      : 3,
            cpsTransNumber: Integer.MAX_VALUE
    ]
}

def latestDatedEdSignal = datedEdSignals
        ? datedEdSignals.max { a, b ->
    a.date <=> b.date ?:
            a.priority <=> b.priority ?:
                    a.awardYear <=> b.awardYear ?:
                            a.cpsTransNumber <=> b.cpsTransNumber
}
        : null

def isExceptionOnFile = latestDatedEdSignal?.value as Boolean
def edExceptionSource = latestDatedEdSignal?.source
def edExceptionSourceAwardYear = latestDatedEdSignal?.awardYear
def edExceptionSourceDate = latestDatedEdSignal?.date

def xplusAwardInfo = awardInfo.asXplus()

def baseLoanLimitException
if (!isOb3Amenable) {
    // Preserve the pre-OB3 delivered behavior before the interim-exception
    // effective date.
    baseLoanLimitException = true
} else if (isExceptionOnFile != null) {
    baseLoanLimitException = isExceptionOnFile
} else if (documentOverrideApplies && ob3OverrideTemporarySchoolInterimExceptionValue != null) {
    baseLoanLimitException = ob3OverrideTemporarySchoolInterimExceptionValue
} else {
    baseLoanLimitException = continuationAnchorAcy != null &&
            disbursements.getTitleIv()
                    .getLoans()
                    .filterAwardYears { it < ob3FirstAllowedAwardYear }
                    .getCancelled(false)
                    .getWithStatus("DISBURSED")
                    .getTotalDisbursementAmount() > 0.0
}

def schoolCredentialLevelMismatchForcesNo = documentOverrideApplies &&
        credentialLevelRestrictiveMismatch
def schoolProgramCipMismatchForcesNo = documentOverrideApplies &&
        programCipRestrictiveMismatch
// The expiration boundary intentionally uses the current academic-year
// schedule end date from the current evaluation scope, falling back only if
// that surface is sparse. This matches the agreed OB3 spec and keeps the
// decision anchored to the current academic year rather than a single term.
def effectiveExpirationForcesNo = isWithinActiveInterimExceptionWindow &&
        effectiveExpirationDate != null &&
        acy.getStartDate() > effectiveExpirationDate
def schoolNegativeOverrideForcesNo = schoolCredentialLevelMismatchForcesNo ||
        schoolProgramCipMismatchForcesNo ||
        effectiveExpirationForcesNo
def computedLoanLimitException = schoolNegativeOverrideForcesNo
        ? false
        : baseLoanLimitException

def isLoanLimitException = computedLoanLimitException
log.debug("OB3Config PLUS award override script DECISION_SUMMARY ob3OverrideMatchedProgramSlot = {}, ob3OverrideSourceAwardYear = {}, ob3OverrideTemporarySchoolInterimExceptionValue = {}, effectiveExpirationDate = {}, codEnrollmentStatusEffectiveDateSource = {}, edExceptionSource = {}, edExceptionSourceAwardYear = {}, edExceptionSourceDate = {}, continuationAnchorAcyNo = {}, threeAcademicYearCapDate = {}, effectiveExpirationForcesNo = {}, schoolCredentialLevelMismatchForcesNo = {}, schoolProgramCipMismatchForcesNo = {}, baseLoanLimitException = {}, isLoanLimitException = {}",
        ob3OverrideMatchedProgramSlot,
        ob3OverrideRecord?.sourceAwardYear,
        ob3OverrideTemporarySchoolInterimExceptionValue,
        effectiveExpirationDate,
        codEnrollmentStatusEffectiveDateSource,
        edExceptionSource,
        edExceptionSourceAwardYear,
        edExceptionSourceDate,
        continuationAnchorAcyNo,
        threeAcademicYearCapDate,
        effectiveExpirationForcesNo,
        schoolCredentialLevelMismatchForcesNo,
        schoolProgramCipMismatchForcesNo,
        baseLoanLimitException,
        isLoanLimitException)

// Write the OB3-related values back to the PLUS award override object.
// - `withEdLoanLimitException(...)` preserves the latest ED-sourced value for
//   visibility and traceability
// - `withLoanLimitException(...)` stores the final controlling institutional
//   result after system-derived duration and school-side logic are applied
// - `withCodEnrollmentStatusEffectiveDate(...)` writes the resolved COD-facing
//   date through the current platform API surface
xplusAwardInfo = xplusAwardInfo
        .withEdLoanLimitException(isExceptionOnFile)
        .withLoanLimitException(isLoanLimitException)
        .withCodEnrollmentStatusEffectiveDate(codEnrollmentStatusEffectiveDate)

// ============================================================================
// OB3 ADVANCED INTERIM EXCEPTION RULES - END
// ============================================================================

// Current-period disbursement context.
def disbursementsInThisPp = lazy {
    disbursements.getInScope(scope)
}

def nonCancelledDisbursementsInThisPp = lazy {
    disbursementsInThisPp()
            .getCancelled(false)
            .filterStatuses { status -> status != "DISBURSEMENT_CANCELED" }
}

def disbursedDisbursementsInThisPp = lazy {
    nonCancelledDisbursementsInThisPp().getWithStatus("DISBURSED")
}

def thisPpIsDisbursed = lazy {
    log.debug(!disbursedDisbursementsInThisPp().empty, "DISBURSED PP")
}

// Current-period and prior-plan state used throughout the PLUS logic.
def disbursedAmount = thisPpIsDisbursed()
        ? disbursedDisbursementsInThisPp().totalDisbursementAmount
        : (BigDecimal) null

def packagedAmount = lazy {
    if (financialPlan.version == 1) {
        return 0.0
    }
    previousFinancialPlan.withVersion(financialPlan.version - 1)
            .getInScope(scope)
            .getFund(fundCode)
            .maxAmount
}

def originatedAmount = nonCancelledDisbursementsInThisPp().totalDisbursementAmount

def now = LocalDate.now()
def ppIsInThePast = pp.endDate < now

// Summer behavior used by the delivered PLUS logic.
def activeSummerTerms = acy.terms.filter {
    it.summer && program.courses.getAssociatedTo(it).any {
        it.schedulingStatus != "PROJECTED"
    }
}
def summerHasCourses = !activeSummerTerms.empty
def summerStart = summerHasCourses ? activeSummerTerms.startDate : null
def isActiveSummerTerm = activeSummerTerms.contains(term)
def isBeyondSummerThreshold = summerStart && now >= summerStart.minusDays(60)
def summerDocExists = lazy {
    def summerDocs = receivedDocuments.forAwardYear(awy)
            .getDocumentsForDocCode("SummerApplication", "Student")
    log.debug(summerDocs != null && !summerDocs.empty, "SUMMER DOC EXISTS")
}

// PLUS-specific summer-only signal used by the delivered optimizer phases.
def actualPlusLoan = plusLoan instanceof IActualPlusLoanAPI
        ? (IActualPlusLoanAPI) plusLoan
        : null
def summerOnlyLoan = log.debug(actualPlusLoan &&
        acy.terms.getOverlappingWith(actualPlusLoan)
                .every { it.summer },
        "SUMMER ONLY LOAN")
def summerStateExists = log.debug(!summerOnlyLoan && summerIsOptionalInAcy && activeSummerTerms.any { it.isSummerTrailer() }, "SUMMER STATE EXISTS")
def isSummerState = log.debug(summerStateExists && isBeyondSummerThreshold && summerDocExists(), "SUMMER STATE")
def isLessThanHalfTime = enrollmentStatus == "LESS_THAN_HALF_TIME"
def frozenPp = log.debug(isLessThanHalfTime || thisPpIsDisbursed() || (isSummerState && (ppIsInThePast || term.standard)), "FROZEN PP")
def notFrozenPp = !frozenPp
def summerAsStandard = isActiveSummerTerm && !summerStateExists
def standardOrSummerAsStandard = term?.isStandard() || summerAsStandard
optimizer.setExtraProperty("sorMustZeroActualSummerUnits", summerStateExists && !isSummerState)

// ============================================================================
// DELIVERED PLUS PACKAGING LOGIC
// ============================================================================
//
// Everything below this point is the underlying PLUS packaging behavior:
// - preserve disbursed amounts
// - preserve originated amounts for frozen periods
// - apply summer logic
// - zero invalid periods
// - freeze on withdrawal / R2T4 / past loan periods
//
// Most customers should not start by changing this section unless they already
// have institution-specific PLUS packaging customizations.
//
// Business meaning of the phases:
// - Phase 1: preserve already-disbursed amounts
// - Phase 2: for frozen-but-not-yet-disbursed periods, preserve the originated amount
// - Phase 3/4: control standard vs summer allocation for still-open periods
optimizer.setFundingForPeriodFilter { fundCode, fundType -> fundCode != "DISCOUNT" }
        .addPhase(thisPpIsDisbursed(), 1) {
            it.withGiven(disbursedAmount, "Keep Disbursed Disbursements")
        }
        .addPhase(frozenPp && !thisPpIsDisbursed(), 2) {
            it.withGiven(originatedAmount, "Freeze PP to Originated Amount")
        }
        .addPhase(notFrozenPp && (!term || standardOrSummerAsStandard), 3)
        .addPhase(notFrozenPp && term && isActiveSummerTerm && (summerOnlyLoan || isSummerState), 4)

// If the student's program is in status X or the payment period is canceled,
// the script zeroes the award immediately.
if (program.enrollmentStatus == "X" || pp.status == "CANCELED") {
    return xplusAwardInfo.withMaxAmount(0.0).withRetainedAmount(0.0)
}

// Graduate-specific protection.
// If the student is in graduate status and there is a prior financial plan
// version, the script preserves whichever is larger:
// - the prior packaged amount
// - the already-disbursed amount
if (program.enrollmentStatus == "G" && financialPlan.version > 1) {
    return xplusAwardInfo.withMaxAmount([packagedAmount(), disbursedAmount].max())
}

// Term-based withdrawal and attendance rules.
// This is where the script decides whether a withdrawn term should freeze based
// on R2T4 state, or whether a not-attending term should be zeroed.
if (program.term) {
    def overlappingTerms = primaryProgram.terms.getOverlappingWith(term)
    if (overlappingTerms.studentsTermStatuses.contains("WITHDRAWN")) {
        log.debug("AWARD FREEZING CRITERIA - STUDENT TERM IS WITHDRAWN")
        def r2t4OverlappingTerms = r2t4.getOverlappingWith(term)
                .getWithProcessStatuses(["NOT_REQUIRED", "COMPLETED"])
                .getWithOldas(overlappingTerms.oldas)
        if (!r2t4OverlappingTerms.empty) {
            log.debug("AWARD FREEZING CRITERIA - R2T4 Overlapping Terms is not empty, FREEZING!")
            def sum = nonCancelledDisbursementsInThisPp().ppMaxDisbursementAmount
            return xplusAwardInfo.withMaxAmount(sum).withRetainedAmount(sum)
        } else {
            log.debug("AWARD FREEZING CRITERIA - R2T4 PROCESS NOT YET IN A FREEZING STATE")
        }
    }

    if (enrollmentStatus == "NOT_ATTENDING") {
        log.debug("ZEROING - enrollmentStatus={} ", enrollmentStatus)
        return xplusAwardInfo.withMaxAmount(0.0).withRetainedAmount(0.0)
    }
}

// Final freeze rule for completed loan periods.
if (log.debug(lp.endDate <= now, "LOAN PERIOD IN THE PAST")) {
    def frozenAmount = disbursedAmount == null ? 0.0 : disbursedAmount
    log.debug("FREEZING TO {}", frozenAmount)
    return xplusAwardInfo.withMaxAmount(frozenAmount).withRetainedAmount(frozenAmount)
}

// If none of the prior conditions are met, return the PLUS override object
// with the OB3 values set and allow the optimizer-driven packaging logic to stand.
return xplusAwardInfo