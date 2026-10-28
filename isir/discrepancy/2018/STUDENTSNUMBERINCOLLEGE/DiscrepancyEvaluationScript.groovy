//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String documentField = "AC1023";
String isirStudentISIRValueForCorrection = isirRecord.getIsirFieldValue("STUDENTSNUMBERINCOLLEGE");

int isirStudentISIRValueForCorrectionValue = 0;
if (!isirStudentISIRValueForCorrection.isAllWhitespace())
{
    isirStudentISIRValueForCorrectionValue = isirStudentISIRValueForCorrection.toInteger();
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField(documentField) != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField(documentField).isAllWhitespace())
{
    int documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField(documentField).toInteger();
    if (isirStudentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%01d",documentValue);
        return;
    }
}