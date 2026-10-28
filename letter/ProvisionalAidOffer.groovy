//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.LetterCodeCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

log.info("ProvisionalAidOfferLetter params={}", letterCode.params)

def letterCodeToReturn = "PO1"

//The letter event should only trigger for academic years where the ISIR award year is open
def academicYearNumber = letterCode.params.academicYearNumber.toInteger()
def academicYear = acys.getWithNumber(academicYearNumber)
def isAwardYearOpen = helper.awardYear.forPeriod(academicYear).find{awy->
    fafsaAwardYearInfoList.find{info->
        awy==info.getAwardYear()}?.isOpen()} != null
if (!isAwardYearOpen){
    log.info("ProvisionalAidOfferLetter return #1, isAwardYearOpen=false: null")
    return null
}
else{
    log.info("ProvisionalAidOfferLetter isAwardYearOpen=true")
}

//The letter event should be triggered for a student when they only have one FPO and it contains at least 1 Projected or Estimated fund status in their academic year
def fundStatuses = financialPlan.getInAcademicYear(academicYearNumber).getFundStatuses()
log.info("ProvisionalAidOfferLetter fundStatuses={}", fundStatuses)
if ((fundStatuses.contains("ESTIMATED") || fundStatuses.contains("PROJECTED")) && financialPlan.getVersion() == 1){
    log.info("ProvisionalAidOfferLetter return #2: {}", letterCodeToReturn)
    return letterCodeToReturn
}

//Once all funds are in an Awarded status the letter event should no longer trigger for that academic year
def financialPlanInAcademicYear = financialPlan.getInAcademicYear(academicYearNumber)
if (financialPlanInAcademicYear.getFundStatuses().find{it != "AWARDED"}==null){
    log.info("ProvisionalAidOfferLetter fundStatuses, all funds awarded={}", financialPlanInAcademicYear.getFundStatuses())
    log.info("ProvisionalAidOfferLetter return #3: null")
    return null
}

//The letter event should be generated when there are updates from the prior version of the FPO V2
if (financialPlan.getVersion() > 1){
    def prevFinancialPlanInAcy = previousFinancialPlan.withVersion(financialPlan.getVersion() - 1).getInAcademicYear(academicYearNumber)

    //The letter should generate if Cost of Attendance is updated from the prior version of the FPO V2
    if (prevFinancialPlanInAcy.getAcyCoa() != financialPlanInAcademicYear.getAcyCoa()){
        log.info("ProvisionalAidOfferLetter preAcyCoa={}, currentAcyCoa={}", prevFinancialPlanInAcy.getAcyCoa(), financialPlanInAcademicYear.getAcyCoa())
        log.info("ProvisionalAidOfferLetter return #4: {}", letterCodeToReturn)
        return letterCodeToReturn
    }

    //If current FPO has different funds in it than prior FPO, then generate letter
    def funds = financialPlanInAcademicYear.getFundCodes()
    def previousFunds = prevFinancialPlanInAcy.getFundCodes()
    log.info("ProvisionalAidOfferLetter previousFunds={}", previousFunds)
    log.info("ProvisionalAidOfferLetter funds={}", funds)
    if (funds != previousFunds) {
        log.info("ProvisionalAidOfferLetter return #5: {}", letterCodeToReturn)
        return letterCodeToReturn
    }

    //The letter should generate if a fund's status or award amount changes
    def differentFund = funds.find { fc ->
        def fund = financialPlanInAcademicYear.getFund(fc)
        def prevFund = prevFinancialPlanInAcy.getFund(fc)
        fund.getFundStatuses() != prevFund.getFundStatuses() || fund.getMaxAmount() != prevFund.getMaxAmount()
    }
    if (differentFund) {
        log.info("ProvisionalAidOfferLetter return #6: {}", letterCodeToReturn)
        return letterCodeToReturn
    }
}

log.info("ProvisionalAidOfferLetter return #7: null")
return null