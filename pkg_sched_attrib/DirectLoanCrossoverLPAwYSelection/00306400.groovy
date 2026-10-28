//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DirectLoanCrossOverLpAwySelectionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

//If the Loan Period start date is prior to to 07/01/24, return null
if (lp.startDate < new LocalDate(2024, 07, 01)) {
    return null
}

//If there are disbursed TIV DL disbursements for the Loan Period return the earliest award year found for those disbursements
def tivDisbursementsInLP = disbursements
        .getTitleIv()
        .getLoans()
        .getInLoanPeriod(lp)
def firstDisbursedAwardYear = candidateAwys.find{tivDisbursementsInLP
        .getInAwardYear(it)
        .getStatuses()
        .contains("DISBURSED")}
if (firstDisbursedAwardYear != null) {
    return firstDisbursedAwardYear
}

//If not a term program, return the current AwY
if (!program.isTerm()) {
    return candidateAwys[0]
}

//If a summer trailer term is the loan period, return the current AwY
def LPIsSummerTrailer = acys
        .terms
        .getOverlappingWith(lp)
        .every{it.summerTrailer}
if (LPIsSummerTrailer) {
    return candidateAwys[0]
}

//If the academic year contains a summer header, return the upcoming AwY
def LPContainsSummerHeader = acys
        .terms
        .getOverlappingWith(lp)
        .any{it.summerHeader}
if (LPContainsSummerHeader) {
    return candidateAwys[1]
}

//If first term is a cross-over, return the upcoming AwY
def firstTermIsCrossover = helper.awardYear.isCrossover(acys.terms[0])
if (firstTermIsCrossover) {
    return candidateAwys[1]
}

//No other conditions met, return the current AwY
return candidateAwys[0]