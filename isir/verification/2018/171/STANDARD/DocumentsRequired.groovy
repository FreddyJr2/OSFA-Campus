//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationDocumentsRequiredScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirSTUDENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNCOMPLETED");
String isirStudentIrsRequestFlag = isirRecord.getIsirFieldValue("STUDENTSIRSREQUESTFLAG");
String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");
String isirStudentBirthday = isirRecord.getIsirFieldValue("DATEOFBIRTH");
String isirFilingStatus = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNFILINGSTATUS");

boolean hasVerificationWorksheetIndStudent = false;
if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
{
    hasVerificationWorksheetIndStudent = true;
}

boolean hasIrsExtensionStudent = false;
if (receivedDocuments.hasDoc("IRSForm4868", "Student") && receivedDocuments.get("IRSForm4868", "Student").isAcceptable())
{
    hasIrsExtensionStudent = true;
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

documentRequest.addDocument("VW-Ind", "Student");

if (isirSTUDENTSTAXRETURNCOMPLETED == "1")
{
    if (isirStudentIrsRequestFlag == "02")
    {
        return;
    }
    else
    {
        documentRequest.addAnyDocuments(["TaxReturnTranscript","Student"],["1040x","Student"],["ForeignTaxTranscript","Student"]);
        return;
    }
}
else
{
    documentRequest.addDocument("NonFilingStatement", "Student");
    if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4")
    {
        documentRequest.addAnyDocuments(["W2","Student"],["1099G","Student"],["SelfEmploymentStatement","Student"]);
    }
    if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
    {
        documentRequest.addAnyDocuments(["W2","Student"],["1099G","Student"],["SelfEmploymentStatement","Student"]);
        documentRequest.addAnyDocuments(["W2","Spouse"],["1099G","Spouse"],["SelfEmploymentStatement","Spouse"]);
    }
    
    /* TODO: Add supporting document request for IRS extension form in document acceptance criteria for W2 and 1099 */
        
    return;
}

return;