//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationDocumentsRequiredScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Student ISIR Variables */
String isirSTUDENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNCOMPLETED");
String isirStudentIrsRequestFlag = isirRecord.getIsirFieldValue("STUDENTSIRSREQUESTFLAG");
String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");
String isirStudentFilingStatus = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNFILINGSTATUS");
String isirStudentsIRSUntaxedIRADistAndPensionsFlag = isirRecord.getIsirFieldValue("STUDENTSIRSUNTAXEDIRADISTANDPENSIONSFLAG");
String isirStudentsIncarceratedApplicantFlag = isirRecord.getIsirFieldValue("INCARCERATEDAPPLICANTFLAG");

/* If the Incarcerated Applicant flag (Field #384) from the ISIR is set to values 1, 2, or 3, then do not request
* any documents and exit the script */
if (isirStudentsIncarceratedApplicantFlag == "1" || isirStudentsIncarceratedApplicantFlag == "2" || isirStudentsIncarceratedApplicantFlag == "3")
{
    return;
}
else {
    boolean hasVerificationWorksheetIndStudent = false;
    if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
    {
        hasVerificationWorksheetIndStudent = true;
    }

    boolean hasIrsExtensionStudent = false;
    if (receivedDocuments.hasDoc("IRSForm4868", "Student") && receivedDocuments.get("IRSForm4868", "Student").isAcceptable() && receivedDocuments.hasDoc("IRSExtensionApproval", "Student") && receivedDocuments.get("IRSExtensionApproval", "Student").isAcceptable())
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

    documentRequest.addDocument("VW-Ind", "Student");

    if (isirSTUDENTSTAXRETURNCOMPLETED == "1")
    {
        if (isirStudentIrsRequestFlag == "02" && (isirStudentsIRSUntaxedIRADistAndPensionsFlag == "1" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "2" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "3" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "4"))
        {
            documentRequest.addDocument("RolloverStatement", "Student");
        }

        if (isirStudentIrsRequestFlag != "02" && isirStudentIrsRequestFlag != "07")
        {
            documentRequest.addAnyDocuments(["TaxReturnTranscript","Student"],["1040","Student"],["1040x","Student"],["ForeignTaxTranscript","Student"]);


            if (isirStudentsIRSUntaxedIRADistAndPensionsFlag == "1" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "2" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "3" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "4")
            {
                documentRequest.addDocument("RolloverStatement", "Student");
            }
        }

        if (isirStudentIrsRequestFlag == "07")
        {
            documentRequest.addAnyDocuments(["TaxReturnTranscript", "Student"],["1040","Student"]);
            documentRequest.addDocument("1040x", "Student");
            if (isirStudentsIRSUntaxedIRADistAndPensionsFlag == "1" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "2" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "3" || isirStudentsIRSUntaxedIRADistAndPensionsFlag == "4")
            {
                documentRequest.addDocument("RolloverStatement", "Student");
            }
        }
    }
    else
    {
        if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3" || isirStudentMaritalStatus == "4")
        {
            documentRequest.addAnyDocuments(["NonFilingStatement","Student"],["W2","Student"],["1099G","Student"],["SelfEmploymentStatement","Student"]);
            if (isirStudentFilingStatus == "3")
            {
                documentRequest.addAnyDocuments(["NonFilingStatement","Spouse"],["W2","Spouse"],["1099G","Spouse"],["SelfEmploymentStatement","Spouse"]);
            }
        }
        return;
    }
}