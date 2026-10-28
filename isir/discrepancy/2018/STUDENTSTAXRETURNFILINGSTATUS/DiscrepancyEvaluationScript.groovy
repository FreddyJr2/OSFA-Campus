//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirValue = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNFILINGSTATUS");

boolean hasTaxReturnTranscript = false;
if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable())
{
    hasTaxReturnTranscript = true;
}

boolean has1040 = false;
if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable())
{
    has1040 = true;
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

if (hasTaxReturnTranscript)
{
    String filingStatus = "";
    if (receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1065") != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1065").isAllWhitespace())
    {
        filingStatus = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1065");
        if (filingStatus == "Single" && isirValue != "1")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "1";
            return;
        }
        if (filingStatus == "Married Filing Jointly" && isirValue != "2")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "2";
            return;
        }
        if (filingStatus == "Married Filing Separately" && isirValue != "3")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "3";
            return;
        }
        if (filingStatus == "Head of Household" && isirValue != "4")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "4";
            return;
        }
        if (filingStatus == "Qualifying Widower w/ Dependent" && isirValue != "5")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "5";
            return;
        }
    }
}

if (has1040)
{
    String filingStatus = "";
    if (receivedDocuments.get("1040", "Student").getDocumentField("AC1065") != null && !receivedDocuments.get("1040", "Student").getDocumentField("AC1065").isAllWhitespace())
    {
        filingStatus = receivedDocuments.get("1040", "Student").getDocumentField("AC1065");
        if (filingStatus == "Single" && isirValue != "1")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "1";
            return;
        }
        if (filingStatus == "Married Filing Jointly" && isirValue != "2")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "2";
            return;
        }
        if (filingStatus == "Married Filing Separately" && isirValue != "3")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "3";
            return;
        }
        if (filingStatus == "Head of Household" && isirValue != "4")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "4";
            return;
        }
        if (filingStatus == "Qualifying Widower w/ Dependent" && isirValue != "5")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "5";
            return;
        }
    }
}

if (has1040x)
{
    String filingStatus = "";
    if (receivedDocuments.get("1040x", "Student").getDocumentField("AC1065") != null && !receivedDocuments.get("1040x", "Student").getDocumentField("AC1065").isAllWhitespace())
    {
        filingStatus = receivedDocuments.get("1040x", "Student").getDocumentField("AC1065");
        if (filingStatus == "Single" && isirValue != "1")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "1";
            return;
        }
        if (filingStatus == "Married Filing Jointly" && isirValue != "2")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "2";
            return;
        }
        if (filingStatus == "Married Filing Separately" && isirValue != "3")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "3";
            return;
        }
        if (filingStatus == "Head of Household" && isirValue != "4")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "4";
            return;
        }
        if (filingStatus == "Qualifying Widower w/ Dependent" && isirValue != "5")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "5";
            return;
        }
    }
}

if (hasForeignTaxTranscript)
{
    String filingStatus = "";
    if (receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1065") != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1065").isAllWhitespace())
    {
        filingStatus = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1065");
        if (filingStatus == "Single" && isirValue != "1")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "1";
            return;
        }
        if (filingStatus == "Married Filing Jointly" && isirValue != "2")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "2";
            return;
        }
        if (filingStatus == "Married Filing Separately" && isirValue != "3")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "3";
            return;
        }
        if (filingStatus == "Head of Household" && isirValue != "4")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "4";
            return;
        }
        if (filingStatus == "Qualifying Widower w/ Dependent" && isirValue != "5")
        {
            discrepancyEvaluationResult.hasDiscrepancy = true;
            discrepancyEvaluationResult.correctedValue = "5";
            return;
        }
    }
}