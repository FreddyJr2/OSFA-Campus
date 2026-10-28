//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.AwardYearSelectionCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Pell Award Year Selection Criteria - combined Term and Non-Term*/

//variables for earlier and later year ISIRs
def earlierYearISIRExists = isirRecords.getCurrentIsirForAwardYear(crossoverHelper.getCurrentAwardYear()) != null
def laterYearISIRExists = isirRecords.getCurrentIsirForAwardYear(crossoverHelper.getUpcomingAwardYear()) != null

//variables for earlier and later year Pell disbursements that are in "Disbursed" status
def earlierYearPellDisbursementExists = crossoverHelper.hasDisbursedDisbursementsInCurrentAwardYear()
def laterYearPellDisbursementExists = crossoverHelper.hasDisbursedDisbursementsInUpcomingAwardYear()

//if an ISIR and disbursements exist in the earlier year, then select the earlier year
if (earlierYearISIRExists && earlierYearPellDisbursementExists) {
    return crossoverHelper.useCurrentAlternative()
}

//if an ISIR and disbursements exist the later year, then select the later year
if (laterYearISIRExists && laterYearPellDisbursementExists) {
    return crossoverHelper.useUpcomingAlternative()
}

//if an ISIR exists in the earlier year and remaining eligibility > $0 for pell in the earlier year, then select the earlier year
if (earlierYearISIRExists && currentFunds.getFund("PELL").getOverlappingWith(pp).maxAmount > 0) {
    return crossoverHelper.useCurrentAlternative()
}

//if an ISIR exists in the later year and no ISIRs exist in the earlier year, then select the later year
if (laterYearISIRExists && !earlierYearISIRExists) {
    return crossoverHelper.useUpcomingAlternative()
}

//if none of the above criteria are met, then use default pell crossover logic
return crossoverHelper.useDefaultAlternative()