//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//if no ISIR, then exit the script
if (isirRecord == null) {
    return
}

String currentDocSignatureLastName = currentDocument.getDocumentFieldValue("AC1094")
def isirSTUDENT_LASTNAME = isirRecord.getField(IsirField.STUDENT_LASTNAME).getOrNull()

if (currentDocSignatureLastName != null && !currentDocSignatureLastName.isAllWhitespace() && isirSTUDENT_LASTNAME != null)
{
    if (!isirSTUDENT_LASTNAME.equalsIgnoreCase(currentDocSignatureLastName))
    {
        supportingDoc.addAnyDocuments(["LegalNameChange", "Student"], ["MarriageCertificate", "Student"],["DriversLicense", "Student"],["Passport", "Student"],["NonDriversLicenseID", "Student"])
    }
}