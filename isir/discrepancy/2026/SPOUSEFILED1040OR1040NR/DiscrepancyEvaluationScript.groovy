//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSPOUSE_FILED1040OR1040NR = isirRecord.getField(IsirField.SPOUSE_FILED1040OR1040NR).getOrNull()
def docValue = ""

//1040 discrepancy rules
if (receivedDocuments.hasDoc("1040","Student Spouse") && receivedDocuments.get("1040","Student Spouse").isAcceptable()) {
    if (receivedDocuments.get("1040", "Student Spouse").getDocumentField("AC1084") != null && !receivedDocuments.get("1040", "Student Spouse").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("1040", "Student Spouse").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirSPOUSE_FILED1040OR1040NR == "2" || isirSPOUSE_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.correctedValue = "1"
            return
        }
    }
}

//1040x discrepancy rules
if (receivedDocuments.hasDoc("1040x","Student Spouse") && receivedDocuments.get("1040x","Student Spouse").isAcceptable()) {
    if (receivedDocuments.get("1040x", "Student Spouse").getDocumentField("AC1084") != null && !receivedDocuments.get("1040x", "Student Spouse").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("1040x", "Student Spouse").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirSPOUSE_FILED1040OR1040NR == "2" || isirSPOUSE_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.correctedValue = "1"
            return
        }
    }
}

//Tax Transcript discrepancy rules
if (receivedDocuments.hasDoc("TaxReturnTranscript","Student Spouse") && receivedDocuments.get("TaxReturnTranscript","Student Spouse").isAcceptable()) {
    if (receivedDocuments.get("TaxReturnTranscript", "Student Spouse").getDocumentField("AC1084") != null && !receivedDocuments.get("TaxReturnTranscript", "Student Spouse").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("TaxReturnTranscript", "Student Spouse").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirSPOUSE_FILED1040OR1040NR == "2" || isirSPOUSE_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.correctedValue = "1"
            return
        }
    }
}