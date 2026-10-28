//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirPARENT_MARITALSTATUS = isirRecord.getField(IsirField.PARENT_MARITALSTATUS).getOrNull()
def docValue = ""

if (receivedDocuments.hasDoc("VW-Dep","Student") && receivedDocuments.get("VW-Dep","Student").isAcceptable() && receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1119") != null && !receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1119").isAllWhitespace())
{
    docValue = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1119")
}

if (isirPARENT_MARITALSTATUS != "1" && docValue == "Never Married")
{
    discrepancyEvaluationResult.hasDiscrepancy = true
    discrepancyEvaluationResult.needManualReview = true
    return
}

if (isirPARENT_MARITALSTATUS != "2" && docValue == "Unmarried living together")
{
    discrepancyEvaluationResult.hasDiscrepancy = true
    discrepancyEvaluationResult.needManualReview = true
    return
}

if (isirPARENT_MARITALSTATUS != "3" && docValue == "Married (not separated)")
{
    discrepancyEvaluationResult.hasDiscrepancy = true
    discrepancyEvaluationResult.needManualReview = true
    return
}

if (isirPARENT_MARITALSTATUS != "4" && docValue == "Remarried")
{
    discrepancyEvaluationResult.hasDiscrepancy = true
    discrepancyEvaluationResult.needManualReview = true
    return
}

if (isirPARENT_MARITALSTATUS != "5" && docValue == "Divorced")
{
    discrepancyEvaluationResult.hasDiscrepancy = true
    discrepancyEvaluationResult.needManualReview = true
    return
}

if (isirPARENT_MARITALSTATUS != "6" && docValue == "Separated")
{
    discrepancyEvaluationResult.hasDiscrepancy = true
    discrepancyEvaluationResult.needManualReview = true
    return
}

if (isirPARENT_MARITALSTATUS != "7" && docValue == "Widowed")
{
    discrepancyEvaluationResult.hasDiscrepancy = true
    discrepancyEvaluationResult.needManualReview = true
    return
}