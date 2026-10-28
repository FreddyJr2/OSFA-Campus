//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirPARENT_UPDATEDFAMILYSIZE = isirRecord.getField(IsirField.PARENT_UPDATEDFAMILYSIZE).getOrNull()
def isirValueForCorrection = 0

if (isirPARENT_UPDATEDFAMILYSIZE != null)
{
    isirValueForCorrection = isirPARENT_UPDATEDFAMILYSIZE
}

if (receivedDocuments.hasDoc("VW-Dep","Student") && receivedDocuments.get("VW-Dep","Student").isAcceptable() && receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1098") != null && !receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1098").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1098").toInteger()
    if (isirValueForCorrection != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}