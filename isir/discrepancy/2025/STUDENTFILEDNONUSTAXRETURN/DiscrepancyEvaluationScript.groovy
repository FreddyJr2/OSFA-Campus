//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSTUDENT_FILEDNONUSTAXRETURN = isirRecord.getField(IsirField.STUDENT_FILEDNONUSTAXRETURN).getOrNull()
def docValue = ""

//Foreign Tax Transcript discrepancy rules
if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable()) {
    if (receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1084") != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1084")
        if (docValue == "Foreign Tax Return" && (isirSTUDENT_FILEDNONUSTAXRETURN == "2" || isirSTUDENT_FILEDNONUSTAXRETURN == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}