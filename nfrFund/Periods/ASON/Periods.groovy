//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NfrPeriodsScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

// Replace this code with your code
def alternativeLoanProgramTypeCodes = ["091"]

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

def cluidsDisbursedInOtherFunds = lazy {
    Set<ICommonLineUniqueId> otherFundsDisbursedCLUIDs = [];
    if (financialPlan.version > 1) {
        otherFundsDisbursedCLUIDs = previousFinancialPlan
                .withVersion(financialPlan.version - 1)
                .nfr
                .filterCommonLineUniqueIds { cluid -> cluid != null }
                .filterFunds { f -> f != fundCode && isDisbursed(f) }
                .commonLineUniqueIds
    }
    otherFundsDisbursedCLUIDs
}

/**
 * Indicate if the fund has already been disbursed across all academic years
 */
def isDisbursed(String fundCode) {
    disbursements
            .getWithFundCode(fundCode)
            .getWithStatus("DISBURSED")
            .getTotalDisbursementAmount() > 0
}

/**
 * Get the list of terms that can be associated with the given loan.
 * All loan / term matching logic should happen here.
 */
IAcademicTermPeriodsAPI getLoanTerms(IAcademicTermPeriodsAPI terms, ICommonLineLoanAPI loan) {
    terms.getOverlappingWith(loan)
}

/**
 * Get the list of terms that can be associated to the given loan across all academic years of the current package.
 */
IAcademicTermPeriodsAPI getAllLoanTermsAcrossPackage(ICommonLineLoanAPI loan) {
    getLoanTerms(acys.getTerms(), loan)
}

/**
 * Indicate if the loan can be associated with the current academic year.
 */
boolean hasTermsInCurrentAcademicYear(ICommonLineLoanAPI loan) {
    !getLoanTerms(acy.terms, loan).isEmpty()
}

return commonLineLoans
        .filter { loan ->
            alternativeLoanProgramTypeCodes.contains(loan.alternativeLoanProgramTypeCode) &&
                    loan.getRequestedLoanAmount() > 0 &&
                    loan.processingStatus != "COMMON_LINE_UNIQUE_ID_CHANGED" &&
                    hasTermsInCurrentAcademicYear(loan) &&
                    !cluidsDisbursedInOtherFunds().contains(loan.commonLineUniqueId)
        }
        .orderedBy(new OrderBy([{ it.startDate }, { it.commonLineUniqueId }]))
        .withIndex()
        .collectMany { loan, loanIndex ->
            def loanTerms = getAllLoanTermsAcrossPackage(loan)
            loanTerms.withIndex(1).collect { period, periodIndex ->
                int periodNumber = loanIndex * 10 + period.number;
                // should be:
                // periodNumber = loanIndex * 10 + periodIndex;
                log.debug("Associating fund {} for period {} starting on {} with CLUID {}",
                        fundCode, periodNumber, period.startDate, loan.commonLineUniqueId)
                new NfrPeriod(periodNumber, period)
                        .withCommonLineLoan(loan)
                        .withPairedPeriod(period)
            }
        }
