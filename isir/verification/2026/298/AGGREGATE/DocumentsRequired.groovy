//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationDocumentsRequiredScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

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
def isirPARENT_FOREIGNEARNEDINCOMEEXCLUSION = isirRecord.getField(IsirField.PARENT_FOREIGNEARNEDINCOMEEXCLUSION).getOrNull()
def isirPARENTFTIM_NUMBEROFEXEMPTIONS_FTI = isirRecord.getField(IsirField.PARENTFTIM_NUMBEROFEXEMPTIONS_FTI).getOrNull()
def isirPARENT_MARITALSTATUS = isirRecord.getField(IsirField.PARENT_MARITALSTATUS).getOrNull()

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

//Custom documents request logic
//documentRequest.addAnyDocuments(["HSDiploma", "Student"],["HSTranscript", "Student"],["GEDCert", "Student"],["GEDTranscript", "Student"],["StateHSEquivalencyCert", "Student"],["SecondarySchoolLeavingCert", "Student"],["HomeSchoolCert", "Student"])
documentRequest.addAnyDocuments(["DriversLicense", "Student"],["NonDriversLicenseID", "Student"],["StateIssuedID", "Student"],["Passport", "Student"])
//documentRequest.addAnyDocuments(["SOEP-Campus", "Student"],["SOEP-Notary", "Student"])

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

//Parent document request logic
if ((isirPARENT_FILED1040OR1040NR == "1" || isirPARENT_FILED1040OR1040NR == null) && [null, "203", "206", "212", "214"].contains(isirPARENTFTIM_IRSRESPONSECODE_FTI)) {
    documentRequest.addAnyDocuments(["1040", "Parent"], ["1040x", "Parent"], ["TaxReturnTranscript", "Parent"])
}
else if ((isirPARENT_FILED1040OR1040NR == "1" || isirPARENT_FILED1040OR1040NR == null) && isirPARENT_FILEDNONUSTAXRETURN == "1") {
    documentRequest.addAnyDocuments(["1040", "Parent"], ["1040x", "Parent"], ["TaxReturnTranscript", "Parent"])
}
else if (isirPARENT_FILED1040OR1040NR == "2" && isirPARENT_FILEDNONUSTAXRETURN == "2") {
    documentRequest.addDocument("ForeignTaxTranscript", "Parent")
}
else if (isirPARENT_FILED1040OR1040NR == "2" && isirPARENT_FILEDNONUSTAXRETURN == "3") {
    documentRequest.addAnyDocuments(["NonFilingStatement", "Parent"], ["ForeignTaxTranscript", "Parent"])
}
else if (isirPARENT_FILED1040OR1040NR == "2" && ["4", "5", "6"].contains(isirPARENT_FILEDNONUSTAXRETURN)) {
    documentRequest.addAnyDocuments(["NonFilingStatement", "Parent"], ["W2", "Parent"], ["1099G", "Parent"], ["SelfEmploymentStatement", "Parent"])
}

//Parent Spouse document request logic
if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" && (isirPARENTSPOUSE_FILED1040OR1040NR == "1" || isirPARENTSPOUSE_FILED1040OR1040NR == null) && [null, "203", "206", "212", "214"].contains(isirPARENTSPOUSEFTIM_IRSRESPONSECODE_FTI)) {
    documentRequest.addAnyDocuments(["1040", "Parent Spouse"], ["1040x", "Parent Spouse"], ["TaxReturnTranscript", "Parent Spouse"])
}
else if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" && (isirPARENTSPOUSE_FILED1040OR1040NR == "1" || isirPARENTSPOUSE_FILED1040OR1040NR == null) && isirPARENTSPOUSE_FILEDNONUSTAXRETURN == "1") {
    documentRequest.addAnyDocuments(["1040", "Parent Spouse"], ["1040x", "Parent Spouse"], ["TaxReturnTranscript", "Parent Spouse"])
}
else if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" && isirPARENTSPOUSE_FILED1040OR1040NR == "2" && isirPARENTSPOUSE_FILEDNONUSTAXRETURN == "2") {
    documentRequest.addDocument("ForeignTaxTranscript", "Parent Spouse")
}
else if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" && isirPARENTSPOUSE_FILED1040OR1040NR == "2" && isirPARENTSPOUSE_FILEDNONUSTAXRETURN == "3") {
    documentRequest.addAnyDocuments(["NonFilingStatement", "Parent Spouse"], ["ForeignTaxTranscript", "Parent Spouse"])
}
else if (isirPARENTSPOUSE_TAXRETURNFILINGSTATUS == "3" && isirPARENTSPOUSE_FILED1040OR1040NR == "2" && ["4", "5", "6"].contains(isirPARENTSPOUSE_FILEDNONUSTAXRETURN)) {
    documentRequest.addAnyDocuments(["NonFilingStatement", "Parent Spouse"], ["W2", "Parent Spouse"], ["1099G", "Parent Spouse"], ["SelfEmploymentStatement", "Parent Spouse"])
}

