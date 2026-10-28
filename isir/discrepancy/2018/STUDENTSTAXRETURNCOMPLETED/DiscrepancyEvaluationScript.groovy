//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirValue = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNCOMPLETED");

boolean hasTaxReturnTranscript = false;
if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable())
{
    hasTaxReturnTranscript = true;
}

boolean has1040x = false;
if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable())
{
    has1040x = true;
}

boolean hasForeignTaxTranscript = false;
if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable())
{
    hasForeignTaxTranscript = true;
}

boolean hasIRSForm4868 = false;
if (receivedDocuments.hasDoc("IRSForm4868","Student") && receivedDocuments.get("IRSForm4868","Student").isAcceptable())
{
    hasIRSForm4868 = true;
}

boolean hasW2Student = false;
if (receivedDocuments.hasDoc("W2", "Student") && receivedDocuments.get("W2", "Student").isAcceptable())
{
    hasW2Student = true;
}

boolean has1099GStudent = false;
if (receivedDocuments.hasDoc("1099G", "Student") && receivedDocuments.get("1099G", "Student").isAcceptable())
{
    has1099GStudent = true;
}

boolean hasNonFilingStudent = false;
if (receivedDocuments.hasDoc("NonFilingStatement", "Student") && receivedDocuments.get("NonFilingStatement", "Student").isAcceptable())
{
    hasNonFilingStudent = true;
}

boolean hasSelfEmploymentStatementStudent = false;
if (receivedDocuments.hasDoc("SelfEmploymentStatement","Student") && receivedDocuments.get("SelfEmploymentStatement","Student").isAcceptable())
{
    hasSelfEmploymentStatementStudent = true;
}

int w2SumStudent = 0;
float w2SumStudentFloat = 0.0;
def w2DocsStudent = receivedDocuments.getDocumentsForDocCode("W2", "Student");
for (doc in w2DocsStudent)
{
    String ac1010 = doc.getDocumentField("AC1010");
    float ac1010Value = 0.0;
    if (ac1010 != null && !ac1010.isAllWhitespace())
    {
        ac1010Value = ac1010.toFloat();
    }
    w2SumStudentFloat = w2SumStudentFloat + ac1010Value;
}
w2SumStudent = w2SumStudentFloat.round();
    
int doc1099SumStudent = 0;
float doc1099StudentFloat = 0.0;
def doc1099GDocsStudent = receivedDocuments.getDocumentsForDocCode("1099G", "Student");
for (doc in doc1099GDocsStudent)
{
    String ac1013 = doc.getDocumentField("AC1013");
    float ac1013Value = 0.0;
    if (ac1013 != null && !ac1013.isAllWhitespace())
    {
        ac1013Value = ac1013.toFloat();
    }
    doc1099StudentFloat = doc1099StudentFloat + ac1013Value;
}
doc1099SumStudent = doc1099StudentFloat.round();

int ac1015 = 0;
if (receivedDocuments.hasDoc("SelfEmploymentStatement","Student") && receivedDocuments.get("SelfEmploymentStatement","Student").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015") != null && receivedDocuments.hasDoc("TaxReturnTranscript","Student") && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1015").isAllWhitespace())
{
    ac1015 = receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").toFloat().round();
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

if (isirValue.isAllWhitespace() && hasNonFilingStudent)
{
    
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "3";
    return;
}