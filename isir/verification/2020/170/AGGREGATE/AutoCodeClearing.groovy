//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationClearanceRulesScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Baseline Configuration - 2019-2020 170 V5 - Code Clearing */

boolean studentVerificationStatus = false;
boolean parentVerificationStatus = false;
boolean hsStatus = false;

/* V4 Document Variables */
	String docHsDiploma = "HSDiploma";
    String docHsTranscript = "HSTranscript";
    String docGedCert = "GEDCert";
    String docGedTranscript = "GEDTranscript";
    String docStateHsEquivalency = "StateHSEquivalencyCert";
    String docSecondarySchoolCert = "SecondarySchoolLeavingCert";
    String docHomeSchoolCert = "HomeSchoolCert";
    String docDriversLicense = "DriversLicense";
    String docNonDriversLicense = "NonDriversLicenseID";
    String docStateId = "StateIssuedID";
    String docPassport = "Passport";
    String docSoepCampus = "SOEP-Campus";
    String docSoepNotary = "SOEP-Notary";
    
    /* V4 Logical Booleans */

    boolean hsPass = false;
    if ((receivedDocuments.hasDoc(docHsDiploma, "Student") && receivedDocuments.get(docHsDiploma, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docHsTranscript, "Student") && receivedDocuments.get(docHsTranscript, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docGedCert, "Student") && receivedDocuments.get(docGedCert, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docGedTranscript, "Student") && receivedDocuments.get(docGedTranscript, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docStateHsEquivalency, "Student") && receivedDocuments.get(docStateHsEquivalency, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docSecondarySchoolCert, "Student") && receivedDocuments.get(docSecondarySchoolCert, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docHomeSchoolCert, "Student") && receivedDocuments.get(docHomeSchoolCert, "Student").isAcceptable()))
    {
        hsPass = true;
    }
    
    boolean identityPass = false;
    if ((receivedDocuments.hasDoc(docDriversLicense, "Student") && receivedDocuments.get(docDriversLicense, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docNonDriversLicense, "Student") && receivedDocuments.get(docNonDriversLicense, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docStateId, "Student") && receivedDocuments.get(docStateId, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docPassport, "Student") && receivedDocuments.get(docPassport, "Student").isAcceptable()))
    {
        identityPass = true;
    }
    
    boolean soepPass = false;
    if ((receivedDocuments.hasDoc(docSoepCampus, "Student") && receivedDocuments.get(docSoepCampus, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docSoepNotary, "Student") && receivedDocuments.get(docSoepNotary, "Student").isAcceptable()))
    {
        soepPass = true;
    }
    
    hsStatus = hsPass && identityPass && soepPass;

/* V1 Document Variables */
    String docRolloverStatement = "RolloverStatement";
    String docTaxTranscript = "TaxReturnTranscript";
    String doc1040 = "1040";
    String doc1040x = "1040x";
    String docForeignTax = "ForeignTaxTranscript";
    String docW2 = "W2";
    String doc1099 = "1099G";
    String docSelfEmployment = "SelfEmploymentStatement";
    String docNonFilingStatement = "NonFilingStatement";

/* Student ISIR Variables */
	String isirSTUDENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNCOMPLETED");
	String isirStudentIrsRequestFlag = isirRecord.getIsirFieldValue("STUDENTSIRSREQUESTFLAG");
	String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");
	String isirStudentBirthday = isirRecord.getIsirFieldValue("DATEOFBIRTH");
	String isirStudentFilingStatus = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNFILINGSTATUS");
	String isirStudentIRSUntaxedPensionsFlag = isirRecord.getIsirFieldValue("STUDENTIRSUNTAXEDPENSIONSFLAG");
	String isirStudentIRSIRADistributionFlag = isirRecord.getIsirFieldValue("STUDENTIRSIRADISTRIBUTIONSFLAG");

/* Parent ISIR Variables */
    String isirPARENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("PARENTSTAXRETURNCOMPLETED");
    String isirPARENTSMARITALSTATUS = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");
    String isirPARENTSIRSREQUESTFLAG = isirRecord.getIsirFieldValue("PARENTSIRSREQUESTFLAG");
    String isirPARENTIRSIRADISTRIBUTIONSFLAG = isirRecord.getIsirFieldValue("PARENTIRSIRADISTRIBUTIONSFLAG");
    String isirPARENTIRSUNTAXEDPENSIONSFLAG = isirRecord.getIsirFieldValue("PARENTIRSUNTAXEDPENSIONSFLAG");
    String isirPARENTSTAXRETURNFILINGSTATUS = isirRecord.getIsirFieldValue("PARENTSTAXRETURNFILINGSTATUS");
    String isirPARENT1DATEOFBIRTH = isirRecord.getIsirFieldValue("PARENT1DATEOFBIRTH");
    String isirPARENT2DATEOFBIRTH = isirRecord.getIsirFieldValue("PARENT2DATEOFBIRTH");

