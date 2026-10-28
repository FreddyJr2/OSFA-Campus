//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirPARENT_FILEDNONUSTAXRETURN = isirRecord.getField(IsirField.PARENT_FILEDNONUSTAXRETURN).getOrNull()
def docValue = ""

//Foreign Tax Transcript Parent discrepancy rules
if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent") && receivedDocuments.get("ForeignTaxTranscript","Parent").isAcceptable()) {
    if (receivedDocuments.get("ForeignTaxTranscript", "Parent").getDocumentField("AC1084") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("ForeignTaxTranscript", "Parent").getDocumentField("AC1084")
        if (docValue == "Foreign Tax Return" && isirPARENT_FILEDNONUSTAXRETURN != "2") {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}