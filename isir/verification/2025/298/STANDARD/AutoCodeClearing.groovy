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
def isirFPS_INCARCERATEDAPPLICANTFLAG = isirRecord.getField(IsirField.FPS_INCARCERATEDAPPLICANTFLAG).getOrNull()
def isirSTUDENT_FILED1040OR1040NR = isirRecord.getField(IsirField.STUDENT_FILED1040OR1040NR).getOrNull()
def isirSTUDENT_FILEDNONUSTAXRETURN = isirRecord.getField(IsirField.STUDENT_FILEDNONUSTAXRETURN).getOrNull()
def isirSTUDENTFTIM_IRSRESPONSECODE_FTI = isirRecord.getField(IsirField.STUDENTFTIM_IRSRESPONSECODE_FTI).getOrNull()
def isirSTUDENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI = isirRecord.getField(IsirField.STUDENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI).getOrNull()
def isirSTUDENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI = isirRecord.getField(IsirField.STUDENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI).getOrNull()
def isirSTUDENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS = isirRecord.getField(IsirField.STUDENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS).getOrNull()
def isirSTUDENT_IRAROLLOVER = isirRecord.getField(IsirField.STUDENT_IRAROLLOVER).getOrNull()
def isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION = isirRecord.getField(IsirField.STUDENT_FOREIGNEARNEDINCOMEEXCLUSION).getOrNull()

//Parent ISIR variables
def isirPARENT_FILED1040OR1040NR = isirRecord.getField(IsirField.PARENT_FILED1040OR1040NR).getOrNull()
def isirPARENTFTIM_IRSRESPONSECODE_FTI = isirRecord.getField(IsirField.PARENTFTIM_IRSRESPONSECODE_FTI).getOrNull()
def isirPARENT_FILEDNONUSTAXRETURN = isirRecord.getField(IsirField.PARENT_FILEDNONUSTAXRETURN).getOrNull()
def isirPARENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI = isirRecord.getField(IsirField.PARENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI).getOrNull()
def isirPARENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI = isirRecord.getField(IsirField.PARENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI).getOrNull()
def isirPARENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS = isirRecord.getField(IsirField.PARENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS).getOrNull()
def isirPARENT_IRAROLLOVER = isirRecord.getField(IsirField.PARENT_IRAROLLOVER).getOrNull()
def isirPARENT_UPDATEDFAMILYSIZE = isirRecord.getField(IsirField.PARENT_UPDATEDFAMILYSIZE).getOrNull()
def isirFPS_ASSUMEDPARENTFAMILYSIZE = isirRecord.getField(IsirField.FPS_ASSUMEDPARENTFAMILYSIZE).getOrNull()
def isirPARENT_FOREIGNEARNEDINCOMEEXCLUSION = isirRecord.getField(IsirField.PARENT_FOREIGNEARNEDINCOMEEXCLUSION).getOrNull()

//Parent Spouse ISIR variables
def isirPARENTSPOUSE_IRAROLLOVER = isirRecord.getField(IsirField.PARENTSPOUSE_IRAROLLOVER).getOrNull()
def isirPARENTSPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS = isirRecord.getField(IsirField.PARENTSPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS).getOrNull()
def isirPARENTSPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI = isirRecord.getField(IsirField.PARENTSPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI).getOrNull()
def isirPARENTSPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI = isirRecord.getField(IsirField.PARENTSPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI).getOrNull()
def isirPARENTSPOUSE_FILED1040OR1040NR = isirRecord.getField(IsirField.PARENTSPOUSE_FILED1040OR1040NR).getOrNull()
def isirPARENTSPOUSEFTIM_IRSRESPONSECODE_FTI = isirRecord.getField(IsirField.PARENTSPOUSEFTIM_IRSRESPONSECODE_FTI).getOrNull()
def isirPARENTSPOUSE_FILEDNONUSTAXRETURN = isirRecord.getField(IsirField.PARENTSPOUSE_FILEDNONUSTAXRETURN).getOrNull()
def isirPARENTSPOUSE_TAXRETURNFILINGSTATUS = isirRecord.getField(IsirField.PARENTSPOUSE_TAXRETURNFILINGSTATUS).getOrNull()
def isirPARENTSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION = isirRecord.getField(IsirField.PARENTSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION).getOrNull()