/* Student Document Booleans */

	boolean hsVerificationPass = false;
	boolean hsDegree = false;
	boolean identificationDocument = false;
	boolean soep = false;

	if ((receivedDocuments.hasDoc("HSDiploma", "Student") && receivedDocuments.get("HSDiploma", "Student").isAcceptable()) || (receivedDocuments.hasDoc("HSTranscript", "Student") && receivedDocuments.get("HSTranscript", "Student").isAcceptable()))
	{
		hsDegree = true;
	}
	if (receivedDocuments.hasDoc("GEDCert", "Student") && receivedDocuments.get("GEDCert", "Student").isAcceptable())
	{
		hsDegree = true;
	}
	if (receivedDocuments.hasDoc("GEDTranscript", "Student") && receivedDocuments.get("GEDTranscript", "Student").isAcceptable())
	{
		hsDegree = true;
	}
	if (receivedDocuments.hasDoc("StateHSEquivalencyCert", "Student") && receivedDocuments.get("StateHSEquivalencyCert", "Student").isAcceptable())
	{
		hsDegree = true;
	}
	if (receivedDocuments.hasDoc("SecondarySchoolLeavingCert", "Student") && receivedDocuments.get("SecondarySchoolLeavingCert", "Student").isAcceptable())
	{
		hsDegree = true;
	}
	if (receivedDocuments.hasDoc("HomeSchoolCert", "Student") && receivedDocuments.get("HomeSchoolCert", "Student").isAcceptable())
	{
		hsDegree = true;
	}
	if (receivedDocuments.hasDoc("DriversLicense", "Student") && receivedDocuments.get("DriversLicense", "Student").isAcceptable())
	{
		identificationDocument = true;
	}
	if (receivedDocuments.hasDoc("NonDriversLicenseID", "Student") && receivedDocuments.get("NonDriversLicenseID", "Student").isAcceptable())
	{
		identificationDocument = true;
	}
	if (receivedDocuments.hasDoc("StateIssuedID", "Student") && receivedDocuments.get("StateIssuedID", "Student").isAcceptable())
	{
		identificationDocument = true;
	}
	if (receivedDocuments.hasDoc("Passport", "Student") && receivedDocuments.get("Passport", "Student").isAcceptable())
	{
		identificationDocument = true;
	}

	if ((receivedDocuments.hasDoc("SOEP-Campus", "Student") && receivedDocuments.get("SOEP-Campus", "Student").isAcceptable()) || (receivedDocuments.hasDoc("SOEP-Notary", "Student") && receivedDocuments.get("SOEP-Notary", "Student").isAcceptable()))
	{
		soep = true;
	}

	if (hsDegree && identificationDocument && soep)
	{
		hsVerificationPass = true;
	}

	/* Standard Verification (Taxes) */

	boolean hasTaxTranscriptStudent = false;
	if (receivedDocuments.hasDoc("TaxReturnTranscript", "Student") && receivedDocuments.get("TaxReturnTranscript", "Student").isAcceptable())
	{
		hasTaxTranscriptStudent = true;
	}

	boolean hasTaxTranscriptSpouse = false;
	if (receivedDocuments.hasDoc("TaxReturnTranscript", "Spouse") && receivedDocuments.get("TaxReturnTranscript", "Spouse").isAcceptable())
	{
		hasTaxTranscriptSpouse = true;
	}

	boolean hasVerificationWorksheetDepStudent = false;
	if (receivedDocuments.hasDoc("VW-Dep", "Student") && receivedDocuments.get("VW-Dep", "Student").isAcceptable())
	{
		hasVerificationWorksheetDepStudent = true;
	}

	boolean has1040Student = false;
	if (receivedDocuments.hasDoc("1040", "Student") && receivedDocuments.get("1040", "Student").isAcceptable())
	{
		has1040Student = true;
	}

	boolean has1040Spouse = false;
	if (receivedDocuments.hasDoc("1040", "Spouse") && receivedDocuments.get("1040", "Spouse").isAcceptable())
	{
		has1040Spouse = true;
	}

	boolean has1040xStudent = false;
	if (receivedDocuments.hasDoc("1040x", "Student") && receivedDocuments.get("1040x", "Student").isAcceptable())
	{
		has1040xStudent = true;
	}

	boolean has1040xSpouse = false;
	if (receivedDocuments.hasDoc("1040x", "Spouse") && receivedDocuments.get("1040x", "Spouse").isAcceptable())
	{
		has1040xSpouse = true;
	}

	boolean hasForeignTaxTranscriptStudent = false;
	if (receivedDocuments.hasDoc("ForeignTaxTranscript", "Student") && receivedDocuments.get("ForeignTaxTranscript", "Student").isAcceptable())
	{
		hasForeignTaxTranscriptStudent = true;
	}

	boolean hasForeignTaxTranscriptSpouse = false;
	if (receivedDocuments.hasDoc("ForeignTaxTranscript", "Spouse") && receivedDocuments.get("ForeignTaxTranscript", "Spouse").isAcceptable())
	{
		hasForeignTaxTranscriptSpouse = true;
	}

	boolean hasW2Student = false;
	if (receivedDocuments.hasDoc("W2", "Student") && receivedDocuments.get("W2", "Student").isAcceptable())
	{
		hasW2Student = true;
	}

	boolean hasW2Spouse = false;
	if (receivedDocuments.hasDoc("W2", "Spouse") && receivedDocuments.get("W2", "Spouse").isAcceptable())
	{
		hasW2Spouse = true;
	}

	boolean has1099GStudent = false;
	if (receivedDocuments.hasDoc("1099G", "Student") && receivedDocuments.get("1099G", "Student").isAcceptable())
	{
		has1099GStudent = true;
	}

	boolean has1099GSpouse = false;
	if (receivedDocuments.hasDoc("1099G", "Spouse") && receivedDocuments.get("1099G", "Spouse").isAcceptable())
	{
		has1099GSpouse = true;
	}

	boolean hasIrsExtensionStudent = false;
	if (receivedDocuments.hasDoc("IRSForm4868", "Student") && receivedDocuments.hasDoc("IRSExtensionApproval", "Student") && receivedDocuments.get("IRSForm4868", "Student").isAcceptable() && receivedDocuments.get("IRSExtensionApproval", "Student").isAcceptable())
	{
		hasIrsExtensionStudent = true;
	}

	boolean hasSelfEmploymentStatementStudent = false;
	if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Student") && receivedDocuments.get("SelfEmploymentStatement", "Student").isAcceptable())
	{
		hasSelfEmploymentStatementStudent = true;
	}

	boolean hasSelfEmploymentStatementSpouse = false;
	if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Spouse") && receivedDocuments.get("SelfEmploymentStatement", "Spouse").isAcceptable())
	{
		hasSelfEmploymentStatementSpouse = true;
	}

	boolean hasRolloverStatement = false;
	if (receivedDocuments.hasDoc("RolloverStatement", "Student") && receivedDocuments.get("RolloverStatement", "Student").isAcceptable())
	{
		hasRolloverStatement = true;
	}

	boolean hasNonFilingStudent = false;
	if (receivedDocuments.hasDoc("NonFilingStatement", "Student") && receivedDocuments.get("NonFilingStatement", "Student").isAcceptable())
	{
		hasNonFilingStudent = true;
	}