//Student rollover information document request logic
if (isirSTUDENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirSTUDENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirSTUDENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirSTUDENT_IRAROLLOVER > 0) {
    documentRequest.addDocument("RolloverStatement", "Student")
}

//Parent rollover information document request logic
if (isirPARENTFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirPARENTFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirPARENT_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirPARENT_IRAROLLOVER > 0) {
    documentRequest.addDocument("RolloverStatement", "Parent")
}

//Parent Spouse rollover information document request logic
if (isirPARENTSPOUSEFTIM_UNTAXEDIRADISTRIBUTIONS_FTI > 0 || isirPARENTSPOUSEFTIM_IRADEDUCTIBLEANDPAYMENTS_FTI > 0 || isirPARENTSPOUSE_UNTAXEDPORTIONSOFIRADISTRIBUTIONS > 0 || isirPARENTSPOUSE_IRAROLLOVER > 0) {
    documentRequest.addDocument("RolloverStatement", "Parent Spouse")
}

//Student Foreign Income Exclusion document request logic
if (isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirSTUDENT_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    documentRequest.addAnyDocuments(["1040", "Student"], ["1040x", "Student"], ["TaxReturnTranscript", "Student"], ["ForeignTaxTranscript", "Student"])
}

//Parent Foreign Income Exclusion document request logic
if (isirPARENT_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirPARENT_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirPARENT_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    documentRequest.addAnyDocuments(["1040", "Parent"], ["1040x", "Parent"], ["TaxReturnTranscript", "Parent"], ["ForeignTaxTranscript", "Parent"])
}

//Parent Spouse Foreign Income Exclusion document request logic
if (isirPARENTSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION != null && (isirPARENTSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION > 0 || isirPARENTSPOUSE_FOREIGNEARNEDINCOMEEXCLUSION < 0)) {
    documentRequest.addAnyDocuments(["1040", "Parent Spouse"], ["1040x", "Parent Spouse"], ["TaxReturnTranscript", "Parent Spouse"], ["ForeignTaxTranscript", "Parent Spouse"])
}

//Family size document request logic
if (isirPARENT_UPDATEDFAMILYSIZE == isirPARENTFTIM_NUMBEROFEXEMPTIONS_FTI) {
    //do not request a verification worksheet
    return
}
if (isirPARENT_UPDATEDFAMILYSIZE == null && isirPARENTFTIM_NUMBEROFEXEMPTIONS_FTI != null) {
    //do not request a verification worksheet
    return
}
if (isirPARENT_UPDATEDFAMILYSIZE != null) {
    if (isirPARENT_UPDATEDFAMILYSIZE == 2 && ["1", "5", "6", "7"].contains(isirPARENT_MARITALSTATUS)) {
        //do not request a verification worksheet
        return
    }
    if (isirPARENT_UPDATEDFAMILYSIZE == 3 && ["2", "3", "4"].contains(isirPARENT_MARITALSTATUS)) {
        //do not request a verification worksheet
        return
    }
    documentRequest.addDocument("VW-Dep", "Student")
}

return