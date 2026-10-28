//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationDocumentsRequiredScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Baseline Configuration - 2019-2020 170 V1 - Document Request */

/* Document Variables */
    String docVerificationWorksheet = "VW-Dep";
    String docRolloverStatement = "RolloverStatement";
    String docTaxTranscript = "TaxReturnTranscript";
    String doc1040 = "1040";
    String doc1040x = "1040x";
    String docForeignTax = "ForeignTaxTranscript";
    String docW2 = "W2";
    String doc1099 = "1099G";
    String docSelfEmployment = "SelfEmploymentStatement";
    String docNonFilingStatement = "NonFilingStatement";

/* Parent ISIR Variables */
    String isirPARENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("PARENTSTAXRETURNCOMPLETED");
    String isirPARENTSMARITALSTATUS = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");
    String isirPARENTSIRSREQUESTFLAG = isirRecord.getIsirFieldValue("PARENTSIRSREQUESTFLAG");
    String isirPARENTIRSIRADISTRIBUTIONSFLAG = isirRecord.getIsirFieldValue("PARENTIRSIRADISTRIBUTIONSFLAG");
    String isirPARENTIRSUNTAXEDPENSIONSFLAG = isirRecord.getIsirFieldValue("PARENTIRSUNTAXEDPENSIONSFLAG");
    String isirPARENTSTAXRETURNFILINGSTATUS = isirRecord.getIsirFieldValue("PARENTSTAXRETURNFILINGSTATUS");

/* Student ISIR Variables */
	String isirSTUDENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNCOMPLETED");
	String isirStudentIrsRequestFlag = isirRecord.getIsirFieldValue("STUDENTSIRSREQUESTFLAG");
	String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");
	String isirStudentBirthday = isirRecord.getIsirFieldValue("DATEOFBIRTH");
	String isirStudentFilingStatus = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNFILINGSTATUS");
	String isirStudentIRSUntaxedPensionsFlag = isirRecord.getIsirFieldValue("STUDENTIRSUNTAXEDPENSIONSFLAG");
	String isirStudentIRSIRADistributionFlag = isirRecord.getIsirFieldValue("STUDENTIRSIRADISTRIBUTIONSFLAG");

boolean hasVerificationWorksheetDepStudent = false;
if (receivedDocuments.hasDoc("VW-Dep", "Student") && receivedDocuments.get("VW-Dep", "Student").isAcceptable())
{
    hasVerificationWorksheetDepStudent = true;
}

boolean hasIrsExtensionStudent = false;
if (receivedDocuments.hasDoc("IRSForm4868", "Student") && receivedDocuments.get("IRSForm4868", "Student").isAcceptable() && receivedDocuments.hasDoc("IRSExtensionApproval", "Student") && receivedDocuments.get("IRSExtensionApproval", "Student").isAcceptable())
{
    hasIrsExtensionStudent = true;
}

int spouseAge = -1;
if (hasVerificationWorksheetDepStudent)
{
    if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1044") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1034");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1045") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1035");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1046") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1036");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1047") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1037");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1048") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1038");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1049") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1039");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1050") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1040");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1051") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1041");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1052") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1042");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
    else if (receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1053") == "Spouse")
    {
        String spouseAgeString = receivedDocuments.get("VW-Dep", "Student").getDocumentField("AC1043");
        if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
        {
            spouseAge = spouseAgeString.toInteger();
        }
    }
}

boolean studentAgeUnder65 = true;
Date age65Threshold = groovyScriptHelper.getIsirDate("19501231");
Date studentBirthday = groovyScriptHelper.getIsirDate("20180101");
if (isirStudentBirthday != null && !isirStudentBirthday.isAllWhitespace())
{
    studentBirthday = groovyScriptHelper.getIsirDate(isirStudentBirthday);
}
if (studentBirthday <= age65Threshold)
{
    studentAgeUnder65 = false;
}
int filingThreshold = 999999;
if (isirStudentFilingStatus == "1" && studentAgeUnder65)
{
    filingThreshold = 10300;
}
if (isirStudentFilingStatus == "1" && !studentAgeUnder65)
{
    filingThreshold = 11850;
}
if (isirStudentFilingStatus == "2" && studentAgeUnder65 && spouseAge < 65)
{
    filingThreshold = 20600;
}
if (isirStudentFilingStatus == "2" && (studentAgeUnder65 || spouseAge < 65))
{
    filingThreshold = 21850;
}
if (isirStudentFilingStatus == "2" && !studentAgeUnder65 && spouseAge >= 65)
{
    filingThreshold = 23100;
}
if (isirStudentFilingStatus == "3")
{
    filingThreshold = 4000;
}
if (isirStudentFilingStatus == "4" && studentAgeUnder65)
{
    filingThreshold = 13250;
}
if (isirStudentFilingStatus == "4" && !studentAgeUnder65)
{
    filingThreshold = 14800;
}
if (isirStudentFilingStatus == "5" && studentAgeUnder65)
{
    filingThreshold = 16600;
}
if (isirStudentFilingStatus == "5" && !studentAgeUnder65)
{
    filingThreshold = 17850;
}

