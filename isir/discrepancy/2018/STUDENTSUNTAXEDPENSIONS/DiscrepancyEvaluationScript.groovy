//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirStudentUntaxedPensions = isirRecord.getIsirFieldValue("STUDENTSUNTAXEDPENSIONS");
String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");

int isirStudentUntaxedPensionsValue = 0;
boolean isirStudentUntaxedPensionsBlank = true;
if (!isirStudentUntaxedPensions.isAllWhitespace())
{
    isirStudentUntaxedPensionsValue = isirStudentUntaxedPensions.toInteger();
    isirStudentUntaxedPensionsBlank = false;
}

int ac1018 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("ac1018") != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("ac1018").isAllWhitespace())
    {
        ac1018 = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("ac1018").toFloat().round();
        if (isirStudentUntaxedPensionsValue != ac1018)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1018 = false;
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("ac1018") != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("ac1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("ac1018").toFloat().round();
    }
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Spouse") && receivedDocuments.get("TaxReturnTranscript","Spouse").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField("ac1018") != null && !receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField("ac1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField("AC1018").toFloat().round();
    }
    
    if (has1018 && isirStudentUntaxedPensionsValue != ac1018)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
        return;
    }
}
ac1018 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable() && receivedDocuments.get("1040", "Student").getDocumentField("AC1018") != null && !receivedDocuments.get("1040", "Student").getDocumentField("AC1018").isAllWhitespace())
    {
        ac1018 = receivedDocuments.get("1040", "Student").getDocumentField("AC1018").toFloat().round();
        if (isirStudentUntaxedPensionsValue != ac1018)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1018 = false;
    if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable() && receivedDocuments.get("1040", "Student").getDocumentField("AC1018") != null && !receivedDocuments.get("1040", "Student").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("1040", "Student").getDocumentField("AC1018").toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040","Spouse") && receivedDocuments.get("1040","Spouse").isAcceptable() && receivedDocuments.get("1040", "Spouse").getDocumentField("AC1018") != null && !receivedDocuments.get("1040", "Spouse").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("1040", "Spouse").getDocumentField("AC1018").toFloat().round();
    }
    
    if (has1018 && isirStudentUntaxedPensionsValue != ac1018)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
        return;
    }
}
ac1018 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable() && receivedDocuments.get("1040x", "Student").getDocumentField("AC1018") != null && !receivedDocuments.get("1040x", "Student").getDocumentField("AC1018").isAllWhitespace())
    {
        ac1018 = receivedDocuments.get("1040x", "Student").getDocumentField("AC1018").toFloat().round();
        if (isirStudentUntaxedPensionsValue != ac1018)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1018 = false;
    if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable() && receivedDocuments.get("1040x", "Student").getDocumentField("AC1018") != null && !receivedDocuments.get("1040x", "Student").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("1040x", "Student").getDocumentField("AC1018").toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040x","Spouse") && receivedDocuments.get("1040x","Spouse").isAcceptable() && receivedDocuments.get("1040x", "Spouse").getDocumentField("AC1018") != null && !receivedDocuments.get("1040x", "Spouse").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("1040x", "Spouse").getDocumentField("AC1018").toFloat().round();
    }
    
    if (has1018 && isirStudentUntaxedPensionsValue != ac1018)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
        return;
    }
}
ac1018 = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1018") != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1018").isAllWhitespace())
    {
        ac1018 = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1018").toFloat().round();
        if (isirStudentUntaxedPensionsValue != ac1018)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean has1018 = false;
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1018") != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1018").toFloat().round();
    }
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Spouse") && receivedDocuments.get("ForeignTaxTranscript","Spouse").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField("AC1018") != null && !receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField("AC1018").isAllWhitespace())
    {
        has1018 = true;
        ac1018 = ac1018 + receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField("AC1018").toFloat().round();
    }
    
    if (has1018 && isirStudentUntaxedPensionsValue != ac1018)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = String.format("%07d",ac1018);
        return;
    }
}
ac1018 = 0;