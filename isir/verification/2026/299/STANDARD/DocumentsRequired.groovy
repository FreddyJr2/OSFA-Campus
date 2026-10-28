//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationDocumentsRequiredScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

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
def isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION = isirRecord.getField(IsirField.STUDENT_FOREIGNEARNEDINCOMEEXCLUSION).getOrNull()
def isirSTUDENTFTIM_NUMBEROFEXEMPTIONS_FTI = isirRecord.getField(IsirField.STUDENTFTIM_NUMBEROFEXEMPTIONS_FTI).getOrNull()
def isirSTUDENT_MARITALSTATUS = isirRecord.getField(IsirField.STUDENT_MARITALSTATUS).getOrNull()

//Student Spouse ISIR variables
def isirSPOUSEFTIM_IRSRESPONSECODE_FTI = isirRecord.getField(IsirField.SPOUSEFTIM_IRSRESPONSECODE_FTI).getOrNull()
def isirSPOUSE_TAXRETURNFILINGSTATUS = isirRecord.getField(IsirField.SPOUSE_TAXRETURNFILINGSTATUS).getOrNull()
def isirSPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI = isirRecord.getField(IsirField.SPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI).getOrNull()
def isirSPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI = isirRecord.getField(IsirField.SPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI).getOrNull()
def isirSPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS = isirRecord.getField(IsirField.SPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS).getOrNull()
def isirSPOUSE_IRAROLLOVER = isirRecord.getField(IsirField.SPOUSE_IRAROLLOVER).getOrNull()
def isirSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION = isirRecord.getField(IsirField.SPOUSE_FOREIGNEARNEDINCOMEEXCLUSION).getOrNull()

//If student is incarcerated then exit the script
if (isirFPS_INCARCERATEDAPPLICANTFLAG == "1" || isirFPS_INCARCERATEDAPPLICANTFLAG == "2" || isirFPS_INCARCERATEDAPPLICANTFLAG == "3") {
    return
}

//Student document request logic
if ((isirSTUDENT_FILED1040OR1040NR == "1" || isirSTUDENT_FILED1040OR1040NR == null) && [null, "203", "206", "212", "214"].contains(isirSTUDENTFTIM_IRSRESPONSECODE_FTI)) {
    documentRequest.addAnyDocuments(["1040", "Student"], ["1040x", "Student"], ["TaxReturnTranscript", "Student"])
}
else if (isirSTUDENT_FILED1040OR1040NR == "2" && isirSTUDENT_FILEDNONUSTAXRETURN == "1") {
    documentRequest.addDocument("ForeignTaxTranscript", "Student")
}
else if (isirSTUDENT_FILED1040OR1040NR == "2" && (isirSTUDENT_FILEDNONUSTAXRETURN == "2" || isirSTUDENT_FILEDNONUSTAXRETURN == null)) {
    documentRequest.addAnyDocuments(["NonFilingStatement", "Student"], ["W2", "Student"], ["1099G", "Student"], ["SelfEmploymentStatement", "Student"])
}

//Student Spouse document request logic
if (isirSPOUSE_TAXRETURNFILINGSTATUS == "3" && [null, "203", "206", "212", "214"].contains(isirSPOUSEFTIM_IRSRESPONSECODE_FTI)) {
    documentRequest.addAnyDocuments(["1040", "Student Spouse"], ["1040x", "Student Spouse"], ["TaxReturnTranscript", "Student Spouse"])
}
else if (isirSPOUSE_TAXRETURNFILINGSTATUS == "3" && [null, "203", "206", "212", "214"].contains(isirSPOUSEFTIM_IRSRESPONSECODE_FTI) && isirSTUDENTFTIM_IRSRESPONSECODE_FTI == "200") {
    documentRequest.addAnyDocuments(["1040", "Student Spouse"], ["1040x", "Student Spouse"], ["TaxReturnTranscript", "Student Spouse"])
}

//Student rollover information document request logic
if (isirSTUDENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirSTUDENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirSTUDENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirSTUDENT_IRAROLLOVER > 0) {
    documentRequest.addDocument("RolloverStatement", "Student")
}

//Student Spouse rollover information document request logic
if (isirSPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirSPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirSPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirSPOUSE_IRAROLLOVER > 0) {
    documentRequest.addDocument("RolloverStatement", "Student Spouse")
}

//Student Foreign Income Exclusion document request logic
if (isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    documentRequest.addAnyDocuments(["1040", "Student"], ["1040x", "Student"], ["TaxReturnTranscript", "Student"], ["ForeignTaxTranscript", "Student"])
}

//Student Spouse Foreign Income Exclusion document request logic
if (isirSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    documentRequest.addAnyDocuments(["1040", "Student Spouse"], ["1040x", "Student Spouse"], ["TaxReturnTranscript", "Student Spouse"], ["ForeignTaxTranscript", "Student Spouse"])
}

//Family size document request logic
if (isirSTUDENT_UPDATEDFAMILYSIZE == isirSTUDENTFTIM_NUMBEROFEXEMPTIONS_FTI) {
    //do not request a verification worksheet
    return
}
if (isirSTUDENT_UPDATEDFAMILYSIZE == null && isirSTUDENTFTIM_NUMBEROFEXEMPTIONS_FTI != null) {
    //do not request a verification worksheet
    return
}
if (isirSTUDENT_UPDATEDFAMILYSIZE != null) {
    if (isirSTUDENT_UPDATEDFAMILYSIZE == 1 && ["1", "4", "5", "6"].contains(isirSTUDENT_MARITALSTATUS)) {
        //do not request a verification worksheet
        return
    }
    if (isirSTUDENT_UPDATEDFAMILYSIZE == 2 && ["2", "3"].contains(isirSTUDENT_MARITALSTATUS)) {
        //do not request a verification worksheet
        return
    }
    documentRequest.addDocument("VW-Ind", "Student")
}

return