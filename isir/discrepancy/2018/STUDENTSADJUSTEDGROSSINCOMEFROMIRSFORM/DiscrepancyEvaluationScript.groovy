//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String documentFieldAC1015 = "AC1015";
String isirStudentISIRValueForCorrection = isirRecord.getIsirFieldValue("STUDENTSADJUSTEDGROSSINCOMEFROMIRSFORM");
String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");

int isirStudentISIRValueForCorrectionValue = 0;
boolean isirStudentISIRValueForCorrectionBlank = true;
if (!isirStudentISIRValueForCorrection.isAllWhitespace())
{
    isirStudentISIRValueForCorrectionValue = groovyScriptHelper.getIntegerFromSignedIsirNumber(isirStudentISIRValueForCorrection);
    isirStudentISIRValueForCorrectionBlank = false;
}

int documentValue = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable() && receivedDocuments.get("1040x", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("1040x", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("1040x", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
        if (isirStudentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable() && receivedDocuments.get("1040x", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("1040x", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("1040x", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040x","Spouse") && receivedDocuments.get("1040x","Spouse").isAcceptable() && receivedDocuments.get("1040x", "Spouse").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("1040x", "Spouse").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("1040x", "Spouse").getDocumentField(documentFieldAC1015).toFloat().round();
    }

    if (hasDocumentValue && isirStudentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
        return;
    }
}
documentValue = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
        if (isirStudentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    if (receivedDocuments.hasDoc("TaxReturnTranscript","Spouse") && receivedDocuments.get("TaxReturnTranscript","Spouse").isAcceptable() && receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("TaxReturnTranscript", "Spouse").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    
    if (hasDocumentValue && isirStudentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
        return;
    }
}
documentValue = 0;


if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable() && receivedDocuments.get("1040", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("1040", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("1040", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
        if (isirStudentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable() && receivedDocuments.get("1040", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("1040", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("1040", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    if (receivedDocuments.hasDoc("1040","Spouse") && receivedDocuments.get("1040","Spouse").isAcceptable() && receivedDocuments.get("1040", "Spouse").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("1040", "Spouse").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("1040", "Spouse").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    
    if (hasDocumentValue && isirStudentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
        return;
    }
}
documentValue = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
        if (isirStudentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    if (receivedDocuments.hasDoc("ForeignTaxTranscript","Spouse") && receivedDocuments.get("ForeignTaxTranscript","Spouse").isAcceptable() && receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("ForeignTaxTranscript", "Spouse").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    
    if (hasDocumentValue && isirStudentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
        return;
    }
}
documentValue = 0;

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Student") && receivedDocuments.get("SelfEmploymentStatement","Student").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        documentValue = receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
        if (isirStudentISIRValueForCorrectionValue != documentValue)
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
            return;
        }
    }
}
if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
{
    boolean hasDocumentValue = false;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Student") && receivedDocuments.get("SelfEmploymentStatement","Student").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    if (receivedDocuments.hasDoc("SelfEmploymentStatement","Spouse") && receivedDocuments.get("SelfEmploymentStatement","Spouse").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField(documentFieldAC1015) != null && !receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField(documentFieldAC1015).isAllWhitespace())
    {
        hasDocumentValue = true;
        documentValue = documentValue + receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField(documentFieldAC1015).toFloat().round();
    }
    
    if (hasDocumentValue && isirStudentISIRValueForCorrectionValue != documentValue)
    {
        discrepancyEvaluationResult.hasDiscrepancy = true;
        discrepancyEvaluationResult.correctedValue = groovyScriptHelper.getSignedIsirNumberFromInteger(documentValue,7);
        return;
    }
}
documentValue = 0;