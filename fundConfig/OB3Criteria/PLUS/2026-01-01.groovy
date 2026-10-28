/**
 * CUSTOMER ATTENTION GUIDE
 * ------------------------
 * Review this file in the following order:
 *
 * 1. Search for `isLoanLimitException()`
 *    This is the first and most important decision point.
 *
 * 2. Search for `financialPlan.filterFunds`
 *    This is where the script decides what counts as already-used Parent PLUS borrowing.
 *
 * 3. Search for `20000.0` and `65000.0`
 *    These are the annual and aggregate Parent PLUS cap constants used by the
 *    delivered logic.
 *
 * 4. Search for `withParentPlusAnnualRemainingEligibility` and
 *    `withParentPlusAggregateRemainingEligibility`
 *    This is where the computed remaining values are written back onto the
 *    PLUS override object.
 *
 * Practical framing:
 * - Customers are generally not expected to redesign the formulas here.
 * - The main customer responsibility is to understand the upstream Loan Limit
 *   Exception behavior and confirm that the local fund-code structure makes the
 *   PLUS-family scoping behave as intended.
 *
 * In plain English:
 * - if PLUS has a valid loan limit exception, leave it alone
 * - otherwise, calculate annual and lifetime remaining eligibility and store
 *   those values for downstream packaging
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.Ob3CriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import java.math.RoundingMode

// Convert the generic award override object into the PLUS-specific view.
def plusAwardInfo = awardInfo.asPlus()

final acyDisbursedDisbursements = disbursements
        .getFund(fundCode)
        .getInAcademicYear(acyNo)
        .getCancelled(false)
        .getWithStatus("DISBURSED")
        .reach()

// NOTE: SOR is not applicable to Parent PLUS

// CUSTOMER REVIEW AREA:
// If the upstream process has already determined that the student has a valid
// Loan Limit Exception, this script does not apply the OB3 remaining-eligibility
// limits here. It simply returns the existing PLUS override object unchanged.
log.debug("OB3Config PLUS OB3 criteria script plusAwardInfo.isLoanLimitException() = {}", plusAwardInfo.isLoanLimitException())
if (plusAwardInfo.isLoanLimitException()) {
    return plusAwardInfo
}

// DELIVERED FEDERAL / PRODUCT LOGIC:
// Scope the student's current financial plan to PLUS funds only.
// This is the script's definition of what already-packaged PLUS borrowing
// should count against the new Parent PLUS limits.
def allCoOutputs = financialPlan.filterFunds { fc ->
    fc.startsWith("PLUS")
}

// Restrict to the current academic year for the annual remaining calculation.
def acyCoOutputs = allCoOutputs.getInAcademicYear(acyNo)

// DELIVERED DEPARTMENT OF EDUCATION / PRODUCT LOGIC:
// Remaining annual eligibility for Parent PLUS.
// Formula:
//   max(0, 20000 - current academic year PLUS usage)
def ob3PlusAcyRemainingElig = [0.0, 20000.0 - acyCoOutputs.maxAmount].max()

// DELIVERED DEPARTMENT OF EDUCATION / PRODUCT LOGIC:
// Remaining aggregate eligibility for Parent PLUS.
def ob3PlusLifetimeRemainingElig =
        dloan.startAggregateLoanLimitCalculationForFunds(["PLUS"])
                .getRemainingAggregateEligibility(65000.0, dloan.getAggregateStudentRecipientParentPlusTotal())

// If disbursed amount exceeds the new limits we assume this is a mixed Interim
// Exception case and try to keep disbursed disbursements
final disbursedAmount = acyDisbursedDisbursements
        .getTotalDisbursementAmount()

final currentLimit = [
        ob3PlusAcyRemainingElig,
        ob3PlusLifetimeRemainingElig
].min()

if (disbursedAmount > currentLimit) {
    plusAwardInfo = plusAwardInfo.withLoanLimitException(true)
    ob3PlusAcyRemainingElig = [ob3PlusAcyRemainingElig, disbursedAmount].max()
    ob3PlusLifetimeRemainingElig = [ob3PlusLifetimeRemainingElig, disbursedAmount].max()
}

// Write the calculated remaining eligibility values back onto the PLUS override
// object for downstream packaging and UI/reporting use.
return plusAwardInfo.withParentPlusAnnualRemainingEligibility(ob3PlusAcyRemainingElig)
        .withParentPlusAggregateRemainingEligibility(ob3PlusLifetimeRemainingElig)