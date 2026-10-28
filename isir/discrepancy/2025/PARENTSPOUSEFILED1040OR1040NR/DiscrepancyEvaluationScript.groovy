//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirPARENTSPOUSE_FILED1040OR1040NR = isirRecord.getField(IsirField.PARENTSPOUSE_FILED1040OR1040NR).getOrNull()
def docValue = ""

//1040 Parent Spouse discrepancy rules
if (receivedDocuments.hasDoc("1040","Parent Spouse") && receivedDocuments.get("1040","Parent Spouse").isAcceptable()) {
    if (receivedDocuments.get("1040", "Parent Spouse").getDocumentField("AC1084") != null && !receivedDocuments.get("1040", "Parent Spouse").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("1040", "Parent Spouse").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirPARENTSPOUSE_FILED1040OR1040NR == "2" || isirPARENTSPOUSE_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}

//1040x Parent Spouse discrepancy rules
if (receivedDocuments.hasDoc("1040x","Parent Spouse") && receivedDocuments.get("1040x","Parent Spouse").isAcceptable()) {
    if (receivedDocuments.get("1040x", "Parent Spouse").getDocumentField("AC1084") != null && !receivedDocuments.get("1040x", "Parent Spouse").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("1040x", "Parent Spouse").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirPARENTSPOUSE_FILED1040OR1040NR == "2" || isirPARENTSPOUSE_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}

//Tax Transcript Parent Spouse discrepancy rules
if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent Spouse") && receivedDocuments.get("TaxReturnTranscript","Parent Spouse").isAcceptable()) {
    if (receivedDocuments.get("TaxReturnTranscript", "Parent Spouse").getDocumentField("AC1084") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent Spouse").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("TaxReturnTranscript", "Parent Spouse").getDocumentField("AC1084")
        if ((docValue == "1040" || docValue == "1040NR") && (isirPARENTSPOUSE_FILED1040OR1040NR == "2" || isirPARENTSPOUSE_FILED1040OR1040NR == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}