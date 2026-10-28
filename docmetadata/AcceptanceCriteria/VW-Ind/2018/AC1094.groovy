//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

if (isirRecord == null)
{
    return;
}

String currentDocSignatureLastName = currentDocument.getDocumentFieldValue("AC1094");

String isirStudentLastName = isirRecord.getIsirFieldValue("STUDENTLASTNAME");

if (currentDocSignatureLastName != null && !currentDocSignatureLastName.isAllWhitespace() && isirStudentLastName != null && !isirStudentLastName.isAllWhitespace())
{
    if (!isirStudentLastName.equalsIgnoreCase(currentDocSignatureLastName))
    {
        supportingDoc.addAnyDocuments(["LegalNameChange", "Student"], ["MarriageCertificate", "Student"],["DriversLicense", "Student"],["Passport", "Student"],["NonDriversLicenseID", "Student"]);
    }
}