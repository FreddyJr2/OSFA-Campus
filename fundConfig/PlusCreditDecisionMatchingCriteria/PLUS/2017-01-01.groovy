//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.PlusCreditDecisionMatchingCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

// Define utility methods
static BigDecimal nthIntPart(BigDecimal dividend, int divisor, int partIdx) {
    int truncatedDividend = dividend.intValue()
    BigDecimal fraction = dividend - truncatedDividend
    int roundedAmount = (dividend / divisor).intValue()
    return partIdx < truncatedDividend % divisor
            ? roundedAmount + 1
            : partIdx == divisor - 1
            ? roundedAmount + fraction
            : roundedAmount
}

// Actual script
boolean applicableLoansExist

if (program.isTerm()) {
    // Acy-scoped fund codes the client may need. Setting it as empty works too but in this case
    // the script can be significantly simplified. As this script only applies to (G)PLUS adding
    // fund codes with lower priority than (G)PLUS will make no difference.
    def acyScopedFundCodes = ["DISCOUNT"] as Set<String>

    def acyScopedTotal = financialPlan.getInAcademicYear(acyNo).filterFunds(acyScopedFundCodes.&contains).maxAmount

    def termsInNeed = acy.terms.indexed().findAll { tidx, t ->
        def termFundingSoFar = financialPlan.filterFunds { code -> !acyScopedFundCodes.contains(code) }
                .getOverlappingWith(t)
                .maxAmount
        def acyScopedInThisTerm = nthIntPart(acyScopedTotal, acy.terms.size(), tidx)
        def termCoa = coa.in(t).activeCoa
        def termRemainingNeed = [termCoa - termFundingSoFar - acyScopedInThisTerm, 0].max()
        termRemainingNeed > 0
    }.values()

    applicableLoansExist = termsInNeed.any { t ->
        !plusLoans.getOverlappingWith(t).isEmpty()
    }
} else {
    applicableLoansExist = acy.loanPaymentPeriods.any { pp ->
        pp.status != "CANCELED" && !plusLoans.getOverlappingWith(pp).isEmpty()
    }
}

if (!applicableLoansExist && plusOverrides.isEmpty() && deniedPlusLoans.isEmpty()) {
    return [projectedPlusLoan]
} else {
    return plusLoans.getOverlappingWith(pp).collect { loan ->
        loan.withNoPriority() // removing default priority queue here and rebuilding it below:
                .addReversedPriority(disbursements.getInScope(scope).isDisbursed())
                .addPriority(loan.getLatestPackagePriority(acyNo))
                .addPriority(loan.getProcessDate())
                .addReversedPriority(loan.getAcceptedLoanAmount())
    }
}
