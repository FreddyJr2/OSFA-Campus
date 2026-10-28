//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationClearanceRulesScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/*
Due to the changes to ISIR Verification for 24/25 we are no longer automatically triggering ISIR corrections within our
baseline configuration. An ISIR Verification task will be created once all acceptable documentation is received for a
Financial Aid Administrator to review. If Oracle determines that there is additional logic that can be added to increase
ISIR Verification Automation we will enhance our baseline configuration in future releases.
*/

//Note: if script returns true then auto code clear, if it returns false then create a task

//Student ISIR variables
def isirSTUDENT_FILED1040OR1040NR = isirRecord.getField(IsirField.STUDENT_FILED1040OR1040NR).getOrNull()
def isirSTUDENTFTIM_IRSRESPONSECODE_FTI = isirRecord.getField(IsirField.STUDENTFTIM_IRSRESPONSECODE_FTI).getOrNull()
def isirSTUDENT_FILEDNONUSTAXRETURN = isirRecord.getField(IsirField.STUDENT_FILEDNONUSTAXRETURN).getOrNull()
def isirSTUDENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI = isirRecord.getField(IsirField.STUDENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI).getOrNull()
def isirSTUDENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI = isirRecord.getField(IsirField.STUDENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI).getOrNull()
def isirSTUDENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS = isirRecord.getField(IsirField.STUDENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS).getOrNull()
def isirSTUDENT_IRAROLLOVER = isirRecord.getField(IsirField.STUDENT_IRAROLLOVER).getOrNull()
def isirSTUDENT_UPDATEDFAMILYSIZE = isirRecord.getField(IsirField.STUDENT_UPDATEDFAMILYSIZE).getOrNull()
def isirFPS_INCARCERATEDAPPLICANTFLAG = isirRecord.getField(IsirField.FPS_INCARCERATEDAPPLICANTFLAG).getOrNull()
def isirFPS_ASSUMEDSTUDENTFAMILYSIZE = isirRecord.getField(IsirField.FPS_ASSUMEDSTUDENTFAMILYSIZE).getOrNull()
def isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION = isirRecord.getField(IsirField.STUDENT_FOREIGNEARNEDINCOMEEXCLUSION).getOrNull()

//Student Spouse ISIR variables
def isirSPOUSEFTIM_IRSRESPONSECODE_FTI = isirRecord.getField(IsirField.SPOUSEFTIM_IRSRESPONSECODE_FTI).getOrNull()
def isirSPOUSE_TAXRETURNFILINGSTATUS = isirRecord.getField(IsirField.SPOUSE_TAXRETURNFILINGSTATUS).getOrNull()
def isirSPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI = isirRecord.getField(IsirField.SPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI).getOrNull()
def isirSPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI = isirRecord.getField(IsirField.SPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI).getOrNull()
def isirSPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS = isirRecord.getField(IsirField.SPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS).getOrNull()
def isirSPOUSE_IRAROLLOVER = isirRecord.getField(IsirField.SPOUSE_IRAROLLOVER).getOrNull()
def isirSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION = isirRecord.getField(IsirField.SPOUSE_FOREIGNEARNEDINCOMEEXCLUSION).getOrNull()

//Custom documents received variables
boolean IdentificationDocsReceived = false
boolean SOEPDocsReceived = false

//Custom documents received check
if ((receivedDocuments.hasDoc("DriversLicense", "Student") && receivedDocuments.get("DriversLicense", "Student").isAcceptable()) || (receivedDocuments.hasDoc("NonDriversLicenseID", "Student") && receivedDocuments.get("NonDriversLicenseID", "Student").isAcceptable()) || (receivedDocuments.hasDoc("StateIssuedID", "Student") && receivedDocuments.get("StateIssuedID", "Student").isAcceptable()) || (receivedDocuments.hasDoc("Passport", "Student") && receivedDocuments.get("Passport", "Student").isAcceptable())) {
    IdentificationDocsReceived =  true
}
if ((receivedDocuments.hasDoc("SOEP-Campus", "Student") && receivedDocuments.get("SOEP-Campus", "Student").isAcceptable()) || (receivedDocuments.hasDoc("SOEP-Notary", "Student") && receivedDocuments.get("SOEP-Notary", "Student").isAcceptable())) {
    SOEPDocsReceived =  true
}

//If student is incarcerated and has custom documents then exit the script
if ((isirFPS_INCARCERATEDAPPLICANTFLAG == "1" || isirFPS_INCARCERATEDAPPLICANTFLAG == "2" || isirFPS_INCARCERATEDAPPLICANTFLAG == "3") && IdentificationDocsReceived && SOEPDocsReceived) {
    return true
}

