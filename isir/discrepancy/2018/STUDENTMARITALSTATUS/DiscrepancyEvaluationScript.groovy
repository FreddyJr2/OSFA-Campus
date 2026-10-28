//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirStudentISIRValueForCorrection = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1093") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1093").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1093");
    if (documentValue == "Single" && isirStudentISIRValueForCorrection != "1")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "1";
        return;
    }
    if (documentValue == "Married/remarried" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
    if (documentValue == "Separated" && isirStudentISIRValueForCorrection != "3")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "3";
        return;
    }
    if (documentValue == "Divorced" && isirStudentISIRValueForCorrection != "4")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "4";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1044") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1044").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1044");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1045") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1045").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1045");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1046") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1046").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1046");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1047") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1047").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1047");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1048") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1048").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1048");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1049") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1049").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1049");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1050") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1050").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1050");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1051") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1051").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1051");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1052") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1052").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1052");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1053") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1053").isAllWhitespace())
{
    String documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1053");
    if (documentValue == "Spouse" && isirStudentISIRValueForCorrection != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = "2";
        return;
    }
}