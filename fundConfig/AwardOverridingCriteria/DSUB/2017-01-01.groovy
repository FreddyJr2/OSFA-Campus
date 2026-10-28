//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.AwardOverridingCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

/* Baseline - Award Overriding Criteria for DSUB DUNSUB - combined Term and Non-Term */
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

def thisPpFundOutput = lazy {
    partialFundOutput.getInLoanPeriod(lpNo).getInPaymentPeriod(ppNo)
}

def thisPpIsProrated = lazy {
    log.debug(thisPpFundOutput().eligible && thisPpFundOutput().prorated, "PRORATED PP")
}

def isInNASuffix = term && term.standard && acy.terms.findAll { t -> t.startDate >= term.startDate && t.standard }
        .every { t -> t.notAttending }

def disbursedAmount = thisPpIsDisbursed()
        ? disbursedDisbursementsInThisPp().totalDisbursementAmount
        : (BigDecimal) null
def acceptedAmount = fundAcceptance.getInScope(scope)
        .filterStatuses {it != "PENDING_ACCEPTANCE" }
        .let {it.empty ? null : it.validatedAcceptedAmount }
def effectiveAcceptedAmount = thisPpIsDisbursed() ? disbursedAmount : acceptedAmount

def now = LocalDate.now()
def activeSummerTerms = acy.terms.filter {
    it.summer && program.courses.getAssociatedTo(it)
            .any {
                it.schedulingStatus != "PROJECTED"
            }
}
def summerHasCourses = !activeSummerTerms.empty
def summerStart = summerHasCourses ? activeSummerTerms.startDate : null
def isActiveSummerTerm = activeSummerTerms.contains(term)
def isBeyondSummerThreshold = summerStart && now >= summerStart.minusDays(60)
def summerDocExists = lazy {
    def summerDocsInAwy = receivedDocuments.forAwardYear(awy)
            .getDocumentsForDocCode("SummerApplication", "Student")
    log.debug(summerDocsInAwy != null && !summerDocsInAwy.empty, "SUMMER DOC EXISTS")
}

def isSummerState = log.debug(summerHasCourses && isBeyondSummerThreshold && summerDocExists(), "SUMMER STATE")

def isLessThanHalfTime = enrollmentStatus == "LESS_THAN_HALF_TIME"

optimizer
        .setCumulativeGradeLevelLimitApplicable(
                (!term || term.standard)
                        && (!isLastAcy || program.undergraduate)
                        && (!isLastAcy || !isInNASuffix)
                        && !thisPpIsProrated())
        .setFundingForPeriodFilter { fundCode, fundType -> fundCode != "DISCOUNT" }
        .setCompensationValue(0)
        .addPhase(thisPpIsDisbursed(), 1) {
            it.withGiven(disbursedAmount)
        }

if (!isLessThanHalfTime) {
    optimizer
            .addPhase(!term, 2)
            .addPhase(term && term.standard && (!isSummerState || effectiveAcceptedAmount), 2) {
                it.withPpMaximum(isSummerState ? effectiveAcceptedAmount : null)
            }
            .addPhase(isSummerState && (isActiveSummerTerm || term.standard && effectiveAcceptedAmount == null), 3)
            .addPhase(isSummerState && (isActiveSummerTerm || term.standard), 4)
}

if (program.enrollmentStatus == "X" || pp.status == "CANCELED")
{
    return awardInfo.withMaxAmount(0.0).withRetainedAmount(0.0)
}
if (program.term)
{
    def overlappingTerms = primaryProgram.terms.getOverlappingWith(term)
    /* Methods returning collection, as well as filters, will be plural; otherwise singular */
    if (overlappingTerms.studentsTermStatuses.contains("WITHDRAWN"))
    {
        log.debug("STUDENT TERM IS WITHDRAWN")

        def r2t4OverlappingTerms = r2t4.getOverlappingWith(term)
                .getWithProcessStatuses(["NOT_REQUIRED", "COMPLETED"])
                .getWithOldas(overlappingTerms.oldas)

        if (!r2t4OverlappingTerms.empty)
        {
            log.debug("R2T4 Overlapping Terms is not empty, FREEZING!")

            def sum = nonCancelledDisbursementsInThisPp().ppMaxDisbursementAmount

            return awardInfo.withMaxAmount(sum).withRetainedAmount(sum)
        }
        else
        {
            log.debug("R2T4 PROCESS NOT YET IN A FREEZING STATE")
        }
    }

    if (enrollmentStatus == "NOT_ATTENDING")
    {
        log.debug("ZEROING - enrollmentStatus={} ", enrollmentStatus)
        return awardInfo.withMaxAmount(0.0).withRetainedAmount(0.0)
    }

}

if (log.debug(lp.endDate <= now, "LOAN PERIOD IN THE PAST")
        || (thisPpIsProrated() && thisPpIsDisbursed()))
{
    def frozenAmount = disbursedAmount == null ? 0.0 : disbursedAmount 
    log.debug("FREEZING TO {}", frozenAmount)
    return awardInfo.withMaxAmount(frozenAmount).withRetainedAmount(frozenAmount)
}

return awardInfo