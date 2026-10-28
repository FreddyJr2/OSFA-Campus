//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSTUDENT_LASTNAME = isirRecord.getField(IsirField.STUDENT_LASTNAME).getOrNull()

if (receivedDocuments.hasDoc("DriversLicense","Student") && receivedDocuments.get("DriversLicense","Student").isAcceptable() && receivedDocuments.get("DriversLicense", "Student").getDocumentField("AC1002") != null && !receivedDocuments.get("DriversLicense", "Student").getDocumentField("AC1002").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("DriversLicense", "Student").getDocumentField("AC1002")
    if (!documentValue.equalsIgnoreCase(isirSTUDENT_LASTNAME))
    {
        if (documentValue.length() > 35)
        {
            documentValue = documentValue.substring(0,35)
        }
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("NonDriversLicenseID","Student") && receivedDocuments.get("NonDriversLicenseID","Student").isAcceptable() && receivedDocuments.get("NonDriversLicenseID", "Student").getDocumentField("AC1002") != null && !receivedDocuments.get("NonDriversLicenseID", "Student").getDocumentField("AC1002").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("NonDriversLicenseID", "Student").getDocumentField("AC1002").replaceAll("-","")
    if (!documentValue.equalsIgnoreCase(isirSTUDENT_LASTNAME))
    {
        if (documentValue.length() > 35)
        {
            documentValue = documentValue.substring(0,35)
        }
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("StateIssuedID","Student") && receivedDocuments.get("StateIssuedID","Student").isAcceptable() && receivedDocuments.get("StateIssuedID", "Student").getDocumentField("AC1002") != null && !receivedDocuments.get("StateIssuedID", "Student").getDocumentField("AC1002").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("StateIssuedID", "Student").getDocumentField("AC1002").replaceAll("-","")
    if (!documentValue.equalsIgnoreCase(isirSTUDENT_LASTNAME))
    {
        if (documentValue.length() > 35)
        {
            documentValue = documentValue.substring(0,35)
        }
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("Passport","Student") && receivedDocuments.get("Passport","Student").isAcceptable() && receivedDocuments.get("Passport", "Student").getDocumentField("AC1002") != null && !receivedDocuments.get("Passport", "Student").getDocumentField("AC1002").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("Passport", "Student").getDocumentField("AC1002").replaceAll("-","")
    if (!documentValue.equalsIgnoreCase(isirSTUDENT_LASTNAME))
    {
        if (documentValue.length() > 35)
        {
            documentValue = documentValue.substring(0,35)
        }
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("LegalNameChange","Student") && receivedDocuments.get("LegalNameChange","Student").isAcceptable() && receivedDocuments.get("LegalNameChange", "Student").getDocumentField("AC1096") != null && !receivedDocuments.get("LegalNameChange", "Student").getDocumentField("AC1096").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("LegalNameChange", "Student").getDocumentField("AC1096").replaceAll("-","")
    if (!documentValue.equalsIgnoreCase(isirSTUDENT_LASTNAME))
    {
        if (documentValue.length() > 35)
        {
            documentValue = documentValue.substring(0,35)
        }
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("MarriageCertificate","Student") && receivedDocuments.get("MarriageCertificate","Student").isAcceptable() && receivedDocuments.get("MarriageCertificate", "Student").getDocumentField("AC1096") != null && !receivedDocuments.get("MarriageCertificate", "Student").getDocumentField("AC1096").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("MarriageCertificate", "Student").getDocumentField("AC1096").replaceAll("-","")
    if (!documentValue.equalsIgnoreCase(isirSTUDENT_LASTNAME))
    {
        if (documentValue.length() > 35)
        {
            documentValue = documentValue.substring(0,35)
        }
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}