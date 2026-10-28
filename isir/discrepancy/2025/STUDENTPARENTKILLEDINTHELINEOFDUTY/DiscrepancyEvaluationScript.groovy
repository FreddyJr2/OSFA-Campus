//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSTUDENT_PARENTKILLEDINTHELINEOFDUTY = isirRecord.getField(IsirField.STUDENT_PARENTKILLEDINTHELINEOFDUTY).getOrNull()
def isirFPS_IASGINDICATOR = isirRecord.getField(IsirField.FPS_IASGINDICATOR).getOrNull()
def isirFPS_CHILDRENOFFALLENHEROESINDICATOR = isirRecord.getField(IsirField.FPS_CHILDRENOFFALLENHEROESINDICATOR).getOrNull()

//If the student has identified parent was killed in line of duty, and the IASG indicator/children of fallen heroes indicator are not in an eligible state
if (isirSTUDENT_PARENTKILLEDINTHELINEOFDUTY == "1" && !["1", "2"].contains(isirFPS_IASGINDICATOR) && !["1", "2"].contains(isirFPS_CHILDRENOFFALLENHEROESINDICATOR))
{
    //If document exists and is acceptable, then create a manual review task
    if (receivedDocuments.hasDoc("IASG-CHF-SelfDeclaration") && receivedDocuments.get("IASG-CHF-SelfDeclaration","Student").isAcceptable())
    {
        discrepancyEvaluationResult.needManualReview = true
    }
    //If document does not exist or is not acceptable, then create a discrepancy
    else
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
    }
}
return