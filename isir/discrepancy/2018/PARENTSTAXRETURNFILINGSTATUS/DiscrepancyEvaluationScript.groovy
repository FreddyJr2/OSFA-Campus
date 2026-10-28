//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirValue = isirRecord.getIsirFieldValue("PARENTSTAXRETURNFILINGSTATUS");

boolean hasTaxReturnTranscript = false;
if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable())
{
    hasTaxReturnTranscript = true;
}

boolean has1040 = false;
if (receivedDocuments.hasDoc("1040","Parent1") && receivedDocuments.get("1040","Parent1").isAcceptable())
{
    has1040 = true;
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

if (hasTaxReturnTranscript)
{
    String filingStatus = "";
    if (receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1065") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1065").isAllWhitespace())
    {
        filingStatus = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1065");
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
    if (receivedDocuments.get("1040", "Parent1").getDocumentField("AC1065") != null && !receivedDocuments.get("1040", "Parent1").getDocumentField("AC1065").isAllWhitespace())
    {
        filingStatus = receivedDocuments.get("1040", "Parent1").getDocumentField("AC1065");
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
    if (receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1065") != null && !receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1065").isAllWhitespace())
    {
        filingStatus = receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1065");
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
    if (receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1065") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1065").isAllWhitespace())
    {
        filingStatus = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1065");
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