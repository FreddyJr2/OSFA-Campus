//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirUsedInPackagingScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//get all compatible ISIRs for the Award Year where no reject codes exist and a Primary EFC exists
def awardYearISIRs = allAwardYearIsirs.forAwardYear(awy).getCompatibleWithPackaging().findAll{it.getRejectCodes().isEmpty() && it.getIsirFieldValue("PRIMARYEFC").trim() != ""}

//if a Valid or Unverified ISIR exists, then return it
def validOrUnverifiedISIR = awardYearISIRs.find{["Valid", "Unverified"].contains(it.isirStatus)}
if (validOrUnverifiedISIR != null) {
    return validOrUnverifiedISIR
}

//if a Corrected ISIR exists, then return it if the ISIR had been previously Valid or Unverified
def previouslyValidOrUnverifiedCorrectedISIR = awardYearISIRs.find{it.isirStatus == "Corrected" && ["Valid", "Unverified"].contains(it.getIsirStatusBeforeCorrection())}
if (previouslyValidOrUnverifiedCorrectedISIR != null) {
    return previouslyValidOrUnverifiedCorrectedISIR
}

//if a Corrected ISIR exists, then return it
def correctedISIR = awardYearISIRs.find{it.isirStatus == "Corrected"}
if (correctedISIR != null) {
    return correctedISIR
}

//if a Pending ISIR exists, then return it
def pendingISIR = awardYearISIRs.find{it.isirStatus == "Pending"}
if (pendingISIR != null) {
    return pendingISIR
}

//if no ISIRs exist in the Award Year that fit the criteria above, then no ISIR will be returned
return null