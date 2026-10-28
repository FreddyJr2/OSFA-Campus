//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirPARENT_FILED1040OR1040NR = isirRecord.getField(IsirField.PARENT_FILED1040OR1040NR).getOrNull()
def docValue = ""

//1040 Parent discrepancy rules
if (receivedDocuments.hasDoc("1040","Parent") && receivedDocuments.get("1040","Parent").isAcceptable()) {
    if (receivedDocuments.get("1040", "Parent").getDocumentField("AC1084") != null && !receivedDocuments.get("1040", "Parent").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("1040", "Parent").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirPARENT_FILED1040OR1040NR == "2" || isirPARENT_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}

//1040x Parent discrepancy rules
if (receivedDocuments.hasDoc("1040x","Parent") && receivedDocuments.get("1040x","Parent").isAcceptable()) {
    if (receivedDocuments.get("1040x", "Parent").getDocumentField("AC1084") != null && !receivedDocuments.get("1040x", "Parent").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("1040x", "Parent").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirPARENT_FILED1040OR1040NR == "2" || isirPARENT_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}

//Tax Transcript Parent discrepancy rules
if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent") && receivedDocuments.get("TaxReturnTranscript","Parent").isAcceptable()) {
    if (receivedDocuments.get("TaxReturnTranscript", "Parent").getDocumentField("AC1084") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("TaxReturnTranscript", "Parent").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirPARENT_FILED1040OR1040NR == "2" || isirPARENT_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}