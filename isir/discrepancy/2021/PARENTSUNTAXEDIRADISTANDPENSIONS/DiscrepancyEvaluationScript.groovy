//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String documentFieldAC1017 = "AC1017";
String isirParentISIRValueForCorrection = isirRecord.getIsirFieldValue("PARENTSUNTAXEDIRADISTANDPENSIONS");
String isirParentMaritalStatus = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");

int isirParentISIRValueForCorrectionValue = 0;
boolean isirParentISIRValueForCorrectionBlank = true;
if (!isirParentISIRValueForCorrection.isAllWhitespace())
{
    isirParentISIRValueForCorrectionValue = isirParentISIRValueForCorrection.toInteger();
    isirParentISIRValueForCorrectionBlank = false;
}

int documentValue = 0;


if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField(documentFieldAC1017).toFloat().round();
        if (isirParentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",documentValue);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField(documentFieldAC1017).toFloat().round();
    }
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent2") && receivedDocuments.get("TaxReturnTranscript","Parent2").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("TaxReturnTranscript", "Parent2").getDocumentField(documentFieldAC1017).toFloat().round();
    }
    
    if (hasDocumentValue && isirParentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",documentValue);
        return;
    }
}
documentValue = 0;


if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040","Parent1") && receivedDocuments.get("1040","Parent1").isAcceptable() && receivedDocuments.get("1040", "Parent1").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("1040", "Parent1").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("1040", "Parent1").getDocumentField(documentFieldAC1017).toFloat().round();
        if (isirParentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",documentValue);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("1040","Parent1") && receivedDocuments.get("1040","Parent1").isAcceptable() && receivedDocuments.get("1040", "Parent1").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("1040", "Parent1").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("1040", "Parent1").getDocumentField(documentFieldAC1017).toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040","Parent2") && receivedDocuments.get("1040","Parent2").isAcceptable() && receivedDocuments.get("1040", "Parent2").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("1040", "Parent2").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("1040", "Parent2").getDocumentField(documentFieldAC1017).toFloat().round();
    }
    
    if (hasDocumentValue && isirParentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",documentValue);
        return;
    }
}
documentValue = 0;


if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040x","Parent1") && receivedDocuments.get("1040x","Parent1").isAcceptable() && receivedDocuments.get("1040x", "Parent1").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("1040x", "Parent1").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("1040x", "Parent1").getDocumentField(documentFieldAC1017).toFloat().round();
        if (isirParentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",documentValue);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("1040x","Parent1") && receivedDocuments.get("1040x","Parent1").isAcceptable() && receivedDocuments.get("1040x", "Parent1").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("1040x", "Parent1").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("1040x", "Parent1").getDocumentField(documentFieldAC1017).toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040x","Parent2") && receivedDocuments.get("1040x","Parent2").isAcceptable() && receivedDocuments.get("1040x", "Parent2").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("1040x", "Parent2").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("1040x", "Parent2").getDocumentField(documentFieldAC1017).toFloat().round();
    }
    
    if (hasDocumentValue && isirParentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",documentValue);
        return;
    }
}
documentValue = 0;

if (isirParentMaritalStatus == "4" || isirParentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent1") && receivedDocuments.get("ForeignTaxTranscript","Parent1").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField(documentFieldAC1017).toFloat().round();
        if (isirParentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",documentValue);
            return;
        }
    }
}
if (isirParentMaritalStatus == "1" || isirParentMaritalStatus == "2" || isirParentMaritalStatus == "3" || isirParentMaritalStatus == "5")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent1") && receivedDocuments.get("ForeignTaxTranscript","Parent1").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField(documentFieldAC1017).toFloat().round();
    }
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent2") && receivedDocuments.get("ForeignTaxTranscript","Parent2").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField(documentFieldAC1017) != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField(documentFieldAC1017).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("ForeignTaxTranscript", "Parent2").getDocumentField(documentFieldAC1017).toFloat().round();
    }
    
    if (hasDocumentValue && isirParentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",documentValue);
        return;
    }
}
documentValue = 0;