//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirValue = isirRecord.getIsirFieldValue("PARENTSTAXRETURNCOMPLETED");

boolean hasTaxReturnTranscript = false;
if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable())
{
    hasTaxReturnTranscript = true;
}

boolean has1040x = false;
if (receivedDocuments.hasDoc("1040x","Parent1") && receivedDocuments.get("1040x","Parent1").isAcceptable())
{
    has1040x = true;
}

boolean hasForeignTaxTranscript = false;
if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent1") && receivedDocuments.get("ForeignTaxTranscript","Parent1").isAcceptable())
{
    hasForeignTaxTranscript = true;
}

boolean hasIRSForm4868 = false;
if (receivedDocuments.hasDoc("IRSForm4868","Parent1") && receivedDocuments.get("IRSForm4868","Parent1").isAcceptable())
{
    hasIRSForm4868 = true;
}

boolean hasW2Parent1 = false;
if (receivedDocuments.hasDoc("W2", "Parent1") && receivedDocuments.get("W2", "Parent1").isAcceptable())
{
    hasW2Parent1 = true;
}

boolean has1099GParent1 = false;
if (receivedDocuments.hasDoc("1099G", "Parent1") && receivedDocuments.get("1099G", "Parent1").isAcceptable())
{
    has1099GParent1 = true;
}

boolean hasNonFilingParent1 = false;
if (receivedDocuments.hasDoc("NonFilingStatement", "Parent1") && receivedDocuments.get("NonFilingStatement", "Parent1").isAcceptable())
{
    hasNonFilingParent1 = true;
}

boolean hasSelfEmploymentStatementParent1 = false;
if (receivedDocuments.hasDoc("SelfEmploymentStatement","Parent1") && receivedDocuments.get("SelfEmploymentStatement","Parent1").isAcceptable())
{
    hasSelfEmploymentStatementParent1 = true;
}

int w2SumParent1 = 0;
float w2SumParent1Float = 0.0;
def w2DocsParent1 = receivedDocuments.getDocumentsForDocCode("W2", "Parent1");
for (doc in w2DocsParent1)
{
    String ac1010 = doc.getDocumentField("AC1010");
    float ac1010Value = 0.0;
    if (ac1010 != null && !ac1010.isAllWhitespace())
    {
        ac1010Value = ac1010.toFloat();
    }
    w2SumParent1Float = w2SumParent1Float + ac1010Value;
}
w2SumParent1 = w2SumParent1Float.round();
    
int doc1099SumParent1 = 0;
float doc1099Parent1Float = 0.0;
def doc1099GDocsParent1 = receivedDocuments.getDocumentsForDocCode("1099G", "Parent1");
for (doc in doc1099GDocsParent1)
{
    String ac1013 = doc.getDocumentField("AC1013");
    float ac1013Value = 0.0;
    if (ac1013 != null && !ac1013.isAllWhitespace())
    {
        ac1013Value = ac1013.toFloat();
    }
    doc1099Parent1Float = doc1099Parent1Float + ac1013Value;
}
doc1099SumParent1 = doc1099Parent1Float.round();

int ac1015 = 0;
if (receivedDocuments.hasDoc("SelfEmploymentStatement","Parent1") && receivedDocuments.get("SelfEmploymentStatement","Parent1").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1015") != null && receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1015").isAllWhitespace())
{
    ac1015 = receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1015").toFloat().round();
}

/* Correction Logic */
if (isirValue != "1" && (hasTaxReturnTranscript || has1040x || hasForeignTaxTranscript))
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "1";
    return;
}

if ((isirValue.isAllWhitespace() || isirValue == "3") && hasIRSForm4868)
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "2";
    return;
}

if (isirValue.isAllWhitespace() && hasNonFilingParent1)
{
    
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "3";
    return;
}