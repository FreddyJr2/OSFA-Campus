//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirStudentTaxPaid = isirRecord.getIsirFieldValue("STUDENTSUSINCOMETAXPAID");
String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");

int isirStudentTaxPaidValue = 0;
boolean isirStudentTaxPaidBlank = true;
if (!isirStudentTaxPaid.isAllWhitespace())
{
    isirStudentTaxPaidValue = isirStudentTaxPaid.toInteger();
    isirStudentTaxPaidBlank = false;
}

int ac1016 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1016").toFloat().round();
        if (isirStudentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Spouse") && receivedDocuments.get("TaxReturnTranscript","Spouse").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField("AC1016") != null && !receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirStudentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable() && receivedDocuments.get("1040", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("1040", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("1040", "Student").getDocumentField("AC1016").toFloat().round();
        if (isirStudentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable() && receivedDocuments.get("1040", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("1040", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("1040", "Student").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040","Spouse") && receivedDocuments.get("1040","Spouse").isAcceptable() && receivedDocuments.get("1040", "Spouse").getDocumentField("AC1016") != null && !receivedDocuments.get("1040", "Spouse").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("1040", "Spouse").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirStudentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable() && receivedDocuments.get("1040x", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("1040x", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("1040x", "Student").getDocumentField("AC1016").toFloat().round();
        if (isirStudentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable() && receivedDocuments.get("1040x", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("1040x", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("1040x", "Student").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040x","Spouse") && receivedDocuments.get("1040x","Spouse").isAcceptable() && receivedDocuments.get("1040x", "Spouse").getDocumentField("AC1016") != null && !receivedDocuments.get("1040x", "Spouse").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("1040x", "Spouse").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirStudentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Student") && receivedDocuments.get("SelfEmploymentStatement","Student").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1016").toFloat().round();
        if (isirStudentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Student") && receivedDocuments.get("SelfEmploymentStatement","Student").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Spouse") && receivedDocuments.get("SelfEmploymentStatement","Spouse").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1016") != null && !receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirStudentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        ac1016 = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1016").toFloat().round();
        if (isirStudentTaxPaidValue != ac1016)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1016 = false;
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1016") != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1016").toFloat().round();
    }
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Spouse") && receivedDocuments.get("ForeignTaxTranscript","Spouse").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField("AC1016") != null && !receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField("AC1016").isAllWhitespace())
    {
        has1016 = true;
        ac1016 = ac1016 + receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField("AC1016").toFloat().round();
    }
    
    if (has1016 && isirStudentTaxPaidValue != ac1016)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1016);
        return;
    }
}
ac1016 = 0;