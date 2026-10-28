/**
 * Primary Purpose: Evaluate the maximum projected award amount for a Non-Federal Resource (NFR) period, considering already disbursed amounts and previously packaged amounts within the Oracle SFP Platform context.
 * Functionality Summary:
 *  - Inspects disbursements for the current Academic Year (acyNo), Award Year (awy), and NFR payment period (period.number).
 *  - Excludes cancelled and DISBURSEMENT_CANCELED records from consideration.
 *  - Detects whether the payment period (PP) has a DISBURSED transaction.
 *  - Retrieves prior packaged maximum amounts from the previous financial plan version where applicable.
 *  - For graduate enrollment (program.enrollmentStatus == "G"), returns the maximum of packaged vs. disbursed amount; otherwise returns a static placeholder (100).
 * Key Calculations:
 *  - Maximum of [packagedAmount, disbursedAmount] when graduate.
 *  - Determination of disbursedAmount conditioned on the PP being disbursed.
 * Critical Business Rules:
 *  - Cancelled disbursements do not count toward maximum projected award.
 *  - DISBURSED status indicates realized funds and must cap or define projected maximum for the PP.
 *  - For version 1 plans, no prior packaged amount exists (treated as 0).
 * Boundary Conditions and Limits:
 *  - financialPlan.version minimum of 1 (no previous version exists below 1).
 *  - Award/Academic Year and Payment Period scoping is mandatory to avoid cross-period contamination.
 * Financial Aid Implications:
 *  - Protects against over-awarding by not exceeding realized (disbursed) or previously packaged limits.
 *  - Aligns with institutional packaging controls and prevents counting cancelled transactions.
 * Required Inputs:
 *  - Bindings: disbursements, financialPlan, previousFinancialPlan, program, acyNo, awy, period, log.
 * Expected Output:
 *  - BigDecimal representing the maximum projected award amount for the NFR period (note: the non-graduate return 100 is assumed placeholder).
 * Error Handling Approach:
 *  - Defensive laziness to delay evaluation.
 *  - Minimal explicit null checks in script; relies on platform API invariants. Logging used for disbursement presence.
 * Integration Points:
 *  - disbursements: scoped queries by acyNo, awy, and payment period; filtering by status.
 *  - previousFinancialPlan: lookup of prior version and period-scoped maxAmount.
 *  - financialPlan: current version used to determine prior plan availability.
 *  - program: enrollment status gating the return rule.
 *  - log: diagnostic flagging of disbursed PP.
 * Compliance Considerations:
 *  - Excluding cancelled disbursements supports accurate Title IV and institutional fund accounting.
 *  - Graduate vs. non-graduate handling can tie to program-level funding policies.
 *  - Ensure alignment with federal late/cancelled disbursement handling and internal policies for NFR funds.
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NfrMaximumProjectedAwardAmountScript)
package CHANGE_ME // this should be updated to your actual package name
import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

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
// Check: Construction of scoped disbursement view for the target PP
// Variables: disbursements.getInAcademicYear(acyNo), .getInAwardYear(awy), .getInPaymentPeriod(period.number), acyNo, awy, period.number
// Purpose: Ensure all subsequent calculations consider only disbursements relevant to the evaluated NFR period, avoiding cross-period contamination.
// Logic:
//   - Start from disbursements binding (IDisbursementsAPI).
//   - Narrow to current academic year via getInAcademicYear(acyNo).
//   - Narrow to current award year via getInAwardYear(awy).
//   - Narrow to current payment period via getInPaymentPeriod(period.number).
// Success Path: Returns a disbursement collection view limited to the PP under evaluation.
// Failure Path: If scoping yields an empty collection, downstream calculations may result in null/zero outcomes.
// Business Rules: Only disbursements for the specific PP are relevant when projecting maximum award amounts.
// Error Handling: The API is expected to return an empty collection on no results; the script assumes no exception on chaining.
// Financial Impact: Prevents inflating or deflating PP amounts by ensuring accurate scoping.
// BOUNDARY CHECKS:
// Check: Validity of acyNo, awy, and period.number
// Source: Runtime bindings for acyNo (Integer), awy (Integer), period (NfrPeriod)
// Variables: acyNo, awy, period.number
// Validation:
//   - Implicit: If any is invalid or not present, the chained calls should yield an empty set; no explicit checks here.
// Edge Cases:
//   - No disbursements in PP: downstream disbursed amount will be null -> handled in later conditional.
//   - Nonexistent award/academic year combination: empty results.
// Error Handling: No explicit try/catch; relies on API resilience.
// Impact: Empty results reduce projected amounts to packaged or default logic.
// ARITHMETIC:
// Operation: N/A (data scoping only)
// Formula: N/A
// Variables: N/A
// Steps: N/A
// Business Context: N/A
// Result: A period-scoped disbursements collection
// Financial Impact: Indirect; enables correct calculation
// Limitations: Assumes availability of scoping methods in runtime API
def disbursementsInThisPp = lazy {
    disbursements
            .getInAcademicYear(acyNo)
            .getInAwardYear(awy)
            .getInPaymentPeriod(period.number)
}

// CONDITIONALS:
// Check: Exclude cancelled or cancellation-equivalent statuses
// Variables: disbursementsInThisPp().getCancelled(false), .filterStatuses { status -> status != "DISBURSEMENT_CANCELED" }
// Purpose: Ensure only active, non-cancelled disbursements influence projected maximums.
// Logic:
//   - Call getCancelled(false) to retain only non-cancelled disbursements.
//   - Apply filterStatuses to exclude any with status equal to "DISBURSEMENT_CANCELED".
// Success Path: Collection contains only active, non-cancelled records.
// Failure Path: If all are cancelled, the collection will be empty.
// Business Rules: Cancelled disbursements must not impact the maximum projected award per institutional and federal accounting practice.
// Error Handling: If statuses are unrecognized, they pass through; specific "DISBURSEMENT_CANCELED" is explicitly excluded.
// Financial Impact: Prevents overstating available funds by ignoring rescinded amounts.
// BOUNDARY CHECKS:
// Check: Status field presence and canonical value comparison
// Source: Disbursement status codes within IDisbursementsAPI
// Variables: status (String), filtered collection
// Validation:
//   - Predicate enforces exact string inequality.
// Edge Cases:
//   - Null status values: Groovy closure will evaluate status != "DISBURSEMENT_CANCELED" as true; such records remain included.
//   - Additional cancellation codes not listed: may pass unless covered by getCancelled(false).
// Error Handling: No explicit errors thrown; relies on API data quality.
// Impact: Inclusion of records with null/unknown statuses could affect totals; mitigated by downstream "DISBURSED" status check.
// ARITHMETIC:
// Operation: N/A (filtering)
// Formula: N/A
// Variables: N/A
// Steps: N/A
// Business Context: N/A
// Result: Non-cancelled disbursements for the PP
// Financial Impact: Indirect safeguard against inflated amounts
// Limitations: Dependent on correctness of status coding upstream
def nonCancelledDisbursementsInThisPp = lazy {
    disbursementsInThisPp()
            .getCancelled(false)
            .filterStatuses { status -> status != "DISBURSEMENT_CANCELED" }
}

// CONDITIONALS:
// Check: Select only records with status "DISBURSED"
// Variables: nonCancelledDisbursementsInThisPp().getWithStatus("DISBURSED")
// Purpose: Identify realized funds in the PP to cap or set the projected maximum amount.
// Logic:
//   - From filtered collection, select records matching "DISBURSED" status.
// Success Path: Returns collection of disbursed transactions (could be empty).
// Failure Path: Empty collection when no disbursed transactions exist.
// Business Rules: Only disbursed funds may be considered realized toward maximum projections.
// Error Handling: If "DISBURSED" not present, results are empty; no exception expected.
// Financial Impact: If present, disbursed amounts can drive the maximum projected award for graduate students.
// BOUNDARY CHECKS:
// Check: Presence of "DISBURSED" as a status value
// Source: Disbursement system status set
// Variables: filtered collection, status
// Validation:
//   - Exact match lookup through API method getWithStatus("DISBURSED").
// Edge Cases:
//   - Case sensitivity or alternative coding could result in missed records.
// Error Handling: N/A beyond empty results
// Impact: Missing disbursed flag would understate maximum when it should match disbursed totals.
// ARITHMETIC:
// Operation: N/A (selection)
// Formula: N/A
// Variables: N/A
// Steps: N/A
// Business Context: N/A
// Result: Collection of disbursed disbursements
// Financial Impact: Enables use of totalDisbursementAmount
// Limitations: Depends on accurate status mapping
def disbursedDisbursementsInThisPp = lazy {
    nonCancelledDisbursementsInThisPp().getWithStatus("DISBURSED")
}

// CONDITIONALS:
// Check: Whether any disbursed transactions exist in this PP
// Variables: disbursedDisbursementsInThisPp().empty, log.debug(...)
// Purpose: Drive downstream logic for disbursedAmount and provide traceability via logging.
// Logic:
//   - Compute boolean: !disbursedDisbursementsInThisPp().empty.
//   - Pass boolean and a label "DISBURSED PP" to debug logging.
// Success Path: Returns boolean indicating PP disbursed status (true if non-empty).
// Failure Path: Returns false when no disbursed transactions.
// Business Rules: Disbursed status informs whether realized funds should be considered.
// Error Handling: Logging calls should not throw; any logging failure is non-blocking.
// Financial Impact: A true result enables using actual disbursed totals, anchoring projections.
// BOUNDARY CHECKS:
// Check: Non-null collection check
// Source: Prior lazy suppliers
// Variables: disbursedDisbursementsInThisPp().empty
// Validation:
//   - Uses standard collection emptiness; implicit null-safety as lazy ensures an object is returned.
// Edge Cases:
//   - Empty but non-null collection is handled correctly.
// Error Handling: None required; safe operation
// Impact: Accurate flagging ensures correct branching for disbursedAmount.
// ARITHMETIC:
// Operation: N/A (boolean evaluation)
// Formula: N/A
// Variables: N/A
// Steps: N/A
// Business Context: N/A
// Result: Boolean flag for disbursed status
// Financial Impact: Indirect; controls utilization of realized disbursed totals
// Limitations: Assumes logging signature supports provided arguments
def thisPpIsDisbursed = lazy {
    log.debug(!disbursedDisbursementsInThisPp().empty, "DISBURSED PP")
}

// CONDITIONALS:
// Check: Use disbursed totals only when PP has disbursements
// Variables: thisPpIsDisbursed(), disbursedDisbursementsInThisPp().totalDisbursementAmount
// Purpose: Avoid using disbursed totals when none exist; set to null to signal absence.
// Logic:
//   - If thisPpIsDisbursed() is true, read totalDisbursementAmount from the disbursed collection.
//   - Else, set disbursedAmount to null.
// Success Path: BigDecimal total amount is available if disbursed; else null.
// Failure Path: Null indicates no realized disbursements for the PP.
// Business Rules: Realized funds must be captured accurately; absence should not be misrepresented as zero unless dictated by policy.
// Error Handling: Assumes totalDisbursementAmount is defined for the API; null assignment safe.
// Financial Impact: When present, caps projections to realized amounts in graduate logic.
// BOUNDARY CHECKS:
// Check: Non-null numeric amount
// Source: IDisbursementsAPI total computation
// Variables: totalDisbursementAmount (BigDecimal)
// Validation:
//   - Implicit: API should return a BigDecimal (0 or positive) for collections.
// Edge Cases:
//   - Zero total: valid, indicates no net disbursement despite status; treated as 0 BigDecimal.
//   - Partial decreases: reflected in total if API nets decreases.
// Error Handling: No explicit handling; expects platform API to compute correctly.
// Impact: Accurate financial reflection of what has been disbursed.
// ARITHMETIC:
// Operation: Aggregate disbursed total retrieval
// Formula: disbursedAmount = thisPpIsDisbursed() ? disbursedDisbursementsInThisPp().totalDisbursementAmount : null
// Variables:
//   - thisPpIsDisbursed(): Boolean
//   - totalDisbursementAmount: BigDecimal, currency units
// Steps:
//   1. Evaluate PP disbursed status.
//   2. If true, get aggregate total; else set null.
// Business Context: Ensures projections integrate realized fund flow.
// Result: BigDecimal or null
// Financial Impact: Directly influences maximum for graduate students
// Limitations: Depends on API’s correctness in totaling decreases and multiple payments
def disbursedAmount = thisPpIsDisbursed()
        ? disbursedDisbursementsInThisPp().totalDisbursementAmount
        : (BigDecimal) null

// CONDITIONALS:
// Check: Whether a previous financial plan version exists (version > 1)
// Variables: financialPlan.version, previousFinancialPlan.withVersion(financialPlan.version - 1), .getInAcademicYear(acyNo), .getInAwardYear(awy), .getInPaymentPeriod(period.number), .maxAmount
// Purpose: Incorporate prior packaged maximum amounts to avoid increasing projections beyond what was already set.
// Logic:
//   - If financialPlan.version == 1, return 0.0 (no previous version to consult).
//   - Else, acquire previous plan by withVersion(version - 1), then scope by acyNo, awy, and PP, and read maxAmount.
// Success Path: Returns prior packaged maxAmount when available; 0 for first version.
// Failure Path: If previous plan or scoped period is missing, platform should return null/zero; script assumes value is obtainable.
// Business Rules: Do not project beyond previously packaged amounts unless policy/process dictates; first version implies no carryover.
// Error Handling: No explicit null handling; relies on platform to provide safe defaults. Potential NPE avoided via lazy invocation order and guarded version check.
// Financial Impact: Prevents inadvertent over-packaging relative to earlier packaging decisions.
// BOUNDARY CHECKS:
// Check: financialPlan.version minimum boundary
// Source: financialPlan.getVersion()
// Variables: version (int), acyNo, awy, period.number
// Validation:
//   - version == 1 -> short-circuit to 0.0 BigDecimal-compatible number.
//   - version > 1 -> safe to query previousFinancialPlan.
// Edge Cases:
//   - Version gaps or missing prior plan data: may yield null; consumers of packagedAmount() should handle as needed.
//   - PP not present in previous plan: maxAmount may be null/zero depending on API behavior.
// Error Handling: No try/catch; failure to resolve previous plan may result in runtime error in some environments; assumed safe per platform contract.
// Impact: If prior data unavailable, 0.0 leads to conservative projections.
// ARITHMETIC:
// Operation: Prior packaged amount resolution
// Formula: packagedAmount = (version == 1) ? 0.0 : previousFinancialPlan.withVersion(version - 1).getInAcademicYear(acyNo).getInAwardYear(awy).getInPaymentPeriod(period.number).maxAmount
// Variables:
//   - financialPlan.version: int
//   - maxAmount: BigDecimal, currency units
// Steps:
//   1. Test version boundary.
//   2. If >1, resolve previous plan and scope to PP.
//   3. Retrieve maxAmount as prior packaged cap.
// Business Context: Ensures continuity with prior packaging decisions across plan versions.
// Result: BigDecimal prior packaged maximum (or 0.0)
// Financial Impact: Caps current projection to historical packaging where applicable
// Limitations: Assumes previous plan data integrity and consistent PP mapping
def packagedAmount = lazy {
    if (financialPlan.version == 1) {
        return 0.0
    }
    previousFinancialPlan.withVersion(financialPlan.version - 1)
            .getFund(fundCode)
            .getInAcademicYear(acyNo)
            .getInAwardYear(awy)
            .getInPaymentPeriod(period.number)
            .maxAmount
}

// CONDITIONALS:
// Check: program.enrollmentStatus == "G"
// Variables: program.enrollmentStatus, packagedAmount(), disbursedAmount
// Purpose: Apply program-level policy differentiating graduate students from others; for graduate enrollment, use the max of realized vs. packaged.
// Logic:
//   - If status is "G" and financialPlan.version > 1: compute [packagedAmount(), disbursedAmount].max().
//   - Else: return 100 (placeholder; assumed to be replaced by fund policy).
// Success Path: Returns BigDecimal max for graduates; otherwise returns 100.
// Failure Path: N/A as both branches return values.
// Business Rules: Graduate programs often have distinct packaging policies; using max prevents reducing below realized funds or prior packaged amounts.
// Error Handling: If disbursedAmount is null and packagedAmount is numeric, List.max() ignores null or may raise error depending on runtime; assumption is that null is handled or converted by Groovy comparison—recommend defensive null handling in future revisions.
// Financial Impact: For graduates, prevents lowering projected awards below what has been disbursed or packaged; for non-graduates, returns placeholder that may under/overstate amounts until configured.
// BOUNDARY CHECKS:
// Check: Validity of enrollment status and nullability of operands for max()
// Source: program.enrollmentStatus API; outputs of prior computations
// Variables: enrollmentStatus (String), packagedAmount BigDecimal/Number, disbursedAmount BigDecimal/null
// Validation:
//   - Exact equality to "G" and financialPlan.version > 1 drives the branch.
// Edge Cases:
//   - disbursedAmount == null and packagedAmount valid -> max should equal packagedAmount.
//   - Both null (unlikely for packaged): would cause runtime issues in max(); here packagedAmount() returns 0.0 at minimum, mitigating risk.
//   - Non-graduate branch fixed value 100 may not be compliant for production; assumed placeholder.
// Error Handling: None; relies on data invariants (packagedAmount non-null).
// Impact: Directly sets the returned maximum projected award amount.
// ARITHMETIC:
// Operation: Maximum selection between packaged and disbursed amounts
// Formula: result = max(packagedAmount(), disbursedAmount)
// Variables:
//   - packagedAmount(): BigDecimal (or numeric), currency units
//   - disbursedAmount: BigDecimal or null, currency units
// Steps:
//   1. Evaluate packagedAmount() lazily.
//   2. Compare to disbursedAmount; take the maximum.
// Business Context: Aligns projected maximum with the higher of planned vs. realized amounts for graduate students, maintaining financial integrity.
// Result: BigDecimal representing the maximum projected award for the PP (graduates) or 100 (others).
// Financial Impact: Ensures graduate awards are not set below realized or historical packaging.
// Limitations: Static 100 for non-graduates is a placeholder and may not reflect policy-driven computations.
if (program.enrollmentStatus == "G" && financialPlan.version > 1) {
    return [packagedAmount(), disbursedAmount].max()
}

// CONDITIONALS:
// Check: N/A (this is the else path)
// Variables: N/A
// Purpose: Provide a default value where graduate-specific logic does not apply.
// Logic:
//   - Returns constant 100.
// Success Path: Returns 100 as BigDecimal-compatible numeric.
// Failure Path: N/A
// Business Rules: Placeholder; in production, replace with proper NFR maximum computation for non-graduates.
// Error Handling: N/A
// Financial Impact: Could misstate projections if left as-is; should be aligned with institutional policy.
// BOUNDARY CHECKS:
// Check: N/A
// Source: N/A
// Variables: N/A
// Validation: N/A
// Edge Cases: N/A
// Error Handling: N/A
// Impact: Returns a constant
// ARITHMETIC:
// Operation: Constant return
// Formula: result = 100
// Variables: N/A
// Steps: N/A
// Business Context: Placeholder default
// Result: 100
// Financial Impact: Placeholder; not policy-driven
// Limitations: Must be replaced by real logic to ensure compliance and accuracy
return 100