//If student is incarcerated then exit the script
if (isirFPS_INCARCERATEDAPPLICANTFLAG == "1" || isirFPS_INCARCERATEDAPPLICANTFLAG == "2" || isirFPS_INCARCERATEDAPPLICANTFLAG == "3") {
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

//Parent auto code clearing logic
if ((isirPARENT_FILED1040OR1040NR == "1" || isirPARENT_FILED1040OR1040NR == null) && [null, "203", "206", "212", "214"].contains(isirPARENTFTIM_IRSRESPONSECODE_FTI)) {
    if ((receivedDocuments.hasDoc("1040", "Parent") && receivedDocuments.get("1040", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Parent") && receivedDocuments.get("1040x", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Parent") && receivedDocuments.get("TaxReturnTranscript", "Parent").isAcceptable())) {
       return false
    }
}
if ((isirPARENT_FILED1040OR1040NR == "1" || isirPARENT_FILED1040OR1040NR == null) && isirPARENT_FILEDNONUSTAXRETURN == "1") {
    if ((receivedDocuments.hasDoc("1040", "Parent") && receivedDocuments.get("1040", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Parent") && receivedDocuments.get("1040x", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Parent") && receivedDocuments.get("TaxReturnTranscript", "Parent").isAcceptable())) {
        return false
    }
}
if (isirPARENT_FILED1040OR1040NR == "2" && isirPARENT_FILEDNONUSTAXRETURN == "2") {
    if (receivedDocuments.hasDoc("ForeignTaxTranscript", "Parent") && receivedDocuments.get("ForeignTaxTranscript", "Parent").isAcceptable()) {
        return false
    }
}
if (isirPARENT_FILED1040OR1040NR == "2" && isirPARENT_FILEDNONUSTAXRETURN == "3") {
    if ((receivedDocuments.hasDoc("NonFilingStatement", "Parent") && receivedDocuments.get("NonFilingStatement", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("ForeignTaxTranscript", "Parent") && receivedDocuments.get("ForeignTaxTranscript", "Parent").isAcceptable())) {
        return false
    }
}
if (isirPARENT_FILED1040OR1040NR == "2" && ["4", "5", "6"].contains(isirPARENT_FILEDNONUSTAXRETURN)) {
    if ((receivedDocuments.hasDoc("NonFilingStatement", "Parent") && receivedDocuments.get("NonFilingStatement", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("W2", "Parent") && receivedDocuments.get("W2", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("1099G", "Parent") && receivedDocuments.get("1099G", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent") && receivedDocuments.get("SelfEmploymentStatement", "Parent").isAcceptable())) {
        return false
    }
}

//Parent Spouse auto code clearing logic
if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" &&  (isirPARENTSPOUSE_FILED1040OR1040NR == "1" || isirPARENTSPOUSE_FILED1040OR1040NR == null) && [null, "203", "206", "212", "214"].contains(isirPARENTSPOUSEFTIM_IRSRESPONSECODE_FTI)) {
    if ((receivedDocuments.hasDoc("1040", "Parent Spouse") && receivedDocuments.get("1040", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Parent Spouse") && receivedDocuments.get("1040x", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Parent Spouse") && receivedDocuments.get("TaxReturnTranscript", "Parent Spouse").isAcceptable())) {
        return false
    }
}
if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" &&  (isirPARENTSPOUSE_FILED1040OR1040NR == "1" || isirPARENTSPOUSE_FILED1040OR1040NR == null) && isirPARENTSPOUSE_FILEDNONUSTAXRETURN == "1") {
    if ((receivedDocuments.hasDoc("1040", "Parent Spouse") && receivedDocuments.get("1040", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Parent Spouse") && receivedDocuments.get("1040x", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Parent Spouse") && receivedDocuments.get("TaxReturnTranscript", "Parent Spouse").isAcceptable())) {
        return false
    }
}
if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" &&  isirPARENTSPOUSE_FILED1040OR1040NR == "2" && isirPARENTSPOUSE_FILEDNONUSTAXRETURN == "2") {
    if (receivedDocuments.hasDoc("ForeignTaxTranscript", "Parent Spouse") && receivedDocuments.get("ForeignTaxTranscript", "Parent Spouse").isAcceptable()) {
        return false
    }
}
if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" && isirPARENTSPOUSE_FILED1040OR1040NR == "2" && isirPARENTSPOUSE_FILEDNONUSTAXRETURN == "3") {
    if ((receivedDocuments.hasDoc("NonFilingStatement", "Parent Spouse") && receivedDocuments.get("NonFilingStatement", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("ForeignTaxTranscript", "Parent Spouse") && receivedDocuments.get("ForeignTaxTranscript", "Parent Spouse").isAcceptable())) {
        return false
    }
}
if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" && isirPARENTSPOUSE_FILED1040OR1040NR == "2" && ["4", "5", "6"].contains(isirPARENTSPOUSE_FILEDNONUSTAXRETURN)) {
    if ((receivedDocuments.hasDoc("NonFilingStatement", "Parent Spouse") && receivedDocuments.get("NonFilingStatement", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("W2", "Parent Spouse") && receivedDocuments.get("W2", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("1099G", "Parent Spouse") && receivedDocuments.get("1099G", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent Spouse") && receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").isAcceptable())) {
        return false
    }
}

//Student rollover information auto code clearing logic
if (isirSTUDENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirSTUDENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirSTUDENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirSTUDENT_IRAROLLOVER > 0) {
    if (receivedDocuments.hasDoc("RolloverStatement", "Student") && receivedDocuments.get("RolloverStatement", "Student").isAcceptable()) {
        return false
    }
}

//Parent rollover information auto code clearing logic
if (isirPARENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirPARENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirPARENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirPARENT_IRAROLLOVER > 0) {
    if (receivedDocuments.hasDoc("RolloverStatement", "Parent") && receivedDocuments.get("RolloverStatement", "Parent").isAcceptable()) {
        return false
    }
}

//Parent Spouse rollover information auto code clearing logic
if (isirPARENTSPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirPARENTSPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirPARENTSPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirPARENTSPOUSE_IRAROLLOVER > 0) {
    if (receivedDocuments.hasDoc("RolloverStatement", "Parent Spouse") && receivedDocuments.get("RolloverStatement", "Parent Spouse").isAcceptable()) {
        return false
    }
}

//Student Foreign Income Exclusion auto code clearing logic
if (isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    if ((receivedDocuments.hasDoc("1040", "Student") && receivedDocuments.get("1040", "Student").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Student") && receivedDocuments.get("1040x", "Student").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Student") && receivedDocuments.get("TaxReturnTranscript", "Student").isAcceptable()) || (receivedDocuments.hasDoc("ForeignTaxTranscript", "Student") && receivedDocuments.get("ForeignTaxTranscript", "Student").isAcceptable())) {
        return false
    }
}

//Parent Foreign Income Exclusion auto code clearing logic
if (isirPARENT_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirPARENT_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirPARENT_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    if ((receivedDocuments.hasDoc("1040", "Parent") && receivedDocuments.get("1040", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Parent") && receivedDocuments.get("1040x", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Parent") && receivedDocuments.get("TaxReturnTranscript", "Parent").isAcceptable()) || (receivedDocuments.hasDoc("ForeignTaxTranscript", "Parent") && receivedDocuments.get("ForeignTaxTranscript", "Parent").isAcceptable())) {
        return false
    }
}

//Parent Spouse Foreign Income Exclusion auto code clearing logic
if (isirPARENTSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirPARENTSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirPARENTSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    if ((receivedDocuments.hasDoc("1040", "Parent Spouse") && receivedDocuments.get("1040", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("1040x", "Parent Spouse") && receivedDocuments.get("1040x", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("TaxReturnTranscript", "Parent Spouse") && receivedDocuments.get("TaxReturnTranscript", "Parent Spouse").isAcceptable()) || (receivedDocuments.hasDoc("ForeignTaxTranscript", "Parent Spouse") && receivedDocuments.get("ForeignTaxTranscript", "Parent Spouse").isAcceptable())) {
        return false
    }
}

//Family size auto code clearing logic
if (isirPARENT_UPDATEDFAMILYSIZE != null) {
    if (receivedDocuments.hasDoc("VW-Dep", "Student") && receivedDocuments.get("VW-Dep", "Student").isAcceptable()) {
        return false
    }
}

return true