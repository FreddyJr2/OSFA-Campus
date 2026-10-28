//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSTUDENT_MARITALSTATUS = isirRecord.getField(IsirField.STUDENT_MARITALSTATUS).getOrNull()

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1117") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1117").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1117")
    if (documentValue == "Single (Never married)" && isirSTUDENT_MARITALSTATUS != "1")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
    if (documentValue == "Married (Not separated)" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
    if (documentValue == "Remarried" && isirSTUDENT_MARITALSTATUS != "3")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
    if (documentValue == "Divorced" && isirSTUDENT_MARITALSTATUS != "4")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
    if (documentValue == "Separated" && isirSTUDENT_MARITALSTATUS != "5")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
    if (documentValue == "Widowed" && isirSTUDENT_MARITALSTATUS != "6")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1044") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1044").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1044")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1045") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1045").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1045")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1046") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1046").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1046")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1047") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1047").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1047")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1048") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1048").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1048")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1049") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1049").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1049")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1050") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1050").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1050")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1051") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1051").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1051")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1052") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1052").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1052")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("VW-Ind","Student") && receivedDocuments.get("VW-Ind","Student").isAcceptable() && receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1053") != null && !receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1053").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1053")
    if (documentValue == "Spouse" && isirSTUDENT_MARITALSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}