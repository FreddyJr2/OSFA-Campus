//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NfrPeriodsScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

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
    !getLoanTerms(acy.getTerms(), loan).isEmpty()
}

/**
 * Get the list of CLUIDs that were associated to a previous package version of the current fund code.
 */
def cluidsUsedByPreviousPackageVersion = lazy {
    Set<ICommonLineUniqueId> usedCLUIDs = [];
    if (financialPlan.version > 1) {
        // Reuse the same loan in case this fund code is already associated to a fund code that was used on a previous package version
        usedCLUIDs = previousFinancialPlan
                .withVersion(financialPlan.version - 1)
                .getFund(fundCode)
                .commonLineUniqueIds
        if (!usedCLUIDs.isEmpty()) {
            // Make sure the loan is not used by another fund in the new package version
            Set<ICommonLineUniqueId> otherFundsCLUID = financialPlan
                    .nfr
                    .filterFunds { f -> !f.equals(fundCode) }
                    .commonLineUniqueIds
            usedCLUIDs.removeAll(otherFundsCLUID)

            if (!usedCLUIDs.isEmpty()) {
                log.debug("Reusing CLUID {} for fund {} from previous package version", usedCLUIDs, fundCode)
            }
        }
    }
    usedCLUIDs
}

def thisFundInPreviousAcademicYears = lazy {
    financialPlan.getFund(fundCode)
            .filterAcademicYears { anAcyNo -> anAcyNo < acyNo }
            .reach()
}

/**
 * Get the list of CLUIDs that are already associated to the current fund code in a prior academic year of that package.
 * This will allow a CLUID to span multiple academic years.
 */
def cluidsUsedInPreviousAcademicYears = lazy {
    // Reuse the same loan in case this fund code is already associated to a fund code that might span multiple academic years
    Set<ICommonLineUniqueId> usedCLUIDs = thisFundInPreviousAcademicYears().commonLineUniqueIds
    if (!usedCLUIDs.isEmpty()) {
        log.debug("Reusing CLUID {} for fund {} from previous academic years", usedCLUIDs, fundCode)
    }
    usedCLUIDs
}

def candidateLoans = lazy {
    commonLineLoans.filter { loan ->
        loan.processingStatus != "COMMON_LINE_UNIQUE_ID_CHANGED" &&
                loan.getRequestedLoanAmount() > 0 &&
                hasTermsInCurrentAcademicYear(loan)
    }
}

/**
 * Find the CL Loan API object that can be reused from the list of usedCLUIDs
 */
def selectAlreadyUsedCLUID = { Set<ICommonLineUniqueId> usedCLUIDs ->
    if (usedCLUIDs.isEmpty()) {
        return null;
    }
    candidateLoans().filter { loan -> usedCLUIDs.contains(loan.getCommonLineUniqueId()) }
            .max { loan -> loan.getRequestedLoanAmount() }
}

/**
 * Select the available CLUID with the maximum requested loan amount.
 */
def newCluid = lazy {
    candidateLoans().filter { loan -> financialPlan.isUnmatchedCommonLineLoan(loan) }
            .max { loan -> loan.getRequestedLoanAmount() }
}

def noLoansLeft = lazy { !candidateLoans().any(financialPlan.&isUnmatchedCommonLineLoan) }

if (noLoansLeft() && thisFundInPreviousAcademicYears().isEmpty()) {
    return [];
}

Set<ICommonLineUniqueId> usedCLUIDs = cluidsUsedByPreviousPackageVersion()
if (usedCLUIDs.isEmpty()) {
    usedCLUIDs = cluidsUsedInPreviousAcademicYears()
}

ICommonLineLoanAPI selectedLoan = selectAlreadyUsedCLUID(usedCLUIDs) ?: newCluid()

if (selectedLoan != null) {
    def loanTerms = getAllLoanTermsAcrossPackage(selectedLoan)
    return loanTerms
            .collect { period ->
                log.debug("Associating fund {} for period {} starting on {} with CLUID {}",
                        fundCode, period.number, period.startDate, selectedLoan.commonLineUniqueId)
                new NfrPeriod(period.getNumber(), period)
                        .withCommonLineLoan(selectedLoan)
                        .withPairedPeriod(period)
            }
}
return []
