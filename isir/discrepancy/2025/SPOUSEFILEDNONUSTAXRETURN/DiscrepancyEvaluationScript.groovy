//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSPOUSE_FILEDNONUSTAXRETURN = isirRecord.getField(IsirField.SPOUSE_FILEDNONUSTAXRETURN).getOrNull()
def docValue = ""

//Foreign Tax Transcript discrepancy rules
if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student Spouse") && receivedDocuments.get("ForeignTaxTranscript","Student Spouse").isAcceptable()) {
    if (receivedDocuments.get("ForeignTaxTranscript", "Student Spouse").getDocumentField("AC1084") != null && !receivedDocuments.get("ForeignTaxTranscript", "Student Spouse").getDocumentField("AC1084").isAllWhitespace()) {
        docValue = receivedDocuments.get("ForeignTaxTranscript", "Student Spouse").getDocumentField("AC1084")
        if (docValue == "Foreign Tax Return" && (isirSPOUSE_FILEDNONUSTAXRETURN == "2" || isirSPOUSE_FILEDNONUSTAXRETURN == null)) {
            discrepancyEvaluationResult.hasDiscrepancy = true
            discrepancyEvaluationResult.needManualReview = true
            return
        }
    }
}