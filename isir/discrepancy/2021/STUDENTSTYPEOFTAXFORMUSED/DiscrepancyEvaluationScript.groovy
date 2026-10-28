//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirValue = isirRecord.getIsirFieldValue("STUDENTSTYPEOFTAXFORMUSED");

if (receivedDocuments.hasDoc("1040","Student") && receivedDocuments.get("1040","Student").isAcceptable())
{
	if (receivedDocuments.get("1040", "Student").getDocumentField("AC1084") != null && !receivedDocuments.get("1040", "Student").getDocumentField("AC1084").isAllWhitespace())
	{
		irs1040 = receivedDocuments.get("1040", "Student").getDocumentField("AC1084");
		if (irs1040 == "1040" && isirValue != "1")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "1";
			return;
		}     
 
		irs1040ForeignTaxTranscript = receivedDocuments.get("1040", "Student").getDocumentField("AC1084");
		if (irs1040ForeignTaxTranscript == "Foreign Tax Return" && isirValue != "3")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "3";
			return;
		}           
	  
		irs1040USTerritoryTaxReturn = receivedDocuments.get("1040", "Student").getDocumentField("AC1084");
		if (irs1040USTerritoryTaxReturn == "U.S. Territory Tax Return" && isirValue != "4")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "4";
			return;
		}   
	}
}          

if (receivedDocuments.hasDoc("TaxReturnTranscript","Student") && receivedDocuments.get("TaxReturnTranscript","Student").isAcceptable())
{
	if (receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1084") != null && !receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1084").isAllWhitespace())
	{
		irs1040 = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1084");
		if (irs1040 == "1040" && isirValue != "1")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "1";
			return;
		}   
 
		irs1040ForeignTaxTranscript = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1084");
		if (irs1040ForeignTaxTranscript == "Foreign Tax Return" && isirValue != "3")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "3";
			return;
		}           
	  
		irs1040USTerritoryTaxReturn = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1084");
		if (irs1040USTerritoryTaxReturn == "U.S. Territory Tax Return" && isirValue != "4")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "4";
			return;
		}          
	}
}

if (receivedDocuments.hasDoc("1040x","Student") && receivedDocuments.get("1040x","Student").isAcceptable())
{
	if (receivedDocuments.get("1040x", "Student").getDocumentField("AC1084") != null && !receivedDocuments.get("1040x", "Student").getDocumentField("AC1084").isAllWhitespace())
	{
		irs1040 = receivedDocuments.get("1040x", "Student").getDocumentField("AC1084");
		if (irs1040 == "1040" && isirValue != "1")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "1";
			return;
		}   
 
		irs1040ForeignTaxTranscript = receivedDocuments.get("1040x", "Student").getDocumentField("AC1084");
		if (irs1040ForeignTaxTranscript == "Foreign Tax Return" && isirValue != "3")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "3";
			return;
		}           
	  
		irs1040USTerritoryTaxReturn = receivedDocuments.get("1040x", "Student").getDocumentField("AC1084");
		if (irs1040USTerritoryTaxReturn == "U.S. Territory Tax Return" && isirValue != "4")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "4";
			return;
		}        
	}
}

if (receivedDocuments.hasDoc("ForeignTaxTranscript","Student") && receivedDocuments.get("ForeignTaxTranscript","Student").isAcceptable())
{
	if (receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1084") != null && !receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1084").isAllWhitespace())
	{
		irs1040 = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1084");
		if (irs1040 == "1040" && isirValue != "1")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "1";
			return;
		}    
 
		irs1040ForeignTaxTranscript = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1084");
		if (irs1040ForeignTaxTranscript == "Foreign Tax Return" && isirValue != "3")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "3";
			return;
		}           
	  
		irs1040USTerritoryTaxReturn = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1084");
		if (irs1040USTerritoryTaxReturn == "U.S. Territory Tax Return" && isirValue != "4")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "4";
			return;
		}            
	}
}    