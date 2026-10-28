//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationClearanceRulesScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirSTUDENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNCOMPLETED");
String isirStudentIrsRequestFlag = isirRecord.getIsirFieldValue("STUDENTSIRSREQUESTFLAG");
String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");
String isirStudentBirthday = isirRecord.getIsirFieldValue("DATEOFBIRTH");
String isirFilingStatus = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNFILINGSTATUS");

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

boolean hasVerificationWorksheetIndStudent = false;
if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
{
    hasVerificationWorksheetIndStudent = true;
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

boolean hasNonFilingStudent = false;
if (receivedDocuments.hasDoc("NonFilingStatement", "Student") && receivedDocuments.get("NonFilingStatement", "Student").isAcceptable())
{
    hasNonFilingStudent = true;
}

boolean hasIrsExtensionStudent = false;
if (receivedDocuments.hasDoc("IRSForm4868", "Student") && receivedDocuments.get("IRSForm4868", "Student").isAcceptable())
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

int totalIncome = w2SumStudent + w2SumSpouse + doc1099SumStudent + doc1099SumSpouse + selfEmploymentStudent + selfEmploymentSpouse;

if (isirSTUDENTSTAXRETURNCOMPLETED == "1")
{
    if (isirStudentIrsRequestFlag == "02" && hasVerificationWorksheetIndStudent)
    {
        return true;
    }
    if (hasVerificationWorksheetIndStudent && (hasTaxTranscriptStudent || has1040xStudent || hasForeignTaxTranscriptStudent))
    {
        return true;
    }
}
else
{
    if (hasIrsExtensionStudent && !hasTaxTranscriptStudent && !has1040xStudent && !hasForeignTaxTranscriptStudent)
    {
        return false;
    }
    if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4")
    {
        if (hasVerificationWorksheetIndStudent && hasNonFilingStudent && (hasW2Student || has1099GStudent || hasSelfEmploymentStatementStudent))
        {
            if (totalIncome >= filingThreshold && (hasTaxTranscriptStudent || has1040xStudent || hasForeignTaxTranscriptStudent))
            {
                return true;
            }
            if (totalIncome < filingThreshold)
            {
                return true;
            }
        }
    }
    if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
    {
        if (hasVerificationWorksheetIndStudent && hasNonFilingStudent && (hasW2Student || has1099GStudent || hasSelfEmploymentStatementStudent) && (hasW2Spouse || has1099GSpouse || hasSelfEmploymentStatementSpouse))
        {
            if (totalIncome >= filingThreshold && (hasTaxTranscriptStudent || has1040xStudent || hasForeignTaxTranscriptStudent))
            {
                if (hasTaxTranscriptStudent)
                {
                    String taxTranscriptFilingStatus = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1065");
                    if (taxTranscriptFilingStatus != null && !taxTranscriptFilingStatus.isAllWhitespace() && taxTranscriptFilingStatus == "Married Filing Separately" && hasTaxTranscriptSpouse)
                    {
                        return true;
                    }
                    else if (taxTranscriptFilingStatus != null && !taxTranscriptFilingStatus.isAllWhitespace() && taxTranscriptFilingStatus != "Married Filing Separately")
                    {
                        return true;
                    }
                }
                if (has1040xStudent)
                {
                    String f1040xFilingStatus = receivedDocuments.get("1040x", "Student").getDocumentField("AC1065");
                    if (f1040xFilingStatus != null && !f1040xFilingStatus.isAllWhitespace() && f1040xFilingStatus == "Married Filing Separately" && has1040xSpouse)
                    {
                        return true;
                    }
                    else if (f1040xFilingStatus != null && !f1040xFilingStatus.isAllWhitespace() && f1040xFilingStatus != "Married Filing Separately")
                    {
                        return true;
                    }
                }
                if (hasForeignTaxTranscriptStudent)
                {
                    String foreignTaxTranscriptFilingStatus = receivedDocuments.get("ForeignTaxTranscript", "Student").getDocumentField("AC1065");
                    if (foreignTaxTranscriptFilingStatus != null && !foreignTaxTranscriptFilingStatus.isAllWhitespace() && foreignTaxTranscriptFilingStatus == "Married Filing Separately" && hasForeignTaxTranscriptSpouse)
                    {
                        return true;
                    }
                    else if (foreignTaxTranscriptFilingStatus != null && !foreignTaxTranscriptFilingStatus.isAllWhitespace() && foreignTaxTranscriptFilingStatus != "Married Filing Separately")
                    {
                        return true;
                    }
                }
            }
            if (totalIncome < filingThreshold)
            {
                return true;
            }
        }
    }
}

return false;