/* Standard (V1) Student */ 
documentRequest.addDocument(docVerificationWorksheet, "Student");

if (isirSTUDENTSTAXRETURNCOMPLETED == "1")
{   
    if (isirStudentIrsRequestFlag == "02" && (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4"))
     {
         documentRequest.addDocument("RolloverStatement", "Student");
     }
    
    if (isirStudentIrsRequestFlag != "02" && isirStudentIrsRequestFlag != "07")
    {
        documentRequest.addAnyDocuments(["TaxReturnTranscript","Student"],["1040","Student"],["1040x","Student"],["ForeignTaxTranscript","Student"]);
        
        
        if (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4")
        {
           documentRequest.addDocument("RolloverStatement", "Student");
        }
    }
    
    if (isirStudentIrsRequestFlag == "07")
    {
        documentRequest.addAnyDocuments(["TaxReturnTranscript", "Student"],["1040","Student"]);
        documentRequest.addDocument("1040x", "Student");
        if (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4")
        {
            documentRequest.addDocument("RolloverStatement", "Student");
        }
    }
}    
else
{
    if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4")
    {
        documentRequest.addAnyDocuments(["W2","Student"],["1099G","Student"],["SelfEmploymentStatement","Student"],["NonFilingStatement","Student"]);
    }
    if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
    {
        documentRequest.addAnyDocuments(["W2","Student"],["1099G","Student"],["SelfEmploymentStatement","Student"],["NonFilingStatement","Student"]);
        
        if (isirStudentFilingStatus == "3")
        {
            documentRequest.addAnyDocuments(["W2","Spouse"],["1099G","Spouse"],["SelfEmploymentStatement","Spouse"],["NonFilingStatement","Spouse"]);
        }
    }        
}

/* Standard (V1) Parent */ 
    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG == "02")
    {
    
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
			if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
			{
				documentRequest.addDocument(docRolloverStatement, "Parent2");
			}
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && (isirPARENTSIRSREQUESTFLAG != "02" && isirPARENTSIRSREQUESTFLAG != "07"))
    {
        documentRequest.addAnyDocuments([docTaxTranscript, "Parent1"],[doc1040, "Parent1"],[doc1040x, "Parent1"],[docForeignTax, "Parent1"]);
        
        if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
		{
			documentRequest.addAnyDocuments([docTaxTranscript, "Parent2"],[doc1040, "Parent2"],[doc1040x, "Parent2"],[docForeignTax, "Parent2"]);
		}
    
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
            if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
			{
				documentRequest.addDocument(docRolloverStatement, "Parent2");
			}
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG == "07")
    {
        documentRequest.addAnyDocuments([docTaxTranscript, "Parent1"],[doc1040, "Parent1"]);
        documentRequest.addDocument(doc1040x, "Parent1");
        
        if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
		{
        	documentRequest.addAnyDocuments([docTaxTranscript, "Parent2"],[doc1040, "Parent2"]);
        	documentRequest.addDocument(doc1040x, "Parent2");		
        }
    
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
			if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
			{
				documentRequest.addDocument(docRolloverStatement, "Parent2");	
			}            
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5"))
    {
        documentRequest.addDocument(docNonFilingStatement, "Parent1");
        documentRequest.addAnyDocuments([docW2, "Parent1"],[doc1099, "Parent1"],[docSelfEmployment, "Parent1"]);
        
		if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
		{
       		documentRequest.addDocument(docNonFilingStatement, "Parent2");
       		documentRequest.addAnyDocuments([docW2, "Parent2"],[doc1099, "Parent2"],[docSelfEmployment, "Parent2"]);	
       	}	
    }