//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirParentUntaxedPensions = isirRecord.getIsirFieldValue("PARENTSUNTAXEDPENSIONS");
String isirParentMaritalStatus = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");

int isirParentUntaxedPensionsValue = 0;
boolean isirParentUntaxedPensionsBlank = true;
if (!isirParentUntaxedPensions.isAllWhitespace())
{
    isirParentUntaxedPensionsValue = isirParentUntaxedPensions.toInteger();
    isirParentUntaxedPensionsBlank = false;
}

int ac1018 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("ac1018") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("ac1018").isAllWhitespace())
    {
        ac1018 = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("ac1018").toFloat().round();
        if (isirParentUntaxedPensionsValue != ac1018)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1018 = false;
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("ac1018") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("ac1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("ac1018").toFloat().round();
    }
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent2") && receivedDocuments.get("TaxReturnTranscript","Parent2").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField("ac1018") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField("ac1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField("AC1018").toFloat().round();
    }
    
    if (has1018 && isirParentUntaxedPensionsValue != ac1018)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
        return;
    }
}
ac1018 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040","Parent1") && receivedDocuments.get("1040","Parent1").isAcceptable() && receivedDocuments.get("1040", "Parent1").getDocumentField("AC1018") != null && !receivedDocuments.get("1040", "Parent1").getDocumentField("AC1018").isAllWhitespace())
    {
        ac1018 = receivedDocuments.get("1040", "Parent1").getDocumentField("AC1018").toFloat().round();
        if (isirParentUntaxedPensionsValue != ac1018)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1018 = false;
    if (receivedDocuments.hasDoc("1040","Parent1") && receivedDocuments.get("1040","Parent1").isAcceptable() && receivedDocuments.get("1040", "Parent1").getDocumentField("AC1018") != null && !receivedDocuments.get("1040", "Parent1").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("1040", "Parent1").getDocumentField("AC1018").toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040","Parent2") && receivedDocuments.get("1040","Parent2").isAcceptable() && receivedDocuments.get("1040", "Parent2").getDocumentField("AC1018") != null && !receivedDocuments.get("1040", "Parent2").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("1040", "Parent2").getDocumentField("AC1018").toFloat().round();
    }
    
    if (has1018 && isirParentUntaxedPensionsValue != ac1018)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
        return;
    }
}
ac1018 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040x","Parent1") && receivedDocuments.get("1040x","Parent1").isAcceptable() && receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1018") != null && !receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1018").isAllWhitespace())
    {
        ac1018 = receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1018").toFloat().round();
        if (isirParentUntaxedPensionsValue != ac1018)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1018 = false;
    if (receivedDocuments.hasDoc("1040x","Parent1") && receivedDocuments.get("1040x","Parent1").isAcceptable() && receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1018") != null && !receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1018").toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040x","Parent2") && receivedDocuments.get("1040x","Parent2").isAcceptable() && receivedDocuments.get("1040x", "Parent2").getDocumentField("AC1018") != null && !receivedDocuments.get("1040x", "Parent2").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("1040x", "Parent2").getDocumentField("AC1018").toFloat().round();
    }
    
    if (has1018 && isirParentUntaxedPensionsValue != ac1018)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
        return;
    }
}
ac1018 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent1") && receivedDocuments.get("ForeignTaxTranscript","Parent1").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1018") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1018").isAllWhitespace())
    {
        ac1018 = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1018").toFloat().round();
        if (isirParentUntaxedPensionsValue != ac1018)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1018 = false;
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent1") && receivedDocuments.get("ForeignTaxTranscript","Parent1").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1018") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1018").toFloat().round();
    }
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent2") && receivedDocuments.get("ForeignTaxTranscript","Parent2").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField("AC1018") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField("AC1018").toFloat().round();
    }
    
    if (has1018 && isirParentUntaxedPensionsValue != ac1018)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
        return;
    }
}
ac1018 = 0;