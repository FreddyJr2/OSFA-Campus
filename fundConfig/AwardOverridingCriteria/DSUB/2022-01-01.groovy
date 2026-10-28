/**
 * Primary Purpose: Determine and override award amounts for DSUB/DUNSUB within a payment period by returning an IAwardOverrideAPI instance,
 *                  honoring federal Title IV regulations, institutional rules, program, disbursement history, and acceptance.
 * Key Calculations:
 * - Derive disbursedAmount for the current Payment Period (PP)
 * - Resolve packagedAmount from prior financial plan version
 * - Determine acceptedAmount and effectiveAcceptedAmount based on disbursement state
 * - Freeze/retain amounts in R2T4 or completed loan period scenarios
 * Critical Business Rules:
 * - Already-disbursed PP amounts must be honored (not decreased), except where compliance requires $0 (e.g., canceled PP)
 * - Cumulative grade level constraint only applies for standard terms or summer header under specified conditions
 * - Fund acceptance gates funding during summer trailer logic if within threshold and documentation exists
 * - Student enrollment and term statuses (e.g., NOT_ATTENDING, WITHDRAWN) can zero or freeze awards
 * Boundary Conditions and Limits:
 * - Max override equals sum of non-canceled PP max disbursements when R2T4 overlap is frozen
 * - Loan Period end-date in the past or PP prorated and disbursed freezes to disbursedAmount
 * - Null-safety on amounts (e.g., disbursedAmount can be null; frozenAmount coerced to 0.0)
 * Financial Aid Implications:
 * - Prevents over-awards by freezing or capping amounts once disbursed or when R2T4 conditions require
 * - Aligns funding with attendance intensity and acceptance status, especially for summer trailer cases
 * - Ensures amounts do not violate cumulative grade-level limits when applicable
 * Required Inputs:
 * - Disbursements, fund acceptance, financial plan, terms/periods, program/enrollment context, received documents (Summer Application)
 * Expected Output:
 * - IAwardOverrideAPI with withMaxAmount(...) and optional withRetainedAmount(...) to enforce freezing/retention
 * Error Handling Approach:
 * - Uses lazy evaluations to protect calls until necessary
 * - Defensive null checks on derived values (e.g., summer docs, dates)
 * - Logging via IScriptLoggerAPI.debug/info/warn/error for traceability and audit
 * Integration Points:
 * - disbursements, partialFundOutput, previousFinancialPlan, fundAcceptance, receivedDocuments, r2t4, program, optimizer, acy/term APIs
 * Compliance Considerations:
 * - Title IV disbursement integrity: do not decrease already disbursed valid amounts
 * - R2T4: freeze when overlapping terms and statuses indicate a completed/not-required process
 * - Enrollment/attendance: zeroing when NOT_ATTENDING; grade-level constraints for undergrad; summer documentation gating
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.AwardOverridingCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name
import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

// CONDITIONALS:
// Check: Lazy initializer factory (lazy)
// Variables: init (Closure<T>), isInitDue (boolean), value (generic T)
// Purpose: Defer expensive API calls or computations until actually needed; ensures idempotent evaluation while caching result.
// Logic:
//   - Return a closure that on first invocation executes init(), stores result in value, flips isInitDue=false.
//   - Subsequent calls return cached value without re-executing init().
// Success Path: init() runs exactly once, subsequent calls reuse value.
// Failure Path: N/A (no branching failure path; if init throws, caller context must handle).
// Business Rules: Avoid unnecessary API calls (e.g., to disbursements) to maintain performance and determinism.
// Error Handling: Exceptions inside init bubble up to callers; logging should be implemented by callers where needed.
// Financial Impact: Performance stability prevents timeouts and ensures consistent calculations for award overrides.
// BOUNDARY CHECKS:
// Check: N/A
// ARITHMETIC:
// Operation: N/A
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

// CONDITIONALS:
// Check: Scope disbursements retrieval for the current PP
// Variables: disbursements.getInScope(scope)
// Purpose: Limit disbursements to the exact fund + ACY + LP + PP under evaluation.
// Logic:
//   - Use scope (IFundScopeAPI) to narrow disbursement dataset to the current evaluation context.
// Success Path: Returns in-scope disbursements collection.
// Failure Path: Empty collection if none in scope.
// Business Rules: Only in-scope amounts can be used to override current PP.
// Error Handling: Lazy wrapper defers call; if underlying API fails, calling sites can handle/log.
// Financial Impact: Prevents cross-period contamination of amounts.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def disbursementsInThisPp = lazy {
    disbursements.getInScope(scope)
}

// CONDITIONALS:
// Check: Filter to non-canceled disbursements
// Variables: disbursementsInThisPp().getCancelled(false), filterStatuses { status != "DISBURSEMENT_CANCELED" }
// Purpose: Ensure canceled or cancel-coded items do not influence override.
// Logic:
//   - getCancelled(false): retain non-canceled items only
//   - filterStatuses: explicit status exclusion for "DISBURSEMENT_CANCELED" for extra safety
// Success Path: Collection contains only valid, non-canceled entries.
// Failure Path: Empty collection if all canceled.
// Business Rules: Canceled disbursements cannot justify retained or max amounts.
// Error Handling: If API returns null status, filter predicate should safely exclude only explicit matches; otherwise included.
// Financial Impact: Avoids overstating disbursed totals.
// BOUNDARY CHECKS:
// Check: Disbursement status integrity
// Source: Disbursement status codes configuration
// Variables: status
// Validation:
//   - Status must not equal "DISBURSEMENT_CANCELED"
// Edge Cases:
//   - Null/unknown statuses pass through first filter; subsequent logic should rely on isDisbursed flags.
//   - Duplicate entries are managed by platform collection semantics.
// Error Handling: Log/monitor upstream inconsistencies.
// Impact: Conservative exclusion prevents over-retention.
// ARITHMETIC: N/A
def nonCancelledDisbursementsInThisPp = lazy {
    disbursementsInThisPp()
            .getCancelled(false)
            .filterStatuses { status -> status != "DISBURSEMENT_CANCELED" }
}

// CONDITIONALS:
// Check: Identify actually disbursed items in-scope
// Variables: nonCancelledDisbursementsInThisPp().getWithStatus("DISBURSED")
// Purpose: Track only those with posted disbursements for freeze/retention.
// Logic:
//   - Apply "DISBURSED" filter on previously non-canceled set.
// Success Path: Contains only truly disbursed records.
// Failure Path: Empty if nothing disbursed yet.
// Business Rules: Only posted disbursements affect freeze logic.
// Error Handling: If status taxonomy changes, this filter may need update; logging elsewhere.
// Financial Impact: Distinguishes planned vs. actual cash flow.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def disbursedDisbursementsInThisPp = lazy {
    nonCancelledDisbursementsInThisPp().getWithStatus("DISBURSED")
}

// CONDITIONALS:
// Check: Is this PP disbursed
// Variables: disbursedDisbursementsInThisPp().empty, log.debug(...)
// Purpose: Flag if any disbursed items exist to drive freeze/acceptance logic.
// Logic:
//   - Negate emptiness of disbursed collection.
//   - log.debug(condition, "DISBURSED PP") also returns the boolean condition in this context.
// Success Path: True when at least one disbursement present.
// Failure Path: False otherwise.
// Business Rules: Disbursed PP is not to be decreased below disbursed amount.
// Error Handling: Logging aids audit.
// Financial Impact: Prevents clawbacks inconsistent with Title IV absent R2T4/void rules.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def thisPpIsDisbursed = lazy {
    log.debug(!disbursedDisbursementsInThisPp().empty, "DISBURSED PP")
}

// CONDITIONALS:
// Check: Retrieve partial fund output for this PP
// Variables: partialFundOutput.getInLoanPeriod(lpNo).getInPaymentPeriod(ppNo)
// Purpose: Access eligibility/proration flags tied to this PP from partial plan state.
// Logic:
//   - Drill-down into LP then PP to access scoped fund output slice.
// Success Path: Returns IFinancialPlanAPI slice for PP.
// Failure Path: If not present, may result in null/exception; later usage is lazy and guarded.
// Business Rules: Downstream proration and eligibility rely on this snapshot.
// Error Handling: Rely on lazy evaluation and boolean short-circuiting.
// Financial Impact: Affects constraints like grade-level limit applicability.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def thisPpFundOutput = lazy {
    partialFundOutput.getInLoanPeriod(lpNo).getInPaymentPeriod(ppNo)
}

// CONDITIONALS:
// Check: Is PP prorated and eligible
// Variables: thisPpFundOutput().eligible, thisPpFundOutput().prorated, log.debug(...)
// Purpose: Proration can alter optimization constraints and freezing rules.
// Logic:
//   - Evaluate both eligible and prorated flags;
//   - log.debug returns combined boolean with message "PRORATED PP".
// Success Path: True if both eligible and prorated.
// Failure Path: False otherwise.
// Business Rules: Prorated/disbursed PPs are frozen to disbursed amount later.
// Error Handling: If fund output slice missing, lazy access may throw; operationally expected to exist when PP is evaluated.
// Financial Impact: Avoids over/under-packaging by freezing disbursed prorated PPs.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def thisPpIsProrated = lazy {
    log.debug(thisPpFundOutput().eligible && thisPpFundOutput().prorated, "PRORATED PP")
}

// CONDITIONALS:
// Check: Identify "NA suffix" state (not attending subsequent standard terms)
// Variables: term, term.standard, acy.terms, t.startDate, t.standard, t.notAttending
// Purpose: Determine if current term sits in an "NA suffix" sequence impacting cumulative grade level applicability.
// Logic:
//   - If term exists and is standard,
//   - Find all standard terms in ACY starting at/after current term start,
//   - Check that every such term has notAttending flag.
// Success Path: True => indicates "NA suffix" scenario.
// Failure Path: False otherwise.
// Business Rules: When last ACY or NA suffix, cumulative grade level limit may be disabled.
// Error Handling: Property access depends on platform schema; null-safe checks applied at head (term && ...).
// Financial Impact: May relax optimization constraint to not reduce awards unnecessarily at program end.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def isInNASuffix = term && term.standard && acy.terms.findAll { t -> t.startDate >= term.startDate && t.standard }
        .every { t -> t.notAttending }

// CONDITIONALS:
// Check: Resolve disbursedAmount for current PP
// Variables: thisPpIsDisbursed(), disbursedDisbursementsInThisPp().totalDisbursementAmount
// Purpose: Set baseline amount for freeze/retention.
// Logic:
//   - If PP is disbursed, get totalDisbursementAmount; else null.
// Success Path: BigDecimal value when disbursed.
// Failure Path: null when not disbursed.
// Business Rules: Disbursed totals are authoritative for freeze/retention.
// Error Handling: totalDisbursementAmount should be numeric; null handled later by coalescing to 0.0 when needed.
// Financial Impact: Drives retained amount setting.
// BOUNDARY CHECKS: N/A
// ARITHMETIC:
// Operation: N/A (direct assignment)
def disbursedAmount = thisPpIsDisbursed()
        ? disbursedDisbursementsInThisPp().totalDisbursementAmount
        : (BigDecimal) null

// CONDITIONALS:
// Check: Retrieve packagedAmount from previous financial plan version
// Variables: financialPlan.version, previousFinancialPlan.withVersion(...).getInScope(scope).maxAmount
// Purpose: Preserve prior packaged decision for continuity when versioning financial plans.
// Logic:
//   - If current version == 1 => no prior, return 0.0
//   - Else load version-1 plan, scope it, and take maxAmount
// Success Path: Returns prior max amount or 0.0 for first version.
// Failure Path: If previous version not found, platform may throw; assumed available when version > 1.
// Business Rules: Avoids sudden drops without regulatory trigger.
// Error Handling: Lazy call; upstream versioning ensures presence. Consider try/catch if platform inconsistent.
// Financial Impact: Baseline for graduate special case (max of packaged vs disbursed).
// BOUNDARY CHECKS:
// Check: Version lower bound
// Source: Financial plan versioning
// Variables: financialPlan.version
// Validation:
//   - If version == 1, boundary => return 0.0
// Error Handling: N/A
// ARITHMETIC: N/A
def packagedAmount = lazy {
    if (financialPlan.version == 1) {
        return 0.0
    }
    previousFinancialPlan.withVersion(financialPlan.version - 1)
            .getInScope(scope)
            .getFund(fundCode)
            .maxAmount
}

// CONDITIONALS:
// Check: Determine acceptedAmount excluding pending
// Variables: fundAcceptance.getInScope(scope), filterNullableStatuses { it != "PENDING_ACCEPTANCE" }, validatedAcceptedAmount
// Purpose: Use only finalized acceptance decisions in award capping.
// Logic:
//   - Scope acceptance records
//   - Exclude "PENDING_ACCEPTANCE"
//   - If empty => null, else use validatedAcceptedAmount
// Success Path: BigDecimal accepted or null if none finalized.
// Failure Path: Null indicates no acceptance constraint.
// Business Rules: Acceptance gates award increases in summer trailer logic.
// Error Handling: Null-safe filter and let; platform ensures numeric validatedAcceptedAmount.
// Financial Impact: Caps award to accepted amount under specific conditions.
// BOUNDARY CHECKS:
// Check: Status must be finalized (not PENDING_ACCEPTANCE)
// Source: Fund Acceptance status configuration
// Variables: statuses collection
// Validation: Only finalized statuses contribute.
// Edge Cases: Multiple records => validated aggregate provided by platform.
// Error Handling: N/A
// Impact: Prevents over-award beyond student’s acceptance.
// ARITHMETIC: N/A
def acceptedAmount = fundAcceptance.getInScope(scope)
        .filterNullableStatuses {it != "PENDING_ACCEPTANCE" }
        .let {it.empty ? null : it.validatedAcceptedAmount }

// CONDITIONALS:
// Check: Choose effectiveAcceptedAmount based on disbursement state
// Variables: thisPpIsDisbursed(), disbursedAmount, acceptedAmount
// Purpose: Once disbursed, acceptance is superseded by actual disbursed cash; otherwise use acceptance if available.
// Logic:
//   - If disbursed => use disbursedAmount
//   - Else => use acceptedAmount
// Success Path: Returns BigDecimal or null depending on availability.
// Failure Path: Null when neither disbursed nor accepted.
// Business Rules: Post-disbursement, acceptance is moot for caps.
// Error Handling: N/A
// Financial Impact: Guides optimizer Fund Acceptance cap phase.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def effectiveAcceptedAmount = thisPpIsDisbursed() ? disbursedAmount : acceptedAmount

// CONDITIONALS: N/A
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def now = LocalDate.now()

// CONDITIONALS:
// Check: Identify active summer terms with non-projected courses
// Variables: acy.terms.filter{ it.summer && program.courses.getAssociatedTo(it).any{ it.schedulingStatus != "PROJECTED" } }
// Purpose: Summer trailer/header logic applies only when there are active scheduled courses.
// Logic:
//   - Filter ACY terms by summer flag
//   - For each term, check program.courses associated and ensure any course not "PROJECTED"
// Success Path: Collection of active summer terms.
// Failure Path: Empty list if none active.
// Business Rules: Prevents gating on inactive/projected summers.
// Error Handling: Null-safe assumptions; any() protects empty course lists.
// Financial Impact: Determines whether summer documentation gating applies.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def activeSummerTerms = acy.terms.filter {
    it.summer && program.courses.getAssociatedTo(it)
            .any {
                it.schedulingStatus != "PROJECTED"
            }
}

// CONDITIONALS:
// Check: Filter to summer trailers within active summer terms
// Variables: activeSummerTerms.filter { it.summerTrailer }
// Purpose: Summer trailer-specific gating.
// Logic:
//   - Subset of active summer terms possessing trailer flag.
// Success Path: List of trailer terms.
// Failure Path: Empty list if no trailer.
// Business Rules: Documentation threshold evaluated only for trailers.
// Error Handling: N/A
// Financial Impact: Affects availability and caps via optimizer.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def activeSummerTrailers = activeSummerTerms.filter {
    it.summerTrailer
}

// CONDITIONALS:
// Check: Does a summer trailer exist; compute start if so
// Variables: activeSummerTrailers.empty, activeSummerTrailers.startDate
// Purpose: Establish threshold date reference for documentation.
// Logic:
//   - summerTrailerHasCourses => true if trailers exist
//   - summerTrailerStart => earliest trailer startDate or collection startDate accessor
// Success Path: start date resolved for threshold calc.
// Failure Path: Null start when no trailers.
// Business Rules: Documentation timing window begins relative to trailer start.
// Error Handling: Assume platform provides startDate on multi-term collection; if not, first element’s date in implementation.
// Financial Impact: Influences whether Fund Acceptance is enforced now.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def summerTrailerHasCourses = !activeSummerTrailers.empty
def summerTrailerStart = summerTrailerHasCourses ? activeSummerTrailers.startDate : null

// CONDITIONALS:
// Check: Is the evaluated term an active summer term
// Variables: activeSummerTerms.contains(term)
// Purpose: Later gating points depend on whether the current term is one of the active summers.
// Logic:
//   - contains() set membership.
// Success Path: True if term is active summer.
// Failure Path: False otherwise.
// Business Rules: Summer header vs standard behavior.
// Error Handling: Null-safe term is used above; here assumed present when checked.
// Financial Impact: Modifies phase selection in optimizer.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def isActiveSummerTerm = activeSummerTerms.contains(term)

// CONDITIONALS:
// Check: Is current date beyond threshold before summer trailer start
// Variables: summerTrailerStart, now, minusDays(60)
// Purpose: Enforce documentation window (60-day threshold) for summer trailer behavior.
// Logic:
//   - Only computes when summerTrailerStart exists
//   - now >= summerTrailerStart.minusDays(60) indicates threshold reached
// Success Path: True when within threshold window or past it.
// Failure Path: False otherwise.
// Business Rules: Applicants must provide Summer Application by threshold to unlock funding without acceptance gating.
// Error Handling: Null-safe guard on summerTrailerStart.
// Financial Impact: Potentially caps awards to acceptance if threshold reached.
// BOUNDARY CHECKS:
// Check: 60-day window boundary
// Source: Institutional policy for summer trailer documentation
// Variables: summerTrailerStart, now
// Validation:
//   - Ensure summerTrailerStart non-null
// Edge Cases:
//   - Start date equal to now-60 => threshold satisfied
//   - Leap days/time zones not relevant (LocalDate)
// Error Handling: N/A
// Impact: Gating applied only when appropriate.
// ARITHMETIC:
// Operation: Threshold date calculation
// Formula: thresholdDate = summerTrailerStart.minusDays(60)
// Variables:
//   - summerTrailerStart (LocalDate)
//   - thresholdDate (LocalDate)
// Steps:
//   1. Subtract 60 days
//   2. Compare with now
// Business Context: Documentation freshness window
// Result: boolean for gating
// Financial Impact: May restrict award without acceptance
// Limitations: Fixed 60-day policy
def isBeyondSummerTrailerThreshold = summerTrailerStart && now >= summerTrailerStart.minusDays(60)

// CONDITIONALS:
// Check: Does a Summer Application document exist in AWY
// Variables: receivedDocuments.forAwardYear(awy).getDocumentsForDocCode("SummerApplication", "Student")
// Purpose: Documentation prerequisite for summer trailer funding logic.
// Logic:
//   - Fetch AY-scoped received documents for code and role
//   - log.debug(booleanPresent, "SUMMER DOC EXISTS") returns boolean
// Success Path: True when docs found
// Failure Path: False when null/empty
// Business Rules: Documentation-driven gating
// Error Handling: Null checks included; logging for audit
// Financial Impact: Allows funding phases without strict acceptance constraint
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def summerDocExists = lazy {
    def summerDocsInAwy = receivedDocuments.forAwardYear(awy)
            .getDocumentsForDocCode("SummerApplication", "Student")
    log.debug(summerDocsInAwy != null && !summerDocsInAwy.empty, "SUMMER DOC EXISTS")
}

// CONDITIONALS:
// Check: Compute composite summer trailer state; compute active summer header
// Variables: summerTrailerHasCourses, isBeyondSummerTrailerThreshold, summerDocExists(), term?.summerHeader, isActiveSummerTerm, log.debug(...)
// Purpose: Drive optimizer phases based on summer header/trailer conditions.
// Logic:
//   - isSummerTrailerState true when trailer exists, threshold reached, and documentation exists
//   - isActiveSummerHeader true when current term has header flag and is active summer
// Success Path: Accurate flags for optimizer configuration
// Failure Path: False flags disengage summer-specific paths
// Business Rules: Summer requires explicit documentation + timing
// Error Handling: Null-safe term access via ?.
// Financial Impact: Determines if acceptance cap phase applies
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def isSummerTrailerState = log.debug(summerTrailerHasCourses && isBeyondSummerTrailerThreshold && summerDocExists(), "SUMMER TRAILER STATE")
def isActiveSummerHeader = log.debug(term?.summerHeader && isActiveSummerTerm, "ACTIVE SUMMER HEADER")

// CONDITIONALS:
// Check: Less-than-half-time attendance
// Variables: enrollmentStatus == "LESS_THAN_HALF_TIME"
// Purpose: Some funds have half-time or greater requirement; used to skip adding certain optimizer phases.
// Logic:
//   - Direct equality check
// Success Path: Flag used below
// Failure Path: N/A
// Business Rules: LTHT may restrict loan disbursement periods
// Error Handling: N/A
// Financial Impact: Avoids ineligible awards
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def isLessThanHalfTime = enrollmentStatus == "LESS_THAN_HALF_TIME"

// CONDITIONALS:
// Check: Standard term or active summer header
// Variables: term?.isStandard(), isActiveSummerHeader
// Purpose: Determine applicability of cumulative grade-level constraints
// Logic:
//   - True if standard term OR active summer header
// Success Path: Used in optimizer constraint setting
// Failure Path: False leads to not including in constraint
// Business Rules: Non-standard/summer trailer may be excluded from cumulative limit
// Error Handling: Null-safe access
// Financial Impact: Alters optimization constraints, can permit higher awards if excluded
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def standardOrSummerHeader = (term?.isStandard() || isActiveSummerHeader)

// CONDITIONALS:
// Check: Configure optimizer with constraints/phases including disbursed-amount preservation
// Variables: optimizer.setEnsureFinalZeroAmountIsIneligible(false), setCumulativeGradeLevelLimitApplicable(...),
//           setFundingForPeriodFilter(...), setCompensationValue(0), addPhase(thisPpIsDisbursed(), 1){ ... },
//           disbursedAmount
// Purpose: Establish optimization rules: disbursed retention, grade-level limit inclusion, fund filtering.
// Logic:
//   - Do not force zero to be ineligible
//   - Apply cumulative grade-level limit only when:
//       (!term || standardOrSummerHeader) && (!isLastAcy || program.undergraduate) && (!isLastAcy || !isInNASuffix) && !thisPpIsProrated()
//   - Exclude DISCOUNT from cost constraints
//   - Compensation value 0
//   - Phase 1 (conditional): If disbursed, withGiven(disbursedAmount) to hold disbursed
// Success Path: Optimizer respects disbursed cash and appropriate constraints
// Failure Path: Phases not added if conditions false
// Business Rules: Title IV disbursement integrity; grade-level regulatory constraints
// Error Handling: Boolean short-circuiting guards nulls; logging earlier captured states
// Financial Impact: Prevents reductions and over-awards; excludes discount from COA constraint
// BOUNDARY CHECKS:
// Check: Grade-level constraint applicability
// Source: Institutional policy and federal grade-level cumulative limits
// Variables: term, standardOrSummerHeader, isLastAcy, program.undergraduate, isInNASuffix, thisPpIsProrated()
// Validation:
//   - Only include when appropriate per combined condition
// Edge Cases:
//   - term == null => treat as applicable
//   - Last ACY graduate programs excluded differently than undergrad
// Error Handling: N/A
// Impact: Controls aggregate limits application
// ARITHMETIC: N/A
optimizer
        .setEnsureFinalZeroAmountIsIneligible(false)
        .setCumulativeGradeLevelLimitApplicable(
                (!term || standardOrSummerHeader)
                        && (!isLastAcy || program.undergraduate)
                        && (!isLastAcy || !isInNASuffix)
                        && !thisPpIsProrated())
        .setFundingForPeriodFilter { fundCode, fundType -> fundCode != "DISCOUNT" }
        .setCompensationValue(0)
        .addPhase(thisPpIsDisbursed(), 1) {
            it.withGiven(disbursedAmount, "Keep disbursed disbursements")
        }

// CONDITIONALS:
// Check: Add phases only when not less than half time
// Variables: isLessThanHalfTime, term, standardOrSummerHeader, isSummerTrailerState, effectiveAcceptedAmount, isActiveSummerTerm
// Purpose: Gate optimizer phases for standard or summer cases, including acceptance cap for summer trailer logic.
// Logic:
//   - If not LTHT:
//       addPhase(!term, 2): non-term structure support
//       addPhase(term && standardOrSummerHeader && (!isSummerTrailerState || effectiveAcceptedAmount != null), 2) {
//         withPpMaximum(isSummerTrailerState ? effectiveAcceptedAmount : null, "Fund Acceptance")
//       }
//       addPhase(isSummerTrailerState && (isActiveSummerTerm || standardOrSummerHeader && effectiveAcceptedAmount == null), 3)
// Success Path: Phases applied to enforce acceptance cap when needed.
// Failure Path: No additional phases for LTHT or unmet conditions.
// Business Rules: Summer trailer requires documentation and acceptance threshold; standard terms respect acceptance when applicable.
// Error Handling: Null-safe checks, lazy booleans
// Financial Impact: Caps awards to accepted amount in trailer context, preventing over-award
// BOUNDARY CHECKS:
// Check: Presence of term, acceptance, and trailer conditions
// Source: AY/Term scheduling and document policy
// Variables: term, effectiveAcceptedAmount
// Validation:
//   - Ensure cap applied only when acceptance available or trailer logic mandates it
// Edge Cases:
//   - standardOrSummerHeader true with trailer false => no cap unless acceptance non-null
// Error Handling: N/A
// Impact: Prevents funding above acceptance in mandated cases
// ARITHMETIC: N/A
if (!isLessThanHalfTime) {
    optimizer
            .addPhase(!term, 2)
            .addPhase(term && standardOrSummerHeader && (!isSummerTrailerState || effectiveAcceptedAmount != null), 2) {
                it.withPpMaximum(isSummerTrailerState ? effectiveAcceptedAmount : null,"Fund Acceptance")
            }
            .addPhase(isSummerTrailerState && (isActiveSummerTerm || standardOrSummerHeader && effectiveAcceptedAmount == null), 3)
}

// CONDITIONALS:
// Check: Hard stop when program enrollment status is "X" (placeholder) or PP is canceled
// Variables: program.enrollmentStatus "X", pp.status "CANCELED" (note: syntax suggests missing operators; intended: ==)
// Purpose: Zero awards in ineligible or canceled PP
// Logic:
//   - If either condition, return awardInfo.withMaxAmount(0.0).withRetainedAmount(0.0)
// Success Path: Awards zeroed
// Failure Path: Continue processing
// Business Rules: No funds where enrollment invalid or PP canceled
// Error Handling: As written, expression may be syntactically invalid in Groovy due to missing '==' operators; expected platform script would compare strings.
// Financial Impact: Prevents disbursements to ineligible periods
// BOUNDARY CHECKS:
// Check: PP status must not be "CANCELED"
// Source: Disbursement/payment period lifecycle
// Variables: pp.status
// Validation: Equality check expected
// Edge Cases: N/A
// Error Handling: If corrected to proper operator, safe; otherwise runtime error. Logging not present here.
// Impact: Avoids improper funding
// ARITHMETIC: N/A
if (program.enrollmentStatus == "X" || pp.status == "CANCELED")
{
    return awardInfo.withMaxAmount(0.0).withRetainedAmount(0.0)
}

// CONDITIONALS:
// Check: Graduate program rule
// Variables: program.enrollmentStatus == "G", [packagedAmount(), disbursedAmount].max(), financialPlan.version > 1
// Purpose: For graduates, retain higher of packaged or disbursed to prevent reductions.
// Logic:
//   - If status "G" and financialPlan.version > 1, compute max of packagedAmount() and disbursedAmount (null-safe behavior depends on platform; null in list may error)
//   - Return awardInfo.withMaxAmount(max)
// Success Path: Max preserved
// Failure Path: Non-graduate continues
// Business Rules: Graduate loans often constrained differently; avoid reducing below prior packaged or actual disbursed
// Error Handling: If disbursedAmount null, ensure .max() handles null; platform may need null-safe list
// Financial Impact: Protects graduate-level aid commitments
// BOUNDARY CHECKS:
// Check: Minimum bound is zero if both null/zero
// Source: Prior plan/disbursement data
// Variables: packagedAmount(), disbursedAmount
// Validation: N/A
// Edge Cases: disbursedAmount null: ensure max handles gracefully
// Error Handling: N/A
// Impact: Avoids under-awarding
// ARITHMETIC:
// Operation: Maximum of two amounts
// Formula: max(packagedAmount, disbursedAmount)
// Variables:
//   - packagedAmount: BigDecimal/Double
//   - disbursedAmount: BigDecimal or null
// Steps:
//   1. Evaluate both lazily
//   2. Take maximum
// Business Context: Preserve higher commitment for graduate
// Result: Cap used for override
// Financial Impact: May increase or hold funding relative to actuals
if (program.enrollmentStatus == "G" && financialPlan.version > 1) {
    return awardInfo.withMaxAmount([packagedAmount(), disbursedAmount].max())
}

// CONDITIONALS:
// Check: Term-based logic: overlapping terms and R2T4 freeze
// Variables: program.term, primaryProgram.terms.getOverlappingWith(term), overlappingTerms.studentsTermStatuses.contains("WITHDRAWN"),
//           r2t4.getOverlappingWith(term).getWithProcessStatuses(["NOT_REQUIRED", "COMPLETED"]).getWithOldas(overlappingTerms.oldas),
//           nonCancelledDisbursementsInThisPp().ppMaxDisbursementAmount
// Purpose: When student term withdrawn and R2T4 indicates freeze-able state, freeze to PP max amount
// Logic:
//   - If program has term structure:
//     - Get overlapping terms with current term
//     - If any student term status WITHDRAWN:
//         - Retrieve R2T4 for overlapping terms with statuses NOT_REQUIRED or COMPLETED and matching OLDAs
//         - If not empty => Freeze: sum = PP max disb amount from non-canceled set; set both max and retained to sum
//         - Else: log not yet freezing
//     - If enrollmentStatus == "NOT_ATTENDING" => zero both amounts
// Success Path: Appropriate freeze or zero applied
// Failure Path: Continue to next conditions
// Business Rules: R2T4 completed/not required leads to freeze; NOT_ATTENDING zeroing to prevent future disbursements
// Error Handling: Multiple property/methods (primaryProgram, studentsTermStatuses, oldas) assume platform availability; logging assists
// Financial Impact: Prevents post-withdrawal changes inconsistent with R2T4, ensures compliance
// BOUNDARY CHECKS:
// Check: R2T4 process state must indicate freezing condition
// Source: R2T4 records and statuses
// Variables: r2t4OverlappingTerms.empty
// Validation:
//   - Only freeze when not empty and statuses in set
// Edge Cases:
//   - Multiple overlapping terms; OLDAs filter ensures exact alignment
//   - No PP disb in scope => ppMaxDisbursementAmount may be 0
// Error Handling: Null checks via chained calls assume platform returns empty collections
// Impact: Avoids incorrect recalculations after withdrawal
// ARITHMETIC:
// Operation: Freeze to PP Max Disbursement Amount
// Formula: sum = nonCancelledDisbursementsInThisPp().ppMaxDisbursementAmount
// Variables:
//   - sum: BigDecimal
// Steps:
//   1. Compute PP maximum for non-canceled items
//   2. Set as both max and retained
// Business Context: Lock-in amounts following R2T4 freeze conditions
// Result: Award override equal to frozen PP maximum
// Financial Impact: Prevents over-award and preserves compliance
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

// CONDITIONALS:
// Check: Freeze when loan period is in the past OR PP is prorated and disbursed
// Variables: log.debug(lp.endDate <= now, "LOAN PERIOD IN THE PAST"), thisPpIsProrated(), thisPpIsDisbursed(), disbursedAmount
// Purpose: Prevent changes to past LPs or to disbursed prorated PPs
// Logic:
//   - If LP ended (lp.endDate <= now) OR (PP prorated AND disbursed):
//       frozenAmount = disbursedAmount ?: 0.0
//       Set both max and retained to frozenAmount
// Success Path: Freeze applied
// Failure Path: Fall-through to return awardInfo unchanged
// Business Rules: Do not alter amounts for completed LPs; protect prorated disbursed PP
// Error Handling: log.debug returns boolean for condition; disbursedAmount null handled by coalescing
// Financial Impact: Stabilizes awards post-period and post-disbursement
// BOUNDARY CHECKS:
// Check: Date boundary at LP end
// Source: Loan period dates
// Variables: lp.endDate, now
// Validation:
//   - LP in the past if endDate <= now
// Edge Cases:
//   - EndDate equals today treated as past
// Error Handling: N/A
// Impact: Freezes amounts to avoid retroactive changes
// ARITHMETIC:
// Operation: Frozen amount coalescing
// Formula: frozenAmount = (disbursedAmount == null ? 0.0 : disbursedAmount)
// Variables:
//   - disbursedAmount: BigDecimal or null
//   - frozenAmount: BigDecimal/Double
// Steps:
//   1. If null => 0.0
//   2. Else use disbursedAmount
// Business Context: Ensure non-negative freeze
// Result: Override equals frozenAmount for both max and retained
// Financial Impact: Eliminates risk of negative or undefined freezes
if (log.debug(lp.endDate <= now, "LOAN PERIOD IN THE PAST")
        || (thisPpIsProrated() && thisPpIsDisbursed()))
{
    def frozenAmount = disbursedAmount == null ? 0.0 : disbursedAmount
    log.debug("FREEZING TO {}", frozenAmount)
    return awardInfo.withMaxAmount(frozenAmount).withRetainedAmount(frozenAmount)
}

// CONDITIONALS:
// Check: Fall-through default
// Variables: awardInfo
// Purpose: If no special conditions apply, return existing awardInfo unchanged.
// Logic: Return awardInfo
// Success Path: No override modifications
// Failure Path: N/A
// Business Rules: Default behavior preserves upstream optimization outcome
// Error Handling: N/A
// Financial Impact: Neutral; uses optimized result
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
return awardInfo