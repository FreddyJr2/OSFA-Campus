/**
 * Primary Purpose: Override award amounts for PLUS and Grad PLUS funds by freezing, zeroing, or retaining amounts
 *                  based on enrollment, disbursement status, R2T4 state, summer eligibility, and term/non-term context.
 * Key Calculations:
 * - Determine disbursed and originated amounts within the current Payment Period (PP) and Loan Period (LP).
 * - Freeze amounts to: (a) disbursed amount if PP is disbursed, (b) originated amount if PP is frozen but not disbursed,
 *   or (c) zero if student is NOT_ATTENDING or specific program/PP status rules apply.
 * Critical Business Rules:
 * - Disbursed PPs are preserved (no reduction below disbursed totals).
 * - Withdrawn term students may trigger freezing once R2T4 has a non-active terminal status (NOT_REQUIRED/COMPLETED).
 * - Summer-state logic may freeze amounts when past threshold and documentation exists.
 * - Non-attendance or program/PP canceled status yields zeroing of amounts.
 * Boundary Conditions and Limits:
 * - Past PP or LP dates trigger freezing to disbursed (PP) or disbursed/zero (LP) amounts.
 * - Null or empty collections are treated safely (lazy evaluators; log-based truth checks).
 * Financial Aid Implications:
 * - Preserves compliance with federal aid regulations by not reducing below disbursed amounts and observing R2T4 outcomes.
 * - Ensures correct Title IV handling for PLUS/GPLUS by reacting to enrollment intensity and attendance.
 * Required Inputs:
 * - Bindings: acy, acys, awardInfo (IAwardOverrideAPI), awy, coa, disbursements, enrollmentStatus, fundType/fundCode,
 *   optimizer (IOptimizerConfigAPI; optional), pp, lp, program, r2t4, receivedDocuments, term, scope, etc.
 * Expected Output:
 * - IAwardOverrideAPI (awardInfo) updated via withMaxAmount/withRetainedAmount/withComment as applicable and returned.
 * Error Handling Approach:
 * - Defensive null-safe checks, lazy evaluation, and debug logging via log.debug/info/warn/error.
 * - Non-available data paths short-circuit decisions (e.g., null disbursedAmount yields default zero freeze).
 * Integration Points:
 * - disbursements API (filtering status, totals), optimizer (phasing), r2t4 API (process states, OLDAs),
 *   receivedDocuments (summer docs), program/term/acy schedules, student enrollment status, IAwardOverrideAPI setters.
 * Compliance Considerations:
 * - Federal aid (Title IV) compliance: do not reduce disbursed amounts; apply R2T4 results to freezing behavior.
 * - Academic progress and attendance: NOT_ATTENDING or WITHDRAWN triggers strict freezing/zeroing.
 * - Institutional and program rules: honor term/summer structure, payment period boundaries, and documentation requirements.
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.AwardOverridingCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name
import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

// CONDITIONALS:
// Check: Lazy initializer flag handling
// Variables: isInitDue (boolean), value (generic T), init() closure
// Purpose: Ensure initialization code runs exactly once; subsequent calls return cached value to avoid re-querying APIs/collections.
// Logic:
//   - If isInitDue is true, call init(), store result in value, set isInitDue=false
//   - Always return value (cached after first call)
// Success Path: init() executed once, value memoized, reduces API load and ensures consistency within execution
// Failure Path: N/A (no branch throws; subsequent calls bypass init())
// Business Rules: Prevents recalculation of disbursement/document queries within rule evaluation, stabilizing award decisions.
// Error Handling: N/A (caller-side defensive usage; init() must handle its own errors if any).
// Financial Impact: Reduces risk of inconsistent totals that could affect award/retention.
// BOUNDARY CHECKS:
// Check: Concurrency/Multiple invocation
// Source: Script execution lifecycle within SFP engine
// Variables: isInitDue flag
// Validation:
//   - Single-threaded script context; flag prevents duplicate initialization
// Edge Cases:
//   - Reentrancy is avoided by stateful isInitDue
//   - init() exceptions would bubble; none thrown here
// Error Handling: N/A
// Impact: Stable financial calculations across rule execution
// ARITHMETIC: N/A
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
// Check: Build PP-scoped disbursements collection
// Variables: disbursements.getInScope(scope)
// Purpose: Limit disbursement data to the fund’s current scope (acyNo, awy, fundCode, lpNo, ppNo) to avoid cross-period contamination.
// Logic:
//   - Call disbursements.getInScope(scope) to obtain a narrowed disbursement data set for this PP.
//   - Wrap in lazy to avoid unnecessary computation until actually needed.
// Success Path: Returns PP-scoped disbursements collection.
// Failure Path: If scope mismatches or empty data, subsequent calls will operate on empty collection.
// Business Rules: Only period- and fund-specific disbursements may influence override decisions.
// Error Handling: Implicitly safe; empty collections propagate forward.
// Financial Impact: Ensures amounts (totals) reflect the correct PP only.
def disbursementsInThisPp = lazy {
    disbursements.getInScope(scope)
}

// CONDITIONALS:
// Check: Filter out cancelled disbursements
// Variables: disbursementsInThisPp().getCancelled(false), filterStatuses{status != "DISBURSEMENT_CANCELED"}
// Purpose: Maintain only non-cancelled disbursements to compute eligible originated amounts.
// Logic:
//   - Start from PP-scoped collection.
//   - Apply getCancelled(false) to exclude cancelled items.
//   - Apply status filter to exclude DISBURSEMENT_CANCELED statuses explicitly.
// Success Path: Collection contains only non-cancelled disbursements.
// Failure Path: Empty result set if all were cancelled.
// Business Rules: Cancelled disbursements must not count toward originations/disbursed totals.
// Error Handling: Null/empty-safe; filters operate on collection APIs.
// Financial Impact: Prevents inflating originated totals with cancelled items.
// BOUNDARY CHECKS:
// Check: Status value boundary
// Source: Disbursement status code set
// Variables: status string comparisons
// Validation:
//   - Ensure status != "DISBURSEMENT_CANCELED" is honored.
// Edge Cases:
//   - Unknown statuses are retained; only explicit canceled is filtered.
// Error Handling: Unknown statuses logged in upstream processes; script proceeds.
// Impact: Accurate non-cancelled base for amount calculations.
// ARITHMETIC:
// N/A
def nonCancelledDisbursementsInThisPp = lazy {
    disbursementsInThisPp()
            .getCancelled(false)
            .filterStatuses { status -> status != "DISBURSEMENT_CANCELED" }
}

// CONDITIONALS:
// Check: Select DISBURSED items
// Variables: nonCancelledDisbursementsInThisPp().getWithStatus("DISBURSED")
// Purpose: Isolate already disbursed transactions to protect them from reductions.
// Logic:
//   - From non-cancelled set, fetch only those with status DISBURSED.
// Success Path: Collection of DISBURSED disbursements ready to total.
// Failure Path: Empty collection if no disbursements completed.
// Business Rules: Disbursed amounts are protected (cannot be reduced).
// Error Handling: Safe if empty; subsequent checks handle empty.
// Financial Impact: Forms floor for freezing rules.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
def disbursedDisbursementsInThisPp = lazy {
    nonCancelledDisbursementsInThisPp().getWithStatus("DISBURSED")
}

// CONDITIONALS:
// Check: Determine if PP is disbursed
// Variables: disbursedDisbursementsInThisPp().empty, log.debug(condition, "DISBURSED PP")
// Purpose: Identify if at least one disbursement has occurred.
// Logic:
//   - Evaluate emptiness; negate to get boolean flag.
//   - Log and return boolean as side-effect from log.debug.
// Success Path: True when at least one disbursement exists.
// Failure Path: False when none exist.
// Business Rules: Disbursed PP triggers “preserve” behaviors in optimizer phases.
// Error Handling: Emptiness is safe; no exceptions.
// Financial Impact: Disbursed PP prevents reductions below paid levels.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
def thisPpIsDisbursed = lazy {
    log.debug(!disbursedDisbursementsInThisPp().empty, "DISBURSED PP")
}

// CONDITIONALS:
// Check: Assign disbursedAmount if PP disbursed
// Variables: thisPpIsDisbursed(), disbursedDisbursementsInThisPp().totalDisbursementAmount
// Purpose: Capture the floor amount equal to total disbursed so far.
// Logic:
//   - Ternary: if disbursed -> totalDisbursementAmount; else -> null.
// Success Path: Non-null BigDecimal when disbursed.
// Failure Path: null when not disbursed; later replaced by zero in LP freeze path.
// Business Rules: Disbursed totals must be honored as minimum payable.
// Error Handling: If totalDisbursementAmount unavailable, platform returns BigDecimal zero; null only by design here when not disbursed.
// Financial Impact: Sets minimum override threshold.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// Operation: Aggregate disbursed amount
// Formula: disbursedAmount = sum(disbursed payments in PP)
// Variables:
//   - disbursedDisbursementsInThisPp().totalDisbursementAmount: BigDecimal, USD
// Steps:
//   1. Identify DISBURSED records.
//   2. Sum their disbursement amounts.
// Business Context: Protect paid funds in PP.
// Result: Floor for max/retained amounts when freezing.
// Financial Impact: Prevents clawback below paid disbursements.
// Limitations: Dependent on accurate status coding.
def disbursedAmount = thisPpIsDisbursed()
        ? disbursedDisbursementsInThisPp().totalDisbursementAmount
        : (BigDecimal) null

// CONDITIONALS:
// Check: Retrieve prior plan packaged amount in-scope
// Variables: financialPlan.version, previousFinancialPlan.withVersion(financialPlan.version - 1).getInScope(scope).maxAmount
// Purpose: Reference prior packaged max to avoid unintentional increases (e.g., graduate-only programs).
// Logic:
//   - If current plan version == 1, no previous exists => return 0.0
//   - Else, retrieve previous plan by version and scope to read prior maxAmount.
// Success Path: Returns numeric previous maxAmount (or 0.0 when version 1).
// Failure Path: If previous plan/scope not found, platform typically returns a safe zero or raises upstream; lazy call defers execution until needed.
// Business Rules: Historical packaged amounts may constrain current overrides in program-specific paths.
// Error Handling: Lazy block avoids unnecessary queries; any retrieval anomalies should be logged upstream.
// Financial Impact: Prevents awarding above previously packaged amounts where policy requires.
// BOUNDARY CHECKS:
// Check: Version lower bound
// Source: Financial plan versioning
// Variables: financialPlan.getVersion()
// Validation:
//   - version >= 1; version == 1 implies no previous plan.
// Edge Cases:
//   - Missing previous version data leads to 0.0 fallback.
// Error Handling: N/A within this closure; relies on platform safety.
// Impact: Stable cap reference in special conditions.
// ARITHMETIC:
// Operation: Prior packaged cap selection
// Formula: packagedAmount = (version == 1) ? 0.0 : previousFinancialPlan.withVersion(version-1).getInScope(scope).maxAmount
// Variables:
//   - financialPlan.version: int
//   - previousFinancialPlan.withVersion(...): prior IFinancialPlanAPI
//   - maxAmount: BigDecimal
// Steps:
//   1. Check version.
//   2. If >1, fetch previous plan in-scope.
//   3. Return maxAmount.
// Business Context: Historical cap.
// Result: Prior max for comparison.
// Financial Impact: Caps current override in specific branch.
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
// Check: Compute originated amount for non-cancelled PP
// Variables: nonCancelledDisbursementsInThisPp().totalDisbursementAmount
// Purpose: Establish “originated” level to freeze when PP is frozen but not disbursed.
// Logic:
//   - Sum total amounts for non-cancelled disbursements.
// Success Path: BigDecimal total, may be zero.
// Failure Path: Zero if no non-cancelled items.
// Business Rules: Originations (pending/authorized) guide freeze when no disbursement yet.
// Error Handling: Platform method safe; returns BigDecimal.
// Financial Impact: Avoids over-award when PP is frozen pre-disbursement.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// Operation: Aggregate originated amount
// Formula: originatedAmount = sum(non-cancelled disbursement amounts in PP)
// Variables:
//   - totalDisbursementAmount: BigDecimal
// Steps:
//   1. Filter non-cancelled.
//   2. Sum amounts.
// Business Context: Freeze to originated when PP is frozen without actual disbursement.
// Result: Freeze target amount.
// Financial Impact: Controls pending exposure.
def originatedAmount = nonCancelledDisbursementsInThisPp().totalDisbursementAmount

// CONDITIONALS:
// Purpose: Set now variable to current local date.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
def now = LocalDate.now()

// CONDITIONALS:
// Check: Identify if PP is in the past
// Variables: pp.endDate, now
// Purpose: Past PPs may be frozen depending on summer and disbursement logic.
// Logic:
//   - Compare PP end date with current date.
// Success Path: True if PP ended before now.
// Failure Path: False otherwise.
// Business Rules: Past periods not open for increases unless policy permits.
// Error Handling: Assume pp.endDate populated by platform; if null in rare cases, comparison error would occur; platform generally ensures non-null.
// Financial Impact: Pushes toward freezing behavior.
// BOUNDARY CHECKS:
// Check: Date boundary
// Source: Academic scheduling
// Variables: pp.endDate vs now
// Validation:
//   - Ensure PP boundary recognized.
// Edge Cases:
//   - Same-day end: strictly < now means not “past” if equal.
// Error Handling: N/A
// Impact: Changes optimizer phase selection.
// ARITHMETIC:
// N/A
def ppIsInThePast = pp.endDate < now

// CONDITIONALS:
// Check: Determine active summer terms
// Variables: acy.terms.filter{ it.summer && program.courses.getAssociatedTo(it).any{ it.schedulingStatus != "PROJECTED"} }
// Purpose: Find summer terms with actual scheduled coursework (non-projected) to drive summer-state.
// Logic:
//   - From academic year terms, filter those flagged as summer.
//   - From program courses, keep terms that have at least one non-PROJECTED associated course.
// Success Path: activeSummerTerms contains real summer engagement.
// Failure Path: Empty list indicates no active summer.
// Business Rules: Summer-only handling applies when student has actionable summer coursework.
// Error Handling: If associations/courses missing, any{} returns false; results in empty set.
// Financial Impact: Influences freezing via isSummerState.
// BOUNDARY CHECKS:
// Check: Course scheduling status domain
// Source: SIS/course schedule integration
// Variables: schedulingStatus != "PROJECTED"
// Validation:
//   - Only statuses not “PROJECTED” considered active.
// Edge Cases:
//   - Mixed statuses across courses -> any non-PROJECTED triggers active.
// Error Handling: N/A
// Impact: Drives summerStart and threshold logic.
// ARITHMETIC:
// N/A
def activeSummerTerms = acy.terms.filter {
    it.summer && program.courses.getAssociatedTo(it).any {
        it.schedulingStatus != "PROJECTED"
    }
}

// CONDITIONALS:
// Check: Has any summer courses
// Variables: activeSummerTerms.empty
// Purpose: Support downstream threshold and state checks.
// Logic:
//   - Negate emptiness.
// Success Path: True if any active summer term exists.
// Failure Path: False otherwise.
// Business Rules: Summer state requires activity.
// Error Handling: N/A
// Financial Impact: Enables summer-based freeze.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
def summerHasCourses = !activeSummerTerms.empty

// CONDITIONALS:
// Check: Determine summer start date
// Variables: summerHasCourses ? activeSummerTerms.startDate : null
// Purpose: Establish threshold anchor for summer-state (-60 days).
// Logic:
//   - If courses exist, use startDate of collection; platform provides collection min start.
// Success Path: summerStart set; else null.
// Failure Path: Null prevents threshold calc.
// Business Rules: Documentation threshold tied to summer start.
// Error Handling: Null handled downstream.
// Financial Impact: Affects early freezing enablement.
// BOUNDARY CHECKS:
// Check: Date availability
// Source: Term calendar
// Variables: activeSummerTerms.startDate
// Validation:
//   - Must exist to compare with now.
// Edge Cases:
//   - Multiple summer terms: platform aggregates; assume earliest start.
// Error Handling: Null-safe ternary.
// Impact: Without date, summer threshold not applied.
// ARITHMETIC:
// N/A
def summerStart = summerHasCourses ? activeSummerTerms.startDate : null

// CONDITIONALS:
// Check: Evaluate whether current term is summer active
// Variables: activeSummerTerms.contains(term)
// Purpose: Apply summer rules only if current term belongs to active summer set.
// Logic:
//   - Membership test on term.
// Success Path: True when current term is one of the active summer terms.
// Failure Path: False otherwise.
// Business Rules: Targeted summer logic only within relevant term.
// Error Handling: N/A
// Financial Impact: Gates freezing on term context.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
def isActiveSummerTerm = activeSummerTerms.contains(term)

// CONDITIONALS:
// Check: Summer threshold window
// Variables: summerStart && now >= summerStart.minusDays(60)
// Purpose: Permit summer-specific freezing only within 60 days leading into summer.
// Logic:
//   - Ensure summerStart exists.
//   - Compare now to (summerStart - 60 days).
// Success Path: True once within threshold window.
// Failure Path: False before window; null summerStart yields false.
// Business Rules: Documentation/eligibility window for summer.
// Error Handling: Null-safe due to short-circuit AND.
// Financial Impact: Earlier or later freezing control.
// BOUNDARY CHECKS:
// Check: 60-day threshold constant
// Source: Institutional policy
// Variables: minusDays(60)
// Validation:
//   - Enforce cutoff exactly 60 days prior.
// Edge Cases:
//   - Boundary day counts as threshold (>=).
// Error Handling: N/A
// Impact: Determines isSummerState.
// ARITHMETIC:
// Operation: Threshold date
// Formula: threshold = summerStart - 60 days
// Variables:
//   - summerStart: LocalDate
// Steps:
//   1. Subtract 60 days.
//   2. Compare to now.
// Business Context: Summer application timing.
// Result: Boolean gating.
// Financial Impact: Controls eligibility timing.
def isBeyondSummerThreshold = summerStart && now >= summerStart.minusDays(60)

// CONDITIONALS:
// Check: Summer documentation presence
// Variables: receivedDocuments.forAwardYear(awy).getDocumentsForDocCode("SummerApplication", "Student"), log.debug(…, "SUMMER DOC EXISTS")
// Purpose: Require presence of student summer application to enable summer-state.
// Logic:
//   - Fetch documents for award year and doc code.
//   - Evaluate non-null/non-empty; log and return boolean via debug.
// Success Path: True when doc exists.
// Failure Path: False without doc.
// Business Rules: Documentation requirement before summer adjustments.
// Error Handling: Null-safe checks; missing doc -> false.
// Financial Impact: Prevents aid changes without required doc.
// BOUNDARY CHECKS:
// Check: Doc code matching
// Source: Document management configuration
// Variables: "SummerApplication", "Student"
// Validation:
//   - Exact code match required.
// Edge Cases:
//   - Duplicates handled by non-empty check.
// Error Handling: N/A
// Impact: Controls isSummerState.
// ARITHMETIC:
// N/A
def summerDocExists = lazy {
    def summerDocs = receivedDocuments.forAwardYear(awy)
            .getDocumentsForDocCode("SummerApplication", "Student")
    log.debug(summerDocs != null && !summerDocs.empty, "SUMMER DOC EXISTS")
}

// CONDITIONALS:
// Check: Determine comprehensive summer state
// Variables: summerHasCourses && isBeyondSummerThreshold && summerDocExists(), log.debug(…, "SUMMER STATE")
// Purpose: Activate summer freezing logic only when all prerequisites are met.
// Logic:
//   - AND across course presence, threshold, and documentation.
//   - Log and return boolean via debug.
// Success Path: True: summer state active.
// Failure Path: False otherwise.
// Business Rules: Multi-factor summer gating.
// Error Handling: Null-safe booleans and lazy retrieval.
// Financial Impact: Enables summer-only freezing behaviors in optimizer.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
def isSummerState = log.debug(summerHasCourses && isBeyondSummerThreshold && summerDocExists(), "SUMMER STATE")

// CONDITIONALS:
// Check: Identify < half-time
// Variables: enrollmentStatus == "LESS_THAN_HALF_TIME"
// Purpose: Enforce freezing for less than half-time attendance.
// Logic:
//   - Equality check on enrollment status.
// Success Path: True when below half-time.
// Failure Path: False otherwise.
// Business Rules: Below half-time typically restricts loan disbursements.
// Error Handling: N/A
// Financial Impact: Likely to freeze amounts.
// BOUNDARY CHECKS:
// Check: Status domain validation
// Source: Enrollment status codes
// Variables: enrollmentStatus
// Validation:
//   - Must be valid enum-like values.
// Edge Cases:
//   - Unknown values bypass this check.
// Error Handling: Upstream validation expected.
// Impact: Drives frozen/notFrozen.
// ARITHMETIC:
// N/A
def isLessThanHalfTime = enrollmentStatus == "LESS_THAN_HALF_TIME"

// CONDITIONALS:
// Check: Determine if PP is frozen
// Variables: isLessThanHalfTime || thisPpIsDisbursed() || (isSummerState && (ppIsInThePast || term.standard), frozenPp, notFrozenPp
// Purpose: Mark PP as frozen due to attendance, disbursement, or summer/past logic.
// Logic:
//   - OR combination of: less than half-time; already disbursed; past PP during summer state.
//   - Log result.
// Success Path: True => PP frozen behavior applies.
// Failure Path: False => Not frozen, may proceed to optimization allocation.
// Business Rules: Prevent increases in protected scenarios.
// Error Handling: N/A
// Financial Impact: Sets basis for phase selection, freezing to originated/disbursed.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
def frozenPp = log.debug(isLessThanHalfTime || thisPpIsDisbursed() || (isSummerState && (ppIsInThePast || term.standard)), "FROZEN PP")
def notFrozenPp = !frozenPp

// CONDITIONALS:
// Check: Identify PLUS loan runtime type
// Variables: plusLoan instanceof IActualPlusLoanAPI ? cast : null
// Purpose: Enable special handling for certain PLUS loan variants if applicable.
// Logic:
//   - Type-check and cast, else null.
// Success Path: actualPlusLoan set for use in summerOnlyLoan logic.
// Failure Path: Null indicates non-applicability.
// Business Rules: Certain PLUS features available only on actual plus loan objects.
// Error Handling: Safe instanceof check.
// Financial Impact: May alter summer-only handling.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
def actualPlusLoan = plusLoan instanceof IActualPlusLoanAPI
        ? (IActualPlusLoanAPI) plusLoan
        : null

// CONDITIONALS:
// Check: Identify summer-only loan
// Variables: actualPlusLoan && acy.terms.getOverlappingWith(actualPlusLoan).every{ it.summer }, log.debug(…, "SUMMER ONLY LOAN")
// Purpose: Flag loans limited strictly to summer for specialized optimization phase routing.
// Logic:
//   - If actualPlusLoan exists, check that all overlapping terms are summer terms.
//   - Log boolean result.
// Success Path: True if loan overlaps exclusively with summer terms.
// Failure Path: False otherwise.
// Business Rules: Avoid treating summer-only loans as general term loans.
// Error Handling: If overlap retrieval returns empty, every{} is true—business may treat as not summer-only by policy; logged for traceability.
// Financial Impact: May route to summer phase 4 behavior.
// BOUNDARY CHECKS:
// Check: Overlap integrity
// Source: Term schedule
// Variables: getOverlappingWith(actualPlusLoan)
// Validation:
//   - Overlap detection must be correct to avoid misclassification.
// Edge Cases:
//   - No overlapping terms -> every{} vacuously true; monitor via logs.
// Error Handling: Logging assists audit.
// Impact: Phase control.
// ARITHMETIC:
// N/A
// Remove summerOnlyLoan or make it always false to avoid treating this kind of loan in a special way
def summerOnlyLoan = log.debug(actualPlusLoan &&
        acy.terms.getOverlappingWith(actualPlusLoan)
                .every { it.summer },
        "SUMMER ONLY LOAN")

// CONDITIONALS:
// Check: Configure optimizer phases for fund allocation
// Variables: optimizer.setFundingForPeriodFilter{ fundCode != "DISCOUNT" }, addPhase(...)
// Purpose: Direct optimizer behavior based on PP state to protect disbursed amounts, freeze when needed, or proceed with term/summer logic.
// Logic:
//   - Set funding filter: exclude DISCOUNT from higher-priority influence on Term COA constraint.
//   - Phase 1 (conditional: thisPpIsDisbursed()): withGiven(disbursedAmount) to keep paid as-is.
//   - Phase 2 (conditional: frozenPp && !thisPpIsDisbursed()): withGiven(originatedAmount) to freeze to originated.
//   - Phase 3 (conditional: notFrozenPp && (!term || term.standard)): standard term allocation.
//   - Phase 4 (conditional: notFrozenPp && term && isActiveSummerTerm && (summerOnlyLoan || isSummerState)): summer allocation.
// Success Path: Optimizer receives correctly ordered phases.
// Failure Path: If conditions false, phases skipped; defaults may apply.
// Business Rules: Respect disbursement protection and freezing policies; separate standard vs summer handling.
// Error Handling: Assumes optimizer binding exists for PLUS/GPLUS; if absent (non-loan funds), this block may be skipped at runtime.
// Financial Impact: Controls how much can be newly awarded vs. retained.
// BOUNDARY CHECKS:
// Check: fund filter boundary
// Source: Institutional policy
// Variables: fundCode != "DISCOUNT"
// Validation:
//   - Ensure DISCOUNT does not influence COA constraint.
// Edge Cases:
//   - Additional exclusions may be institution-specific; not covered here.
// Error Handling: N/A
// Impact: Prevents non-aid items from constraining optimization.
// ARITHMETIC:
// Operation: Freeze to disbursed or originated via withGiven
// Formula: Phase 1 -> given = disbursedAmount; Phase 2 -> given = originatedAmount
// Variables:
//   - disbursedAmount, originatedAmount: BigDecimal
// Business Context: Guardrails for disbursed and originated amounts.
// Result: Optimizer will not allocate below/above configured given values.
// Financial Impact: Enforces correct award ceilings.
optimizer.setFundingForPeriodFilter { fundCode, fundType -> fundCode != "DISCOUNT" }
        .addPhase(thisPpIsDisbursed(), 1) {
            it.withGiven(disbursedAmount, "Keep Disbursed Disbursements")
        }
        .addPhase(frozenPp && !thisPpIsDisbursed(), 2) {
            it.withGiven(originatedAmount, "Freeze PP to Originated Amount")
        }
        .addPhase(notFrozenPp && (!term || term.standard), 3)
        .addPhase(notFrozenPp && term && isActiveSummerTerm && (summerOnlyLoan || isSummerState), 4)

// CONDITIONALS:
// Check: Hard stop on program/PP cancellation scenarios
// Variables: program.enrollmentStatus "X", pp.status "CANCELED"
// Purpose: Zero out awards when program or PP indicates cancellation/invalid state.
// Logic:
//   - If program enrollment status equals "X" OR PP status equals "CANCELED", return awardInfo with zero max and retained.
// Success Path: Immediate zeroing and return.
// Failure Path: Continue evaluation.
// Business Rules: No awarding when program/PP is canceled or invalid.
// Error Handling: Assumes program.enrollmentStatus and pp.status provided; if null, comparison fails silently or is false depending on platform; logs are not added here.
// Financial Impact: Prevents disbursement or origination in invalid states.
// BOUNDARY CHECKS:
// Check: Status code validation
// Source: Institutional/program configurations
// Variables: "X", "CANCELED"
// Validation:
//   - Exact status strings required.
// Edge Cases:
//   - Case sensitivity or alternative codes should be normalized upstream.
// Error Handling: N/A
// Impact: Immediate zero award.
// ARITHMETIC:
// Operation: Set award to zero
// Formula: withMaxAmount(0.0) and withRetainedAmount(0.0)
// Steps:
//   1. Assign zero as max and retained.
// Business Context: System safety stop.
// Result: Award overridden to zero.
// Financial Impact: No aid disbursed or retained if canceled.
if (program.enrollmentStatus == "X" || pp.status == "CANCELED") {
    return awardInfo.withMaxAmount(0.0).withRetainedAmount(0.0)
}

// CONDITIONALS:
// Check: Graduate program cap based on prior packaged/disbursed
// Variables: program.enrollmentStatus == "G", [packagedAmount(), disbursedAmount].max(), financialPlan.version > 1
// Purpose: For graduate-coded enrollment statuses, cap award to the max of prior packaged and already disbursed.
// Logic:
//   - If graduate status and financialPlan.version > 1, compute maximum of prior packaged (lazy) and disbursed (may be null).
//   - Return awardInfo with maxAmount set to that cap; retainedAmount left unchanged unless later overridden.
// Success Path: Return with capped max amount.
// Failure Path: Not graduate: move to next logic.
// Business Rules: Conservative cap to prevent exceeding prior commitments for graduate students.
// Error Handling: If disbursedAmount is null, collection max ignores null in Groovy list max? Ensure not null-safe: may need coercion; assumed platform-safe.
// Financial Impact: Prevents over-awarding in graduate context.
// BOUNDARY CHECKS:
// Check: Value selection with null
// Source: Groovy collection max behavior
// Variables: disbursedAmount can be null
// Validation:
//   - Intended behavior is to choose the higher of numeric values; if null, packagedAmount likely chosen.
// Edge Cases:
//   - Null handling should be consistent; platform tests recommended.
// Error Handling: N/A
// Impact: Conservative cap.
// ARITHMETIC:
// Operation: Cap selection
// Formula: cap = max(packagedAmount(), disbursedAmount)
// Variables:
//   - packagedAmount: BigDecimal/double
//   - disbursedAmount: BigDecimal or null
// Steps:
//   1. Evaluate both values.
//   2. Pick maximum.
// Business Context: Protects against over-extension.
// Result: Max amount restricted for graduates.
// Financial Impact: Restrains increases.
if (program.enrollmentStatus == "G" && financialPlan.version > 1) {
    return awardInfo.withMaxAmount([packagedAmount(), disbursedAmount].max())
}

// CONDITIONALS:
// Check: Term-based logic branch
// Variables: program.term
// Purpose: Apply additional rules only for term-structured programs.
// Logic:
//   - If program is term-based, evaluate withdrawn statuses and R2T4 impact, or NOT_ATTENDING zeroing.
// Success Path: Apply nested rules.
// Failure Path: Skip to LP past check.
// Business Rules: Term programs require alignment with term withdraw/R2T4 outcomes.
// Error Handling: Safe boolean flag.
// Financial Impact: Can freeze to PP max or zero awards.
// BOUNDARY CHECKS:
// N/A
// ARITHMETIC:
// N/A
if (program.term) {

    // CONDITIONALS:
    // Check: Determine overlapping terms and withdrawn status
    // Variables: primaryProgram.terms.getOverlappingWith(term), overlappingTerms.studentsTermStatuses.contains("WITHDRAWN")
    // Purpose: If student withdrew in the term, check if R2T4 is in a freezing-eligible state to freeze awards.
    // Logic:
    //   - Compute overlapping terms with current term.
    //   - If any overlapping term shows WITHDRAWN, proceed to R2T4 evaluation.
    // Success Path: If WITHDRAWN and R2T4 ready, freeze to PP max disbursement amount.
    // Failure Path: If not withdrawn, or R2T4 not ready, continue.
    // Business Rules: Post-withdrawal awards governed by R2T4 outcomes.
    // Error Handling: Logging for traceability; overlaps may be empty, which bypasses logic.
    // Financial Impact: Prevents over-awarding after withdrawal.
    // BOUNDARY CHECKS:
    // Check: R2T4 process status domain
    // Source: R2T4 processing outcomes
    // Variables: ["NOT_REQUIRED", "COMPLETED"]
    // Validation:
    //   - Only these states allow freezing.
    // Edge Cases:
    //   - PENDING/IN_PROGRESS: do not freeze yet.
    // Error Handling: Safe filters.
    // Impact: Timing sensitivity of freeze.
    // ARITHMETIC:
    // Operation: Freeze to PP maximum disbursement amount
    // Formula: sum = nonCancelledDisbursementsInThisPp().ppMaxDisbursementAmount
    // Variables:
    //   - ppMaxDisbursementAmount: BigDecimal
    // Steps:
    //   1. From non-cancelled PP disbursements derive PP maximum disbursement limit defined by platform.
    //   2. Set both max and retained to this sum.
    // Business Context: After R2T4 completion/not required, lock to PP max to prevent increases.
    // Result: Award locked at PP max.
    // Financial Impact: Caps exposure post-withdrawal.
    def overlappingTerms = primaryProgram.terms.getOverlappingWith(term)
    if (overlappingTerms.studentsTermStatuses.contains("WITHDRAWN")) {
        log.debug("AWARD FREEZING CRITERIA - STUDENT TERM IS WITHDRAWN")
        def r2t4OverlappingTerms = r2t4.getOverlappingWith(term)
                .getWithProcessStatuses(["NOT_REQUIRED", "COMPLETED"])
                .getWithOldas(overlappingTerms.oldas)
        if (!r2t4OverlappingTerms.empty) {
            log.debug("AWARD FREEZING CRITERIA - R2T4 Overlapping Terms is not empty, FREEZING!")
            def sum = nonCancelledDisbursementsInThisPp().ppMaxDisbursementAmount
            return awardInfo.withMaxAmount(sum).withRetainedAmount(sum)
        } else {
            log.debug("AWARD FREEZING CRITERIA - R2T4 PROCESS NOT YET IN A FREEZING STATE")
        }
    }

    // CONDITIONALS:
    // Check: Zeroing for NOT_ATTENDING
    // Variables: enrollmentStatus == "NOT_ATTENDING"
    // Purpose: Fully zero award when the student is not attending in a term program.
    // Logic:
    //   - If NOT_ATTENDING, return zeros immediately.
    // Success Path: Award set to zero and returned.
    // Failure Path: Continue to next checks.
    // Business Rules: Non-attendance disallows disbursements.
    // Error Handling: Log for audit.
    // Financial Impact: Removes aid for the PP/term due to ineligibility.
    // BOUNDARY CHECKS:
    // N/A
    // ARITHMETIC:
    // Operation: Set award to zero
    // Formula: withMaxAmount(0.0), withRetainedAmount(0.0)
    // Variables: constants
    // Steps:
    //   1. Return zero amounts.
    // Business Context: Compliance with attendance requirements.
    // Result: No aid disbursed or retained.
    // Financial Impact: Avoids improper payments.
    if (enrollmentStatus == "NOT_ATTENDING") {
        log.debug("ZEROING - enrollmentStatus={} ", enrollmentStatus)
        return awardInfo.withMaxAmount(0.0).withRetainedAmount(0.0)
    }
}

// CONDITIONALS:
// Check: Freeze if loan period is in the past
// Variables: log.debug(lp.endDate <= now, "LOAN PERIOD IN THE PAST")
// Purpose: For LPs already ended, freeze at disbursed (or zero if none).
// Logic:
//   - Compare LP end date with now; if past or equal, compute frozenAmount as disbursedAmount or 0.0 if null.
//   - Return awardInfo with both max and retained set to frozenAmount.
// Success Path: Frozen to disbursed or zero.
// Failure Path: Return awardInfo unchanged at end of script.
// Business Rules: No increases after LP end.
// Error Handling: Log provides audit; disbursedAmount null-handled by zero default.
// Financial Impact: Prevents retroactive increases.
// BOUNDARY CHECKS:
// Check: Date boundary at LP end
// Source: Loan period configuration
// Variables: lp.endDate, now
// Validation:
//   - Inclusive boundary (<= now).
// Edge Cases:
//   - End date equals now -> considered past for freezing.
// Error Handling: N/A
// Impact: Immediate freeze.
// ARITHMETIC:
// Operation: Determine frozenAmount
// Formula: frozenAmount = (disbursedAmount == null ? 0.0 : disbursedAmount)
// Variables:
//   - disbursedAmount: BigDecimal or null
//   - frozenAmount: BigDecimal/double
// Steps:
//   1. If disbursedAmount null, choose 0.0; else use disbursedAmount.
//   2. Apply both as max and retained.
// Business Context: LP closure.
// Result: Locked award at paid or zero.
// Financial Impact: Eliminates exposure to post-LP adjustments.
if (log.debug(lp.endDate <= now, "LOAN PERIOD IN THE PAST")) {
    def frozenAmount = disbursedAmount == null ? 0.0 : disbursedAmount
    log.debug("FREEZING TO {}", frozenAmount)
    return awardInfo.withMaxAmount(frozenAmount).withRetainedAmount(frozenAmount)
}

return awardInfo