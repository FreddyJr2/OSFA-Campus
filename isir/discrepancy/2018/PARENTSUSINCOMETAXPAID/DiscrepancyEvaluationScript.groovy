//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirParentTaxPaid = isirRecord.getIsirFieldValue("PARENTSUSINCOMETAXPAID");
String isirParentMaritalStatus = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");

int isirParentTaxPaidValue = 0;
boolean isirParentTaxPaidBlank = true;
if (!isirParentTaxPaid.isAllWhitespace())
{
    isirParentTaxPaidValue = isirParentTaxPaid.toInteger();
    isirParentTaxPaidBlank = false;
}

int ac1016 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1016").toFloat().round();
        if (isirParentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent2") && receivedDocuments.get("TaxReturnTranscript","Parent2").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField("AC1016") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirParentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040","Parent1") && receivedDocuments.get("1040","Parent1").isAcceptable() && receivedDocuments.get("1040", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("1040", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("1040", "Parent1").getDocumentField("AC1016").toFloat().round();
        if (isirParentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("1040","Parent1") && receivedDocuments.get("1040","Parent1").isAcceptable() && receivedDocuments.get("1040", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("1040", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("1040", "Parent1").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040","Parent2") && receivedDocuments.get("1040","Parent2").isAcceptable() && receivedDocuments.get("1040", "Parent2").getDocumentField("AC1016") != null && !receivedDocuments.get("1040", "Parent2").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("1040", "Parent2").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirParentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040x","Parent1") && receivedDocuments.get("1040x","Parent1").isAcceptable() && receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1016").toFloat().round();
        if (isirParentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("1040x","Parent1") && receivedDocuments.get("1040x","Parent1").isAcceptable() && receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040x","Parent2") && receivedDocuments.get("1040x","Parent2").isAcceptable() && receivedDocuments.get("1040x", "Parent2").getDocumentField("AC1016") != null && !receivedDocuments.get("1040x", "Parent2").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("1040x", "Parent2").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirParentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Parent1") && receivedDocuments.get("SelfEmploymentStatement","Parent1").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1016").toFloat().round();
        if (isirParentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Parent1") && receivedDocuments.get("SelfEmploymentStatement","Parent1").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Parent2") && receivedDocuments.get("SelfEmploymentStatement","Parent2").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent2").getDocumentField("AC1016") != null && !receivedDocuments.get("SelfEmploymentStatement", "Parent2").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("SelfEmploymentStatement", "Parent2").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirParentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent1") && receivedDocuments.get("ForeignTaxTranscript","Parent1").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1016").toFloat().round();
        if (isirParentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent1") && receivedDocuments.get("ForeignTaxTranscript","Parent1").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1016") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent2") && receivedDocuments.get("ForeignTaxTranscript","Parent2").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField("AC1016") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirParentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;