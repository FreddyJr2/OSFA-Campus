//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

if (isirRecord == null)
{
    return;
}

String docOwner = currentDocument.getDocumentOwnerTypes().get(0);

if (docOwner == "Student")
{
    String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");
    String isirStudentBirthday = isirRecord.getIsirFieldValue("DATEOFBIRTH");
    String isirFilingStatus = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNFILINGSTATUS");

    boolean hasVerificationWorksheetIndStudent = false;
    if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
    {
        hasVerificationWorksheetIndStudent = true;
    }

    boolean hasSelfEmploymentStatementStudent = false;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Student") && receivedDocuments.get("SelfEmploymentStatement", "Student").isAcceptable())
    {
        hasSelfEmploymentStatementStudent = true;
    }

    int selfEmploymentStudent = 0;
    if (hasSelfEmploymentStatementStudent)
    {
        if (receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").isAllWhitespace())
        {
            selfEmploymentStudent = receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").toFloat().round();
        }
    }

    boolean hasSelfEmploymentStatementSpouse = false;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Spouse") && receivedDocuments.get("SelfEmploymentStatement", "Spouse").isAcceptable())
    {
        hasSelfEmploymentStatementSpouse = true;
    }

    int selfEmploymentSpouse = 0;
    if (hasSelfEmploymentStatementSpouse)
    {
        if (receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1015").isAllWhitespace())
        {
            selfEmploymentSpouse = receivedDocuments.get("SelfEmploymentStatement", "Spouse").getDocumentField("AC1015").toFloat().round();
        }
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
    if (docOwner == "Student")
    {
        String currentAC1013 = currentDocument.getDocumentFieldValue("AC1013");
        if (currentAC1013 != null && !currentAC1013.isAllWhitespace())
        {
            doc1099StudentFloat = doc1099StudentFloat + currentAC1013.toFloat();
        }
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
    if (docOwner == "Spouse")
    {
        String currentAC1013 = currentDocument.getDocumentFieldValue("AC1013");
        if (currentAC1013 != null && !currentAC1013.isAllWhitespace())
        {
            doc1099SpouseFloat = doc1099SpouseFloat + currentAC1013.toFloat();
        }
    }    
    doc1099SumSpouse = doc1099SpouseFloat.round();

    int totalIncome = 0;

    int spouseAge = -1;
    if (hasVerificationWorksheetIndStudent)
    {
        if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1044") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1034");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1045") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1035");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1046") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1036");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1047") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1037");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1048") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1038");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1049") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1039");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1050") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1040");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1051") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1041");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1052") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1042");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1053") == "Spouse")
        {
            String spouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1043");
            if (spouseAgeString != null && !spouseAgeString.isAllWhitespace())
            {
                spouseAge = spouseAgeString.toInteger();
            }
        }
    }

    boolean studentAgeUnder65 = true;
    Date age65Threshold = groovyScriptHelper.getIsirDate("19501231");
    Date studentBirthday = groovyScriptHelper.getIsirDate("20170101");
    if (isirStudentBirthday != null && !isirStudentBirthday.isAllWhitespace())
    {
        studentBirthday = groovyScriptHelper.getIsirDate(isirStudentBirthday);
    }
    if (studentBirthday <= age65Threshold)
    {
        studentAgeUnder65 = false;
    }
    int filingThreshold = 999999;
   	if (isirFilingStatus == "1" && studentAgeUnder65)
	{
		filingThreshold = 12200;
	}
	if (isirFilingStatus == "1" && !studentAgeUnder65)
	{
		filingThreshold = 13850;
	}
	if (isirFilingStatus == "2" && studentAgeUnder65 && spouseAge < 65)
	{
		filingThreshold = 24400;
	}
	if (isirFilingStatus == "2" && (studentAgeUnder65 || spouseAge < 65))
	{
		filingThreshold = 25700;
	}
	if (isirFilingStatus == "2" && !studentAgeUnder65 && spouseAge >= 65)
	{
		filingThreshold = 27000;
	}
	if (isirFilingStatus == "3")
	{
		filingThreshold = 5;
	}
	if (isirFilingStatus == "4" && studentAgeUnder65)
	{
		filingThreshold = 18350;
	}
	if (isirFilingStatus == "4" && !studentAgeUnder65)
	{
		filingThreshold = 20000;
	}
	if (isirFilingStatus == "5" && studentAgeUnder65)
	{
		filingThreshold = 24400;
	}
	if (isirFilingStatus == "5" && !studentAgeUnder65)
	{
		filingThreshold = 25700;
	}

    if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
    {
        totalIncome = w2SumStudent + doc1099SumStudent + selfEmploymentStudent;
        if (totalIncome >= filingThreshold)
        {
            /* supportingDoc.addDocument("IRSForm4868", "Student"); */
            supportingDoc.addAnyDocuments(["IRSForm4868", "Student"],["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"]);
        }
    }
    else if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
    {
        totalIncome = w2SumStudent + doc1099SumStudent + w2SumSpouse + doc1099SumSpouse + selfEmploymentStudent + selfEmploymentSpouse;
        if (totalIncome >= filingThreshold)
        {
            /* supportingDoc.addAnyDocument("IRSForm4868", "Student"); */
            supportingDoc.addAnyDocuments(["IRSForm4868", "Student"],["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"],["TaxReturnTranscript", "Spouse"],["1040x", "Spouse"],["ForeignTaxTranscript", "Spouse"]);
        }
    }
}
if (docOwner == "Parent1")
{
    boolean hasW2Parent1 = receivedDocuments.hasDoc("W2", "Parent1") && receivedDocuments.get("W2", "Parent1").isAcceptable();
    boolean hasW2Parent2 = receivedDocuments.hasDoc("W2", "Parent2") && receivedDocuments.get("W2", "Parent2").isAcceptable();

    String isirPARENTSTAXRETURNFILINGSTATUS = isirRecord.getIsirFieldValue("PARENTSTAXRETURNFILINGSTATUS");
    String isirPARENT1DATEOFBIRTH = isirRecord.getIsirFieldValue("PARENT1DATEOFBIRTH");
    String isirPARENT2DATEOFBIRTH = isirRecord.getIsirFieldValue("PARENT2DATEOFBIRTH");
    String isirPARENTSMARITALSTATUS = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");
    
    boolean parent1Under65 = true;
    boolean parent2Under65 = true;
    Date age65Threshold = groovyScriptHelper.getIsirDate("19511231");
    Date parent1Birthday = groovyScriptHelper.getIsirDate("20170101");
    Date parent2Birthday = groovyScriptHelper.getIsirDate("20170101");
    if (isirPARENT1DATEOFBIRTH != null && !isirPARENT1DATEOFBIRTH.isAllWhitespace())
    {
        parent1Birthday = groovyScriptHelper.getIsirDate(isirPARENT1DATEOFBIRTH);
    }
    if (isirPARENT2DATEOFBIRTH != null && !isirPARENT2DATEOFBIRTH.isAllWhitespace())
    {
        parent2Birthday = groovyScriptHelper.getIsirDate(isirPARENT2DATEOFBIRTH);
    }
    if (parent1Birthday <= age65Threshold)
    {
        parent1Under65 = false;
    }
    if (parent2Birthday <= age65Threshold)
    {
        parent2Under65 = false;
    }
    int filingThreshold = 999999;
    
    if (!hasW2Parent1 && !hasW2Parent2)
    {
        filingThreshold = 400;
    }
    else
    {
		if (isirPARENTSTAXRETURNFILINGSTATUS == "1")
		{
			if (parent1Under65)
			{
				filingThreshold = 12200;
			}
			else
			{
				filingThreshold = 13850;
			}
		}
		if (isirPARENTSTAXRETURNFILINGSTATUS == "4")
		{
			if (parent1Under65)
			{
				filingThreshold = 18350;
			}
			else
			{
				filingThreshold = 20000;
			}
		}
		if (isirPARENTSTAXRETURNFILINGSTATUS == "2")
		{
			if (parent1Under65 && parent2Under65)
			{
				filingThreshold = 24400;
			}
			else if ((parent1Under65 && !parent2Under65) || (!parent1Under65 && parent2Under65))
			{
				filingThreshold = 25700;
			}
			else
			{
				filingThreshold = 27000;
			}
		}
		if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
		{
			filingThreshold = 5;
		}
		if (isirPARENTSTAXRETURNFILINGSTATUS == "5")
		{
			if (parent1Under65)
			{
				filingThreshold = 24400;
			}
			else
			{
				filingThreshold = 25700;
			}
		}
    }
    
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
    if (docOwner == "Parent1")
    {
        String currentAC1013 = currentDocument.getDocumentFieldValue("AC1013");
        if (currentAC1013 != null && !currentAC1013.isAllWhitespace())
        {
            doc1099Parent1Float = doc1099Parent1Float + currentAC1013.toFloat();
        }
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
    if (docOwner == "Parent2")
    {
        String currentAC1013 = currentDocument.getDocumentFieldValue("AC1013");
        if (currentAC1013 != null && !currentAC1013.isAllWhitespace())
        {
            doc1099Parent2Float = doc1099Parent2Float + currentAC1013.toFloat();
        }
    }
    doc1099SumParent2 = doc1099Parent2Float.round();
    
    int totalIncome = 0;
    
    if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
    {
        totalIncome = selfEmploymentParent1 + selfEmploymentParent2 + w2SumParent1 + w2SumParent2 + doc1099SumParent1 + doc1099SumParent2;
        if (totalIncome >= filingThreshold)
        {
            supportingDoc.addAnyDocuments(["IRSForm4868", "Parent1"],["TaxReturnTranscript", "Parent1"],["1040x", "Parent1"],["ForeignTaxTranscript", "Parent1"],["IRSForm4868", "Parent2"],["TaxReturnTranscript", "Parent2"],["1040x", "Parent2"],["ForeignTaxTranscript", "Parent2"]);
        }
    }
    else
    {
        supportingDoc.addAnyDocuments(["IRSForm4868", "Parent1"],["TaxReturnTranscript", "Parent1"],["1040x", "Parent1"],["ForeignTaxTranscript", "Parent1"]);
    }
}