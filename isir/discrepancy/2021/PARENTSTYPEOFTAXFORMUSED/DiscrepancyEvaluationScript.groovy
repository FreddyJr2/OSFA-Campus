//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirValue = isirRecord.getIsirFieldValue("PARENTSTYPEOFTAXFORMUSED");

if (receivedDocuments.hasDoc("1040","Parent1") && receivedDocuments.get("1040","Parent1").isAcceptable())
{
	if (receivedDocuments.get("1040", "Parent1").getDocumentField("AC1084") != null && !receivedDocuments.get("1040", "Parent1").getDocumentField("AC1084").isAllWhitespace())
	{
		irs1040 = receivedDocuments.get("1040", "Parent1").getDocumentField("AC1084");
		if (irs1040 == "1040" && isirValue != "1")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "1";
			return;
		}
		 
		irs1040A = receivedDocuments.get("1040", "Parent1").getDocumentField("AC1084");
		if (irs1040A == "1040A" && isirValue != "2")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			/*discrepancyEvaluationResult.correctedValue = "2";*/
			return;
		}  
 
		irs1040EZ = receivedDocuments.get("1040", "Parent1").getDocumentField("AC1084");
		if (irs1040EZ == "1040EZ" && isirValue != "2")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			/*discrepancyEvaluationResult.correctedValue = "2";*/
			return;
		}        
 
		irs1040ForeignTaxTranscript = receivedDocuments.get("1040", "Parent1").getDocumentField("AC1084");
		if (irs1040ForeignTaxTranscript == "Foreign Tax Return" && isirValue != "3")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "3";
			return;
		}           
	  
		irs1040USTerritoryTaxReturn = receivedDocuments.get("1040", "Parent1").getDocumentField("AC1084");
		if (irs1040USTerritoryTaxReturn == "U.S. Territory Tax Return" && isirValue != "4")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "4";
			return;
		}   
	}
}          

if (receivedDocuments.hasDoc("TaxReturnTranscript","Parent1") && receivedDocuments.get("TaxReturnTranscript","Parent1").isAcceptable())
{
	if (receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1084") != null && !receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1084").isAllWhitespace())
	{
		irs1040 = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040 == "1040" && isirValue != "1")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "1";
			return;
		}
		 
		irs1040A = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040A == "1040A" && isirValue != "2")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			/*discrepancyEvaluationResult.correctedValue = "2";*/
			return;
		}    
 
		irs1040EZ = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040EZ == "1040EZ" && isirValue != "2")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			/*discrepancyEvaluationResult.correctedValue = "2";*/
			return;
		}        
 
		irs1040ForeignTaxTranscript = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040ForeignTaxTranscript == "Foreign Tax Return" && isirValue != "3")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "3";
			return;
		}           
	  
		irs1040USTerritoryTaxReturn = receivedDocuments.get("TaxReturnTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040USTerritoryTaxReturn == "U.S. Territory Tax Return" && isirValue != "4")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "4";
			return;
		}          
	}
}

if (receivedDocuments.hasDoc("1040x","Parent1") && receivedDocuments.get("1040x","Parent1").isAcceptable())
{
	if (receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1084") != null && !receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1084").isAllWhitespace())
	{
		irs1040 = receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1084");
		if (irs1040 == "1040" && isirValue != "1")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "1";
			return;
		}
		 
		irs1040A = receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1084");
		if (irs1040A == "1040A" && isirValue != "2")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			/*discrepancyEvaluationResult.correctedValue = "2";*/
			return;
		}    
 
		irs1040EZ = receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1084");
		if (irs1040EZ == "1040EZ" && isirValue != "2")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			/*discrepancyEvaluationResult.correctedValue = "2";*/
			return;
		}        
 
		irs1040ForeignTaxTranscript = receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1084");
		if (irs1040ForeignTaxTranscript == "Foreign Tax Return" && isirValue != "3")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "3";
			return;
		}           
	  
		irs1040USTerritoryTaxReturn = receivedDocuments.get("1040x", "Parent1").getDocumentField("AC1084");
		if (irs1040USTerritoryTaxReturn == "U.S. Territory Tax Return" && isirValue != "4")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "4";
			return;
		}        
	}
}

if (receivedDocuments.hasDoc("ForeignTaxTranscript","Parent1") && receivedDocuments.get("ForeignTaxTranscript","Parent1").isAcceptable())
{
	if (receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1084") != null && !receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1084").isAllWhitespace())
	{
		irs1040 = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040 == "1040" && isirValue != "1")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "1";
			return;
		}
		 
		irs1040A = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040A == "1040A" && isirValue != "2")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			/*discrepancyEvaluationResult.correctedValue = "2";*/
			return;
		}    
 
		irs1040EZ = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040EZ == "1040EZ" && isirValue != "2")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			/*discrepancyEvaluationResult.correctedValue = "2";*/
			return;
		}        
 
		irs1040ForeignTaxTranscript = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040ForeignTaxTranscript == "Foreign Tax Return" && isirValue != "3")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "3";
			return;
		}           
	  
		irs1040USTerritoryTaxReturn = receivedDocuments.get("ForeignTaxTranscript", "Parent1").getDocumentField("AC1084");
		if (irs1040USTerritoryTaxReturn == "U.S. Territory Tax Return" && isirValue != "4")
		{
			discrepancyEvaluationResult.hasDiscrepancy = true;
			discrepancyEvaluationResult.correctedValue = "4";
			return;
		}            
	}
}    