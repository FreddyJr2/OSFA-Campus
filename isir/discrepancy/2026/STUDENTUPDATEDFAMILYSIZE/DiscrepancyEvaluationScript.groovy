//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSTUDENT_UPDATEDFAMILYSIZE = isirRecord.getField(IsirField.STUDENT_UPDATEDFAMILYSIZE).getOrNull()
def isirValueForCorrection = 0

if (isirSTUDENT_UPDATEDFAMILYSIZE != null)
{
    isirValueForCorrection = isirSTUDENT_UPDATEDFAMILYSIZE
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1022") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1022").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1022").toInteger()
    if (isirValueForCorrection != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.correctedValue = String.format("%02d",documentValue)
        return
    }
}