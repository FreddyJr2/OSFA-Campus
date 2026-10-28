//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatterBuilder

//Variable definitions
def isirSTUDENT_DATEOFBIRTH = isirRecord.getField(IsirField.STUDENT_DATEOFBIRTH).getOrNull()
def documentDateFormatter = new DateTimeFormatterBuilder()
        .appendYear(4, 4)
        .appendMonthOfYear(2)
        .appendDayOfMonth(2)
        .toFormatter()

if (receivedDocuments.hasDoc("DriversLicense","Student") && receivedDocuments.get("DriversLicense","Student").isAcceptable() && receivedDocuments.get("DriversLicense", "Student").getDocumentField("AC1003") != null && !receivedDocuments.get("DriversLicense", "Student").getDocumentField("AC1003").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("DriversLicense", "Student").getDocumentField("AC1003")
    def documentDate = LocalDate.parse(documentValue.substring(0, 8), documentDateFormatter)
    if (documentDate != isirSTUDENT_DATEOFBIRTH.secureValue())
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("NonDriversLicenseID","Student") && receivedDocuments.get("NonDriversLicenseID","Student").isAcceptable() && receivedDocuments.get("NonDriversLicenseID", "Student").getDocumentField("AC1003") != null && !receivedDocuments.get("NonDriversLicenseID", "Student").getDocumentField("AC1003").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("NonDriversLicenseID", "Student").getDocumentField("AC1003")
    def documentDate = LocalDate.parse(documentValue.substring(0, 8), documentDateFormatter)
    if (documentDate != isirSTUDENT_DATEOFBIRTH.secureValue())
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("StateIssuedID","Student") && receivedDocuments.get("StateIssuedID","Student").isAcceptable() && receivedDocuments.get("StateIssuedID", "Student").getDocumentField("AC1003") != null && !receivedDocuments.get("StateIssuedID", "Student").getDocumentField("AC1003").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("StateIssuedID", "Student").getDocumentField("AC1003")
    def documentDate = LocalDate.parse(documentValue.substring(0, 8), documentDateFormatter)
    if (documentDate != isirSTUDENT_DATEOFBIRTH.secureValue())
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}

if (receivedDocuments.hasDoc("Passport","Student") && receivedDocuments.get("Passport","Student").isAcceptable() && receivedDocuments.get("Passport", "Student").getDocumentField("AC1003") != null && !receivedDocuments.get("Passport", "Student").getDocumentField("AC1003").isAllWhitespace())
{
    def documentValue = receivedDocuments.get("Passport", "Student").getDocumentField("AC1003")
    def documentDate = LocalDate.parse(documentValue.substring(0, 8), documentDateFormatter)
    if (documentDate != isirSTUDENT_DATEOFBIRTH.secureValue())
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.needManualReview = true
        return
    }
}