/* Parent V1 Document Booleans */
    boolean hasRolloverParent1 = false;
    if (receivedDocuments.hasDoc(docRolloverStatement, "Parent1") && receivedDocuments.get(docRolloverStatement, "Parent1").isAcceptable())
    {
        hasRolloverParent1 = true;
    }

    boolean hasRolloverParent2 = false;
    if (receivedDocuments.hasDoc(docRolloverStatement, "Parent2") && receivedDocuments.get(docRolloverStatement, "Parent2").isAcceptable())
    {
        hasRolloverParent2 = true;
    }

	boolean hasNonFilingParent1 = false;
	if (receivedDocuments.hasDoc("NonFilingStatement", "Parent1") && receivedDocuments.get("NonFilingStatement", "Parent1").isAcceptable())
	{
		hasNonFilingParent1 = true;
	}	

	boolean hasNonFilingParent2 = false;
	if (receivedDocuments.hasDoc("NonFilingStatement", "Parent2") && receivedDocuments.get("NonFilingStatement", "Parent2").isAcceptable())
	{
		hasNonFilingParent2 = true;
	}

    boolean hasTaxTranscriptParent1 = false;
    if (receivedDocuments.hasDoc(docTaxTranscript, "Parent1") && receivedDocuments.get(docTaxTranscript, "Parent1").isAcceptable())
    {
        hasTaxTranscriptParent1 = true;
    }

    boolean hasTaxTranscriptParent2 = false;
    if (receivedDocuments.hasDoc(docTaxTranscript, "Parent2") && receivedDocuments.get(docTaxTranscript, "Parent2").isAcceptable())
    {
        hasTaxTranscriptParent2 = true;
    }
    
    boolean has1040Parent1 = false;
    if (receivedDocuments.hasDoc(doc1040, "Parent1") && receivedDocuments.get(doc1040, "Parent1").isAcceptable())
    {
        has1040Parent1 = true;
    }
    
    boolean has1040Parent2 = false;
    if (receivedDocuments.hasDoc(doc1040, "Parent2") && receivedDocuments.get(doc1040, "Parent2").isAcceptable())
    {
        has1040Parent2 = true;
    }     

    boolean has1040xParent1 = false;
    if (receivedDocuments.hasDoc(doc1040x, "Parent1") && receivedDocuments.get(doc1040x, "Parent1").isAcceptable())
    {
        has1040xParent1 = true;
    }

    boolean has1040xParent2 = false;
    if (receivedDocuments.hasDoc(doc1040x, "Parent2") && receivedDocuments.get(doc1040x, "Parent2").isAcceptable())
    {
        has1040xParent2 = true;
    }

    boolean hasForeignTaxParent1 = false;
    if (receivedDocuments.hasDoc(docForeignTax, "Parent1") && receivedDocuments.get(docForeignTax, "Parent1").isAcceptable())
    {
        hasForeignTaxParent1 = true;
    }

    boolean hasForeignTaxParent2 = false;
    if (receivedDocuments.hasDoc(docForeignTax, "Parent2") && receivedDocuments.get(docForeignTax, "Parent2").isAcceptable())
    {
        hasForeignTaxParent2 = true;
    }

    boolean hasW2Parent1 = false;
    if (receivedDocuments.hasDoc(docW2, "Parent1") && receivedDocuments.get(docW2, "Parent1").isAcceptable())
    {
        hasW2Parent1 = true;
    }

    boolean hasW2Parent2 = false;
    if (receivedDocuments.hasDoc(docW2, "Parent2") && receivedDocuments.get(docW2, "Parent2").isAcceptable())
    {
        hasW2Parent2 = true;
    }

    boolean has1099Parent1 = false;
    if (receivedDocuments.hasDoc(doc1099, "Parent1") && receivedDocuments.get(doc1099, "Parent1").isAcceptable())
    {
        has1099Parent1 = true;
    }

    boolean has1099Parent2 = false;
    if (receivedDocuments.hasDoc(doc1099, "Parent2") && receivedDocuments.get(doc1099, "Parent2").isAcceptable())
    {
        has1099Parent2 = true;
    }

    boolean hasSelfEmploymentParent1 = false;
    if (receivedDocuments.hasDoc(docSelfEmployment, "Parent1") && receivedDocuments.get(docSelfEmployment, "Parent1").isAcceptable())
    {
        hasSelfEmploymentParent1 = true;
    }

    boolean hasSelfEmploymentParent2 = false;
    if (receivedDocuments.hasDoc(docSelfEmployment, "Parent2") && receivedDocuments.get(docSelfEmployment, "Parent2").isAcceptable())
    {
        hasSelfEmploymentParent2 = true;
    }

	boolean hasIrsExtensionParent1 = false;
	if (receivedDocuments.hasDoc("IRSForm4868", "Parent1") && receivedDocuments.get("IRSForm4868", "Parent1").isAcceptable() && receivedDocuments.hasDoc("IRSExtensionApproval", "Parent1") && receivedDocuments.get("IRSExtensionApproval", "Parent1").isAcceptable())
	{
		hasIrsExtensionStudent = true;
	}  
	
	boolean hasIrsExtensionParent2 = false;
	if (receivedDocuments.hasDoc("IRSForm4868", "Parent2") && receivedDocuments.get("IRSForm4868", "Parent2").isAcceptable() && receivedDocuments.hasDoc("IRSExtensionApproval", "Parent2") && receivedDocuments.get("IRSExtensionApproval", "Parent2").isAcceptable())
	{
		hasIrsExtensionStudent = true;
	}	  

