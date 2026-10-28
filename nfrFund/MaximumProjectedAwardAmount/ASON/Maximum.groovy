//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NfrMaximumProjectedAwardAmountScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

import org.joda.time.LocalDate

static BigDecimal nthIntPart(BigDecimal dividend, int divisor, int partIdx) {
    int truncatedDividend = dividend.intValue();
    BigDecimal fraction = dividend - truncatedDividend;
    int roundedAmount = (dividend / divisor).intValue()
    return partIdx < truncatedDividend % divisor
            ? roundedAmount + 1
            : partIdx == divisor - 1
            ? roundedAmount + fraction
            : roundedAmount
}

def now = LocalDate.now()

def condDebug = { NfrPeriod p, String msg ->
    if (p == period) {
        log.debug(msg)
    }
}

def loan = period.commonLineLoan
def periodsAssociatedToLoan = nfrConfig.evalPeriods().filter { p -> p.commonLineLoan.equals(loan) }
        .indexed(1)
int nbPeriods = periodsAssociatedToLoan.size()
def getLoanPeriodNumber = { NfrPeriod p ->
    periodsAssociatedToLoan.find { it.value == p }.key
}
def isAlreadyPackaged = { NfrPeriod p ->
    p.pairedTerm.acyNo < acyNo
}
def alreadyPackaged = financialPlan.filterAcademicYears { curAcyNo -> curAcyNo < acyNo }
        .getWithCommonLineUniqueId(loan.commonLineUniqueId)
        .reach();
def thisInfo = "for fund $fundCode for acy $acy.number in period $period.number using CLUID $loan.commonLineUniqueId"

def isDecrease = loan.requestedLoanAmount < loan.ppAmountTotal

def frozenValue = { NfrPeriod p ->
    int clPpNo = getLoanPeriodNumber(p)
    int periodIndex = clPpNo - 1

    if (periodIndex == -1) {
        condDebug(p, "Period index can't be found $thisInfo, periodsAssociatedToLoan=$periodsAssociatedToLoan")
        return 0.0
    }

    if (loan.recordStatus == "LOAN_TERMINATED") {
        condDebug(p, "Setting zero amount for terminated loan $thisInfo, periodIndex=$periodIndex, nbPeriods=$nbPeriods")
        return 0.0
    }

    def thisTerm = p.pairedTerm
    if (thisTerm == null) {
        condDebug(p, "Setting zero amount for not found current term $thisInfo, periodIndex=$periodIndex, nbPeriods=$nbPeriods")
        return 0.0
    }

    if (thisTerm.isNotAttending()) {
        condDebug(p, "Setting zero amount for not attending term $thisInfo, periodIndex=$periodIndex, nbPeriods=$nbPeriods")
        return 0.0
    }

    if (isAlreadyPackaged(p)) {
        condDebug(p, "Using value calculated in previous acy $thisInfo")
        return alreadyPackaged.getOverlappingWith(p).maxAmount
    }

    if (!isDecrease && loan.isDisbursed(clPpNo) && loan.endDate < now) {
        condDebug(p, "Using disbursed amount $thisInfo")
        return loan.getPpAmount(clPpNo)
    }
    return null;
}.memoize()

def isFrozen = { NfrPeriod p -> frozenValue(p) != null }

if (isFrozen(period)) {
    return frozenValue(period)
}

def allFrozen = periodsAssociatedToLoan.findAll { isFrozen(it.value) }
def frozenTotal = allFrozen.collect { frozenValue(it.value) }
        .inject(0.0, { a, b -> a + b })

def amountLeft = loan.requestedLoanAmount - frozenTotal;
def periodsLeft = periodsAssociatedToLoan - allFrozen

if (periodsLeft.isEmpty()) {
    log.debug("No periods left for $thisInfo")
    return 0
}

def acyScopedFundCodes = ["DISCOUNT"].toSet()
def acyScopedTotal = { int curAcyNo ->
    financialPlan.getInAcademicYear(curAcyNo).getFunds(acyScopedFundCodes).maxAmount
}.memoize();
def higherPriorityLoans = nfrConfig.evalPeriods()
        .takeWhile { p -> p.commonLineLoan != loan }
        .collect { p -> p.commonLineLoan }
        .unique()
def higherPriorityPeriods = nfrConfig.evalPeriods().filter { p -> higherPriorityLoans.contains(p.commonLineLoan) }

def orderedByNeed = periodsLeft.collectEntries([:] as LinkedHashMap<NfrPeriod, BigDecimal>, { clPpNo, p ->
    def curAcy = acys.getWithNumber(p.pairedTerm.acyNo)
    def curAcyNo = curAcy.number
    def termFundingSoFar = financialPlan.filterFunds { code -> !acyScopedFundCodes.contains(code) }
            .getOverlappingWith(p)
            .maxAmount
    def higherPriorityPeriodTotalInThisTerm = higherPriorityPeriods
            .getOverlappingWith(p)
            .collect(nfrConfig.&evalMaximumProjectedAwardAmount)
            .inject(0.0, { a, b -> a + b })
    def nbTerms = curAcy.terms.size()
    def pairedTermIdx = curAcy.terms.indexOf(p.pairedTerm)
    def acyScopedInThisTerm = nthIntPart(acyScopedTotal(curAcyNo), nbTerms, pairedTermIdx);
    def termCoa = coa.in(p.pairedTerm).activeCoa
    def termRemainingNeed = [termCoa - termFundingSoFar - higherPriorityPeriodTotalInThisTerm - acyScopedInThisTerm, 0].max()
    [(p): termRemainingNeed]
}).toSorted { it.value }

def computedFirst = orderedByNeed.takeWhile { it.key != period }
        .keySet();

def computedFirstTotal = computedFirst
        .collect(nfrConfig.&evalMaximumProjectedAwardAmount)
        .inject(0.0, { a, b -> a + b })

def noPeriodsLeftToCompute = orderedByNeed.size() - computedFirst.size();

def curMaxAmount = nthIntPart(amountLeft - computedFirstTotal, noPeriodsLeftToCompute, 0)

def amount = [curMaxAmount, orderedByNeed[period]].min();

log.debug("Setting amount for fund {} for acy {} period {} using CLUID {}, amount={}, nbPeriods={}",
        fundCode, acy.number, period.number, loan.commonLineUniqueId, amount, nbPeriods)

return amount
