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
if (docOwner == "Student")
{
    String currentAC1010 = currentDocument.getDocumentFieldValue("AC1010");
    if (currentAC1010 != null && !currentAC1010.isAllWhitespace())
    {
        w2SumStudentFloat = w2SumStudentFloat + currentAC1010.toFloat();
    }
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
if (docOwner == "Spouse")
{
    String currentAC1010 = currentDocument.getDocumentFieldValue("AC1010");
    if (currentAC1010 != null && !currentAC1010.isAllWhitespace())
    {
        w2SumSpouseFloat = w2SumSpouseFloat + currentAC1010.toFloat();
    }
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
    filingThreshold = 10300;
}
if (isirFilingStatus == "1" && !studentAgeUnder65)
{
    filingThreshold = 11850;
}
if (isirFilingStatus == "2" && studentAgeUnder65 && spouseAge < 65)
{
    filingThreshold = 20600;
}
if (isirFilingStatus == "2" && (studentAgeUnder65 || spouseAge < 65))
{
    filingThreshold = 21850;
}
if (isirFilingStatus == "2" && !studentAgeUnder65 && spouseAge >= 65)
{
    filingThreshold = 23100;
}
if (isirFilingStatus == "3")
{
    filingThreshold = 4000;
}
if (isirFilingStatus == "4" && studentAgeUnder65)
{
    filingThreshold = 13250;
}
if (isirFilingStatus == "4" && !studentAgeUnder65)
{
    filingThreshold = 14800;
}
if (isirFilingStatus == "5" && studentAgeUnder65)
{
    filingThreshold = 16600;
}
if (isirFilingStatus == "5" && !studentAgeUnder65)
{
    filingThreshold = 17850;
}

if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4" || isirStudentMaritalStatus.isAllWhitespace())
{
    totalIncome = w2SumStudent + doc1099SumStudent + selfEmploymentStudent;
    if (totalIncome >= filingThreshold)
    {
        /* supportingDoc.addDocument("IRSForm4868", "Student"); */
        supportingDoc.addAnyDocuments(["IRSForm4868", "Student"], ["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"]);
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