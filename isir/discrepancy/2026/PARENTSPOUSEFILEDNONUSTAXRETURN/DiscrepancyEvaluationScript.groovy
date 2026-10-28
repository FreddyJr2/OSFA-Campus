//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirPARENTSPOUSE_FILEDNONUSTAXRETURN = isirRecord.getField(IsirField.PARENTSPOUSE_FILEDNONUSTAXRETURN).getOrNull()
def docValue = ""

//Foreign Tax Transcript Parent Spouse discrepancy rules
if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent Spouse") && receivedDocuments.get("ForeignTaxTranscript","Parent Spouse").isAcceptable()) {
    if (receivedDocuments.get("ForeignTaxTranscript", "Parent Spouse").getDocumentField("AC1084") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent Spouse").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("ForeignTaxTranscript", "Parent Spouse").getDocumentField("AC1084")
        if (docValue == "Foreign Tax Return" && isirPARENTSPOUSE_FILEDNONUSTAXRETURN != "2") {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.correctedValue = "2"
            return
        }
    }
}