//Student auto code clearing logic
if ((isirSTUDENT_FILED1040OR1040NR == "1" || isirSTUDENT_FILED1040OR1040NR == null) && [null, "203", "206", "212", "214"].contains(isirSTUDENTFTIM_IRSRESPONSECODE_FTI)) {
    if ((receivedDocuments.hasDoc("1040", "Student") && receivedDocuments.get("1040", "Student").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Student") && receivedDocuments.get("1040x", "Student").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Student") && receivedDocuments.get("TaxReturnTranscript", "Student").isAcceptable())) {
        return false
    }
}
if (isirSTUDENT_FILED1040OR1040NR == "2" && isirSTUDENT_FILEDNONUSTAXRETURN == "1") {
    if (receivedDocuments.hasDoc("ForeignTaxTranscript", "Student") && receivedDocuments.get("ForeignTaxTranscript", "Student").isAcceptable()) {
        return false
    }
}
if (isirSTUDENT_FILED1040OR1040NR == "2" && (isirSTUDENT_FILEDNONUSTAXRETURN == "2" || isirSTUDENT_FILEDNONUSTAXRETURN == null)) {
    if ((receivedDocuments.hasDoc("NonFilingStatement", "Student") && receivedDocuments.get("NonFilingStatement", "Student").isAcceptable()) || (receivedDocuments.hasDoc("W2", "Student") && receivedDocuments.get("W2", "Student").isAcceptable()) || (receivedDocuments.hasDoc("1099G", "Student") && receivedDocuments.get("1099G", "Student").isAcceptable()) || (receivedDocuments.hasDoc("SelfEmploymentStatement", "Student") && receivedDocuments.get("SelfEmploymentStatement", "Student").isAcceptable())) {
        return false
    }
}

//Student Spouse auto code clearing logic
if (isirSPOUSE_TAXRETURNFILINGSTATUS == "3" && [null, "203", "206", "212", "214"].contains(isirSPOUSEFTIM_IRSRESPONSECODE_FTI)) {
    if ((receivedDocuments.hasDoc("1040", "Student Spouse") && receivedDocuments.get("1040", "Student Spouse").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Student Spouse") && receivedDocuments.get("1040x", "Student Spouse").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Student Spouse") && receivedDocuments.get("TaxReturnTranscript", "Student Spouse").isAcceptable())) {
        return false
    }
}
if (isirSPOUSE_TAXRETURNFILINGSTATUS == "3" && [null, "203", "206", "212", "214"].contains(isirSPOUSEFTIM_IRSRESPONSECODE_FTI) && isirSTUDENTFTIM_IRSRESPONSECODE_FTI == "200") {
    if ((receivedDocuments.hasDoc("1040", "Student Spouse") && receivedDocuments.get("1040", "Student Spouse").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Student Spouse") && receivedDocuments.get("1040x", "Student Spouse").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Student Spouse") && receivedDocuments.get("TaxReturnTranscript", "Student Spouse").isAcceptable())) {
        return false
    }
}

//Student rollover information auto code clearing logic
if (isirSTUDENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirSTUDENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirSTUDENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirSTUDENT_IRAROLLOVER > 0) {
    if (receivedDocuments.hasDoc("RolloverStatement", "Student") && receivedDocuments.get("RolloverStatement", "Student").isAcceptable()) {
        return false
    }
}

//Student Spouse rollover information auto code clearing logic
if (isirSPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirSPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirSPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirSPOUSE_IRAROLLOVER > 0) {
    if (receivedDocuments.hasDoc("RolloverStatement", "Student Spouse") && receivedDocuments.get("RolloverStatement", "Student Spouse").isAcceptable()) {
        return false
    }
}

//Student Foreign Income Exclusion auto code clearing logic
if (isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    if ((receivedDocuments.hasDoc("1040", "Student") && receivedDocuments.get("1040", "Student").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Student") && receivedDocuments.get("1040x", "Student").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Student") && receivedDocuments.get("TaxReturnTranscript", "Student").isAcceptable()) || (receivedDocuments.hasDoc("ForeignTaxTranscript", "Student") && receivedDocuments.get("ForeignTaxTranscript", "Student").isAcceptable())) {
        return false
    }
}

//Student Spouse Foreign Income Exclusion auto code clearing logic
if (isirSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    if ((receivedDocuments.hasDoc("1040", "Student Spouse") && receivedDocuments.get("1040", "Student Spouse").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Student Spouse") && receivedDocuments.get("1040x", "Student Spouse").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Student Spouse") && receivedDocuments.get("TaxReturnTranscript", "Student Spouse").isAcceptable()) || (receivedDocuments.hasDoc("ForeignTaxTranscript", "Student Spouse") && receivedDocuments.get("ForeignTaxTranscript", "Student Spouse").isAcceptable())) {
        return false
    }
}

//Family size auto code clearing logic
if (isirSTUDENT_UPDATEDFAMILYSIZE != null) {
    if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable()) {
        return false
    }
}

return true