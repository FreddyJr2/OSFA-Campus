//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirStudentISIRValueForCorrection = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");

String docValue = "";
if (receivedDocuments.hasDoc("VW-Dep","Student") && receivedDocuments.get("VW-Dep","Student").isAcceptable() && receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1097") != null && !receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1097").isAllWhitespace())
{
    docValue = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1097");
}

if (isirStudentISIRValueForCorrection != "1" && docValue == "Married/remarried")
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "1";
    return;
}

if (isirStudentISIRValueForCorrection != "2" && docValue == "Never married")
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "2";
    return;
}

if (isirStudentISIRValueForCorrection != "3" && docValue == "Divorced/Separated")
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "3";
    return;
}

if (isirStudentISIRValueForCorrection != "4" && docValue == "Widowed")
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "4";
    return;
}

if (isirStudentISIRValueForCorrection != "5" && docValue == "Unmarried and both parents living together")
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "5";
    return;
}