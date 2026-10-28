//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSTUDENT_FILED1040OR1040NR = isirRecord.getField(IsirField.STUDENT_FILED1040OR1040NR).getOrNull()
def docValue = ""

//1040 discrepancy rules
if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable()) {
    if (receivedDocuments.get("1040", "Student").getDocumentField("AC1084") != null && !receivedDocuments.get("1040", "Student").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("1040", "Student").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirSTUDENT_FILED1040OR1040NR == "2" || isirSTUDENT_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}

//1040x discrepancy rules
if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable()) {
    if (receivedDocuments.get("1040x", "Student").getDocumentField("AC1084") != null && !receivedDocuments.get("1040x", "Student").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("1040x", "Student").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirSTUDENT_FILED1040OR1040NR == "2" || isirSTUDENT_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}

//Tax Transcript discrepancy rules
if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable()) {
    if (receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1084") != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirSTUDENT_FILED1040OR1040NR == "2" || isirSTUDENT_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}