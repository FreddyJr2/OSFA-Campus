//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationClearanceRulesScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirSTUDENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNCOMPLETED");
String isirStudentIrsRequestFlag = isirRecord.getIsirFieldValue("STUDENTSIRSREQUESTFLAG");
String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");
String isirStudentBirthday = isirRecord.getIsirFieldValue("DATEOFBIRTH");
String isirStudentFilingStatus = isirRecord.getIsirFieldValue("STUDENTSTAXRETURNFILINGSTATUS");
String isirStudentIRSUntaxedPensionsFlag = isirRecord.getIsirFieldValue("STUDENTIRSUNTAXEDPENSIONSFLAG");
String isirStudentIRSIRADistributionFlag = isirRecord.getIsirFieldValue("STUDENTIRSIRADISTRIBUTIONSFLAG");

/* High School Verification */

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

boolean hasVerificationWorksheetIndStudent = false;
if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
{
    hasVerificationWorksheetIndStudent = true;
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

boolean hasNonFilingStudent = false;
if (receivedDocuments.hasDoc("NonFilingStatement", "Student") && receivedDocuments.get("NonFilingStatement", "Student").isAcceptable())
{
    hasNonFilingStudent = true;
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


/* Student Tax Rules */
if (isirSTUDENTSTAXRETURNCOMPLETED == "1")
{
   if (isirStudentIrsRequestFlag == "02" && (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4") && hasRolloverStatement && hsVerificationPass && hasVerificationWorksheetIndStudent)
    {
        return true;
    }
    else if (isirStudentIrsRequestFlag == "02" && (isirStudentIRSUntaxedPensionsFlag != "1" && isirStudentIRSUntaxedPensionsFlag != "2" && isirStudentIRSUntaxedPensionsFlag != "3" && isirStudentIRSUntaxedPensionsFlag != "4" && isirStudentIRSIRADistributionFlag != "1" && isirStudentIRSIRADistributionFlag != "2" && isirStudentIRSIRADistributionFlag != "3" && isirStudentIRSIRADistributionFlag != "4") && hsVerificationPass && hasVerificationWorksheetIndStudent)
    {
        return true;
    }
    
     if ((isirStudentIrsRequestFlag != "02" && isirStudentIrsRequestFlag != "07") && (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4") && hasRolloverStatement && hasVerificationWorksheetIndStudent && (hasTaxTranscriptStudent || has1040Student || has1040xStudent || hasForeignTaxTranscriptStudent) && hsVerificationPass)
    {
        return true;
    }
    
    if ((isirStudentIrsRequestFlag != "02" && isirStudentIrsRequestFlag != "07") && (isirStudentIRSUntaxedPensionsFlag != "1" && isirStudentIRSUntaxedPensionsFlag != "2" && isirStudentIRSUntaxedPensionsFlag != "3" && isirStudentIRSUntaxedPensionsFlag != "4" && isirStudentIRSIRADistributionFlag != "1" && isirStudentIRSIRADistributionFlag != "2" && isirStudentIRSIRADistributionFlag != "3" && isirStudentIRSIRADistributionFlag != "4") && hasVerificationWorksheetIndStudent && (hasTaxTranscriptStudent || has1040Student || has1040xStudent || hasForeignTaxTranscriptStudent))
    {
        return true;
    }
    
    if (isirStudentIrsRequestFlag == "07" && (isirStudentIRSUntaxedPensionsFlag == "1" || isirStudentIRSUntaxedPensionsFlag == "2" || isirStudentIRSUntaxedPensionsFlag == "3" || isirStudentIRSUntaxedPensionsFlag == "4" || isirStudentIRSIRADistributionFlag == "1" || isirStudentIRSIRADistributionFlag == "2" || isirStudentIRSIRADistributionFlag == "3" || isirStudentIRSIRADistributionFlag == "4") && hasRolloverStatement && hasVerificationWorksheetIndStudent && (hasTaxTranscriptStudent || has1040Student) && has1040xStudent && hsVerificationPass)
    {
        return true;
    }
    
    if (isirStudentIrsRequestFlag == "07" && (isirStudentIRSUntaxedPensionsFlag != "1" && isirStudentIRSUntaxedPensionsFlag != "2" && isirStudentIRSUntaxedPensionsFlag != "3" && isirStudentIRSUntaxedPensionsFlag != "4" && isirStudentIRSIRADistributionFlag != "1" && isirStudentIRSIRADistributionFlag != "2" && isirStudentIRSIRADistributionFlag != "3" && isirStudentIRSIRADistributionFlag != "4") && hasVerificationWorksheetIndStudent && (hasTaxTranscriptStudent || has1040Student) && has1040xStudent && hsVerificationPass)
    {
        return true;
    }
}
else
{
    if (hasIrsExtensionStudent && !hasTaxTranscriptStudent && !has1040Student && !has1040xStudent && !hasForeignTaxTranscriptStudent && !hsVerificationPass)
    {
        return false;
    }
    if (isirStudentMaritalStatus == "1" || isirStudentMaritalStatus == "4")
    {
        if (hasVerificationWorksheetIndStudent && hasNonFilingStudent && (hasW2Student || has1099GStudent || hasSelfEmploymentStatementStudent) && hsVerificationPass)
        {
            if (studentTotalIncome >= studentFilingThreshold && (hasTaxTranscriptStudent || has1040Student || has1040xStudent || hasForeignTaxTranscriptStudent))
            {
                return true;
            }
            if (studentTotalIncome < studentFilingThreshold)
            {
                return true;
            }
        }
    }
    if (isirStudentMaritalStatus == "2" || isirStudentMaritalStatus == "3")
    {
        if (hasVerificationWorksheetIndStudent && hasNonFilingStudent && (hasW2Student || has1099GStudent || hasSelfEmploymentStatementStudent) && hsVerificationPass)
        {
            if (studentTotalIncome >= studentFilingThreshold && (((hasTaxTranscriptStudent || has1040Student || has1040xStudent ||  hasForeignTaxTranscriptStudent) && isirStudentFilingStatus != "3") || (isirStudentFilingStatus == "3" && (hasTaxTranscriptSpouse || has1040Spouse || has1040xSpouse ||  hasForeignTaxTranscriptSpouse) && (hasW2Spouse || has1099GSpouse || hasSelfEmploymentStatementSpouse) && (hasTaxTranscriptStudent || has1040Student || has1040xStudent ||  hasForeignTaxTranscriptStudent))))
            {
                if (hasTaxTranscriptStudent)
                {
                    String taxTranscriptFilingStatus = receivedDocuments.get("TaxReturnTranscript", "Student").getDocumentField("AC1065");
                    if (taxTranscriptFilingStatus != null && !taxTranscriptFilingStatus.isAllWhitespace() && taxTranscriptFilingStatus == "Married Filing Separately" && (hasTaxTranscriptSpouse || has1040Spouse))
                    {
                        return true;
                    }
                    else if (taxTranscriptFilingStatus != null && !taxTranscriptFilingStatus.isAllWhitespace() && taxTranscriptFilingStatus != "Married Filing Separately")
                    {
                        return true;
                    }
                }
                if (has1040Student)
                {
                    String f1040FilingStatus = receivedDocuments.get("1040", "Student").getDocumentField("AC1065");
                    if (f1040FilingStatus != null && !f1040FilingStatus.isAllWhitespace() && f1040FilingStatus == "Married Filing Separately" && has1040Spouse)
                    {
                        return true;
                    }
                    else if (f1040FilingStatus != null && !f1040FilingStatus.isAllWhitespace() && f1040FilingStatus != "Married Filing Separately")
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
            if (studentTotalIncome < studentFilingThreshold && isirStudentFilingStatus != "3")
            {
                return true;
                if (isirStudentFilingStatus == "3" && (hasW2Spouse || has1099GSpouse || hasSelfEmploymentStatementSpouse))
                {
                    return true;
                }
            }
        }
    }
}

return false;