/* Student Spouse Age */
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
  
/* Student Calculate Filing Threshold */  
	int selfEmploymentStudent = 0;
	if (hasSelfEmploymentStatementStudent)
	{
		if (receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").isAllWhitespace())
		{
			selfEmploymentStudent = receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").toFloat().round();
		}
	}

	int selfEmploymentSpouse = 0;
	if (hasSelfEmploymentStatementSpouse)
	{
		if (receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1015").isAllWhitespace())
		{
			selfEmploymentSpouse = receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1015").toFloat().round();
		}
	}  
 
    boolean studentAgeUnder65 = true;
    Date studentAge65Threshold = groovyScriptHelper.getIsirDate("19501231");
    Date studentBirthday = groovyScriptHelper.getIsirDate("20180101");
    if (isirStudentBirthday != null && !isirStudentBirthday.isAllWhitespace())
    {
        studentBirthday = groovyScriptHelper.getIsirDate(isirStudentBirthday);
    }
    if (studentBirthday <= studentAge65Threshold)
    {
        studentAgeUnder65 = false;
    }
    int studentFilingThreshold = 999999;
    if (isirStudentFilingStatus == "1" && studentAgeUnder65)
    {
        studentFilingThreshold = 10300;
    }
    if (isirStudentFilingStatus == "1" && !studentAgeUnder65)
    {
        studentFilingThreshold = 11850;
    }
    if (isirStudentFilingStatus == "2" && studentAgeUnder65 && spouseAge < 65)
    {
        studentFilingThreshold = 20600;
    }
    if (isirStudentFilingStatus == "2" && (studentAgeUnder65 || spouseAge < 65))
    {
        studentFilingThreshold = 21850;
    }
    if (isirStudentFilingStatus == "2" && !studentAgeUnder65 && spouseAge >= 65)
    {
        studentFilingThreshold = 23100;
    }
    if (isirStudentFilingStatus == "3")
    {
        studentFilingThreshold = 4000;
    }
    if (isirStudentFilingStatus == "4" && studentAgeUnder65)
    {
        studentFilingThreshold = 13250;
    }
    if (isirStudentFilingStatus == "4" && !studentAgeUnder65)
    {
        studentFilingThreshold = 14800;
    }
    if (isirStudentFilingStatus == "5" && studentAgeUnder65)
    {
        studentFilingThreshold = 16600;
    }
    if (isirStudentFilingStatus == "5" && !studentAgeUnder65)
    {
        studentFilingThreshold = 17850;
    }

/* Student Calculate Finances */    
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

	int w2SumSpouse = 0;
	float w2SumSpouseFloat = 0.0;
	def w2DocsSpouse = receivedDocuments.getDocumentsForDocCode("W2", "Spouse");
	for (doc in w2DocsSpouse)
	{
		String ac1010 = doc.getDocumentField("AC1010");
		float ac1010Value = 0.0;
		if (ac1010 != null && !ac1010.isAllWhitespace())
		{
			ac1010Value = ac1010.toFloat();
		}
		w2SumSpouseFloat = w2SumSpouseFloat + ac1010Value;
	}
	w2SumSpouse = w2SumSpouseFloat.round();

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

	int doc1099SumSpouse = 0;
	float doc1099SpouseFloat = 0.0;
	def doc1099GDocsSpouse = receivedDocuments.getDocumentsForDocCode("1099G", "Spouse");
	for (doc in doc1099GDocsSpouse)
	{
		String ac1013 = doc.getDocumentField("AC1013");
		float ac1013Value = 0.0;
		if (ac1013 != null && !ac1013.isAllWhitespace())
		{
			ac1013Value = ac1013.toFloat();
		}
		doc1099SpouseFloat = doc1099SpouseFloat + ac1013Value;
	}
	doc1099SumSpouse = doc1099SpouseFloat.round();

	int studentTotalIncome = w2SumStudent + w2SumSpouse + doc1099SumStudent + doc1099SumSpouse + selfEmploymentStudent + selfEmploymentSpouse; 
    
/* Parent Calculate Filing Threshold */
    int parentFilingThreshold = 99999;
    
    if (!hasW2Parent1 && !hasW2Parent2)
    {
        parentFilingThreshold = 400;
    }
    else
    {
        boolean parent1Under65 = true;
        boolean parent2Under65 = true;
        Date parentAge65Threshold = groovyScriptHelper.getIsirDate("19511231");
        Date parent1Birthday = groovyScriptHelper.getIsirDate("20180101");
        Date parent2Birthday = groovyScriptHelper.getIsirDate("20180101");
        if (isirPARENT1DATEOFBIRTH != null && !isirPARENT1DATEOFBIRTH.isAllWhitespace())
        {
            parent1Birthday = groovyScriptHelper.getIsirDate(isirPARENT1DATEOFBIRTH);
        }
        if (isirPARENT2DATEOFBIRTH != null && !isirPARENT2DATEOFBIRTH.isAllWhitespace())
        {
            parent2Birthday = groovyScriptHelper.getIsirDate(isirPARENT2DATEOFBIRTH);
        }
        if (parent1Birthday <= parentAge65Threshold)
        {
            parent1Under65 = false;
        }
        if (parent2Birthday <= parentAge65Threshold)
        {
            parent2Under65 = false;
        }
        
        if (isirPARENTSTAXRETURNFILINGSTATUS == "1")
        {
            if (parent1Under65)
            {
                parentFilingThreshold = 10400;
            }
            else
            {
                parentFilingThreshold = 11950;
            }
        }
        if (isirPARENTSTAXRETURNFILINGSTATUS == "4")
        {
            if (parent1Under65)
            {
                parentFilingThreshold = 13400;
            }
            else
            {
                parentFilingThreshold = 14950;
            }
        }
        if (isirPARENTSTAXRETURNFILINGSTATUS == "2")
        {
            if (parent1Under65 && parent2Under65)
            {
                parentFilingThreshold = 20800;
            }
            else if ((parent1Under65 && !parent2Under65) || (!parent1Under65 && parent2Under65))
            {
                parentFilingThreshold = 22050;
            }
            else
            {
                parentFilingThreshold = 23300;
            }
        }
        if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
        {
            parentFilingThreshold = 4050;
        }
        if (isirPARENTSTAXRETURNFILINGSTATUS == "5")
        {
            if (parent1Under65)
            {
                parentFilingThreshold = 16750;
            }
            else
            {
                parentFilingThreshold = 18000;
            }
        }
    }
    
/* Parent Calculate Finances */

    int selfEmploymentParent1 = 0;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent1") && receivedDocuments.get("SelfEmploymentStatement", "Parent1").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1015").isAllWhitespace())
    {
        selfEmploymentParent1 = receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1015").toFloat().round();
    }
    
    int selfEmploymentParent2 = 0;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent2") && receivedDocuments.get("SelfEmploymentStatement", "Parent2").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent2").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Parent2").getDocumentField("AC1015").isAllWhitespace())
    {
        selfEmploymentParent2 = receivedDocuments.get("SelfEmploymentStatement", "Parent2").getDocumentField("AC1015").toFloat().round();
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
    
    int w2SumParent2 = 0;
    float w2SumParent2Float = 0.0;
    def w2DocsParent2 = receivedDocuments.getDocumentsForDocCode("W2", "Parent2");
    for (doc in w2DocsParent2)
    {
        String ac1010 = doc.getDocumentField("AC1010");
        float ac1010Value = 0.0;
        if (ac1010 != null && !ac1010.isAllWhitespace())
        {
            ac1010Value = ac1010.toFloat();
        }
        w2SumParent2Float = w2SumParent2Float + ac1010Value;
    } 
    w2SumParent2 = w2SumParent2Float.round();
    
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
    
    int doc1099SumParent2 = 0;
    float doc1099Parent2Float = 0.0;
    def doc1099GDocsParent2 = receivedDocuments.getDocumentsForDocCode("1099G", "Parent2");
    for (doc in doc1099GDocsParent2)
    {
        String ac1013 = doc.getDocumentField("AC1013");
        float ac1013Value = 0.0;
        if (ac1013 != null && !ac1013.isAllWhitespace())
        {
            ac1013Value = ac1013.toFloat();
        }
        doc1099Parent2Float = doc1099Parent2Float + ac1013Value;
    }
    doc1099SumParent2 = doc1099Parent2Float.round();

/* Student Tax Rules */
if (isirSTUDENTSTAXRETURNCOMPLETED == "1")
{
   if (isirStudentIrsRequestFlag == "02" && (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4") && hasRolloverStatement && hsVerificationPass && hasVerificationWorksheetDepStudent)
    {
        studentVerificationStatus = true;
    }
    else if (isirStudentIrsRequestFlag == "02" && (isirStudentIRSUntaxedPensionsFlag != "1" && isirStudentIRSUntaxedPensionsFlag != "2" && isirStudentIRSUntaxedPensionsFlag != "3" && isirStudentIRSUntaxedPensionsFlag != "4" && isirStudentIRSIRADistributionFlag != "1" && isirStudentIRSIRADistributionFlag != "2" && isirStudentIRSIRADistributionFlag != "3" && isirStudentIRSIRADistributionFlag != "4") && hsVerificationPass && hasVerificationWorksheetDepStudent)
    {
        studentVerificationStatus = true;
    }
    
     if ((isirStudentIrsRequestFlag != "02" && isirStudentIrsRequestFlag != "07") && (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4") && hasRolloverStatement && hasVerificationWorksheetDepStudent && (hasTaxTranscriptStudent || has1040Student || has1040xStudent || hasForeignTaxTranscriptStudent) && hsVerificationPass)
    {
        studentVerificationStatus = true;
    }
    
    if ((isirStudentIrsRequestFlag != "02" && isirStudentIrsRequestFlag != "07") && (isirStudentIRSUntaxedPensionsFlag != "1" && isirStudentIRSUntaxedPensionsFlag != "2" && isirStudentIRSUntaxedPensionsFlag != "3" && isirStudentIRSUntaxedPensionsFlag != "4" && isirStudentIRSIRADistributionFlag != "1" && isirStudentIRSIRADistributionFlag != "2" && isirStudentIRSIRADistributionFlag != "3" && isirStudentIRSIRADistributionFlag != "4") && hasVerificationWorksheetDepStudent && (hasTaxTranscriptStudent || has1040Student || has1040xStudent || hasForeignTaxTranscriptStudent))
    {
       	studentVerificationStatus = true;
    }
    
    if (isirStudentIrsRequestFlag == "07" && (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4") && hasRolloverStatement && hasVerificationWorksheetDepStudent && (hasTaxTranscriptStudent || has1040Student) && has1040xStudent && hsVerificationPass)
    {
    	studentVerificationStatus = true;
    }
    
    if (isirStudentIrsRequestFlag == "07" && (isirStudentIRSUntaxedPensionsFlag != "1" && isirStudentIRSUntaxedPensionsFlag != "2" && isirStudentIRSUntaxedPensionsFlag != "3" && isirStudentIRSUntaxedPensionsFlag != "4" && isirStudentIRSIRADistributionFlag != "1" && isirStudentIRSIRADistributionFlag != "2" && isirStudentIRSIRADistributionFlag != "3" && isirStudentIRSIRADistributionFlag != "4") && hasVerificationWorksheetDepStudent && (hasTaxTranscriptStudent || has1040Student) && has1040xStudent && hsVerificationPass)
    {
        studentVerificationStatus = true;
    }
}
else
{
    if (hasIrsExtensionStudent && !hasTaxTranscriptStudent && !has1040Student && !has1040xStudent && !hasForeignTaxTranscriptStudent && !hsVerificationPass)
    {
        studentVerificationStatus = false;
    }
    if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4")
    {
        if (hasVerificationWorksheetDepStudent && (hasNonFilingStudent || hasW2Student || has1099GStudent || hasSelfEmploymentStatementStudent) && hsVerificationPass)
        {
            if (studentTotalIncome >= studentFilingThreshold && (hasTaxTranscriptStudent || has1040Student || has1040xStudent || hasForeignTaxTranscriptStudent))
            {
                studentVerificationStatus = true;
            }
            if (studentTotalIncome < studentFilingThreshold)
            {
                studentVerificationStatus = true;
            }
        }
    }
    if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
    {
        if (hasVerificationWorksheetDepStudent && (hasNonFilingStudent || hasW2Student || has1099GStudent || hasSelfEmploymentStatementStudent) && hsVerificationPass)
        {
            if (studentTotalIncome >= studentFilingThreshold && (((hasTaxTranscriptStudent || has1040Student || has1040xStudent ||  hasForeignTaxTranscriptStudent) && isirStudentFilingStatus != "3") || (isirStudentFilingStatus == "3" && (hasTaxTranscriptSpouse || has1040Spouse || has1040xSpouse ||  hasForeignTaxTranscriptSpouse) && (hasW2Spouse || has1099GSpouse || hasSelfEmploymentStatementSpouse) && (hasTaxTranscriptStudent || has1040Student || has1040xStudent ||  hasForeignTaxTranscriptStudent))))
            {
                if (hasTaxTranscriptStudent)
                {
                    String taxTranscriptFilingStatus = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1065");
                    if (taxTranscriptFilingStatus != null && !taxTranscriptFilingStatus.isAllWhitespace() && taxTranscriptFilingStatus == "Married Filing Separately" && (hasTaxTranscriptSpouse || has1040Spouse))
                    {
                        studentVerificationStatus = true;
                    }
                    else if (taxTranscriptFilingStatus != null && !taxTranscriptFilingStatus.isAllWhitespace() && taxTranscriptFilingStatus != "Married Filing Separately")
                    {
                        studentVerificationStatus = true;
                    }
                }
                if (has1040Student)
                {
                    String f1040FilingStatus = receivedDocuments.get("1040", "Student").getDocumentField("AC1065");
                    if (f1040FilingStatus != null && !f1040FilingStatus.isAllWhitespace() && f1040FilingStatus == "Married Filing Separately" && has1040Spouse)
                    {
                       studentVerificationStatus = true;
                    }
                    else if (f1040FilingStatus != null && !f1040FilingStatus.isAllWhitespace() && f1040FilingStatus != "Married Filing Separately")
                    {
                        studentVerificationStatus = true;
                    }
                }                
                if (has1040xStudent)
                {
                    String f1040xFilingStatus = receivedDocuments.get("1040x", "Student").getDocumentField("AC1065");
                    if (f1040xFilingStatus != null && !f1040xFilingStatus.isAllWhitespace() && f1040xFilingStatus == "Married Filing Separately" && has1040xSpouse)
                    {
                        studentVerificationStatus = true;
                    }
                    else if (f1040xFilingStatus != null && !f1040xFilingStatus.isAllWhitespace() && f1040xFilingStatus != "Married Filing Separately")
                    {
                        studentVerificationStatus = true;
                    }
                }
                if (hasForeignTaxTranscriptStudent)
                {
                    String foreignTaxTranscriptFilingStatus = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1065");
                    if (foreignTaxTranscriptFilingStatus != null && !foreignTaxTranscriptFilingStatus.isAllWhitespace() && foreignTaxTranscriptFilingStatus == "Married Filing Separately" && hasForeignTaxTranscriptSpouse)
                    {
                        studentVerificationStatus = true;
                    }
                    else if (foreignTaxTranscriptFilingStatus != null && !foreignTaxTranscriptFilingStatus.isAllWhitespace() && foreignTaxTranscriptFilingStatus != "Married Filing Separately")
                    {
                        studentVerificationStatus = true;
                    }
                }
            }
            if (studentTotalIncome < studentFilingThreshold && isirStudentFilingStatus != "3")
            {
                studentVerificationStatus = true;
                if (isirStudentFilingStatus == "3" && (hasW2Spouse || has1099GSpouse || hasSelfEmploymentStatementSpouse))
                {
                    studentVerificationStatus = true;
                }
            }
        }
    }
}

/* Parent Tax Rules */
    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG == "02" && hasVerificationWorksheetDepStudent)
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            parentVerificationStatus = true;
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1)
        {
            parentVerificationStatus = true;
            if (isirPARENTSTAXRETURNFILINGSTATUS == "3" && !hasRolloverParent2)
        	{
            	parentVerificationStatus = false;
        	}
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG != "02" && isirPARENTSIRSREQUESTFLAG != "07" && hasVerificationWorksheetDepStudent && (hasTaxTranscriptParent1 || has1040Parent1 || has1040xParent1 || hasForeignTaxParent1))
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            parentVerificationStatus = true;
            if (isirPARENTSTAXRETURNFILINGSTATUS == "3" && (!hasTaxTranscriptParent2 && !has1040Parent2 && !has1040xParent2 && !hasForeignTaxParent2))
        	{
            	parentVerificationStatus = false;
        	}            
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1)
        {
            parentVerificationStatus = true;
            if (isirPARENTSTAXRETURNFILINGSTATUS == "3" && !hasRolloverParent2)
        	{
            	parentVerificationStatus = false;
        	}                
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG == "07" && hasVerificationWorksheetDepStudent && ((hasTaxTranscriptParent1 || has1040Parent1) && has1040xParent1))
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            parentVerificationStatus = true;
			if (isirPARENTSTAXRETURNFILINGSTATUS == "3" && (!has1040xParent2 && (!hasTaxTranscriptParent2 || !has1040Parent2)))
			{
				parentVerificationStatus = false;
			}               
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1)
        {
            parentVerificationStatus = true;
			if (isirPARENTSTAXRETURNFILINGSTATUS == "3" && !hasRolloverParent2)
			{
				parentVerificationStatus = false;
			}                   
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSTAXRETURNFILINGSTATUS != "3" && hasVerificationWorksheetDepStudent && hasNonFilingParent1 && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1))
    {
        if ((selfEmploymentParent1 + w2SumParent1 + doc1099SumParent1) < parentFilingThreshold)
        {
            parentVerificationStatus = true;
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSTAXRETURNFILINGSTATUS == "3" && hasVerificationWorksheetDepStudent && hasNonFilingParent1 && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1) && hasNonFilingParent2 && (hasW2Parent2 || has1099Parent2 || hasSelfEmploymentParent2))
    {
        if ((selfEmploymentParent1 + selfEmploymentParent2 + w2SumParent1 + w2SumParent2 + doc1099SumParent1 + doc1099SumParent2) < parentFilingThreshold)
        {
            parentVerificationStatus = true;
        }
    }
    
    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSTAXRETURNFILINGSTATUS != "3" && hasVerificationWorksheetDepStudent && hasNonFilingParent1 && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1))
    {
        if ((selfEmploymentParent1 + w2SumParent1 + doc1099SumParent1) >= parentFilingThreshold && (hasTaxTranscriptParent1 || has1040Parent1 || has1040xParent1 || hasForeignTaxParent1))
        {
        	parentVerificationStatus = true;
        }
    }
    
    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSTAXRETURNFILINGSTATUS == "3" && hasVerificationWorksheetDepStudent && hasNonFilingParent1 && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1) && hasNonFilingParent2 && (hasW2Parent2 || has1099Parent2 || hasSelfEmploymentParent2))
    {
        if ((selfEmploymentParent1 + selfEmploymentParent2 + w2SumParent1 + w2SumParent2 + doc1099SumParent1 + doc1099SumParent2) >= parentFilingThreshold && (hasTaxTranscriptParent1 || has1040Parent1 || has1040xParent1 || hasForeignTaxParent1))
        {
            if (hasTaxTranscriptParent2 || has1040Parent2 || has1040xParent2 || hasForeignTaxParent2)
            {
                parentVerificationStatus = true;
            }
        }
    }
    
  	if (hasIrsExtensionParent1 && !hasTaxTranscriptParent1 && !has1040Parent1 && !has1040xParent1 && !hasForeignTaxParent1)
    {
        parentVerificationStatus = false;
    }
    
    /*This rule references the documents requested in docmetadata/AcceptanceCriteria/W2/2019/AC1010.groovy */
   	if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
	{
		if (hasIrsExtensionParent1 && hasIrsExtensionParent2 && !hasTaxTranscriptParent1 && !hasTaxTranscriptParent2 && !has1040Parent1 && !has1040Parent2 && !has1040xParent1 && !has1040xParent2 && !hasForeignTaxParent1 && !hasForeignTaxParent2)
    	{
        	parentVerificationStatus = false;
    	}
	}
    
return studentVerificationStatus && parentVerificationStatus && hsStatus;