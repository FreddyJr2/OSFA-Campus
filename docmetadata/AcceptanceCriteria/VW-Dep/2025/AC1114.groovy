//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//if no ISIR, then exit the script
if (isirRecord == null) {
    return
}

String currentDocSignatureLastName = currentDocument.getDocumentFieldValue("AC1114")
def isirPARENT_LASTNAME = isirRecord.getField(IsirField.PARENT_LASTNAME).getOrNull()
def isirPARENTSPOUSE_LASTNAME = isirRecord.getField(IsirField.PARENTSPOUSE_LASTNAME).getOrNull()

if (currentDocSignatureLastName != null && !currentDocSignatureLastName.isAllWhitespace() && isirPARENT_LASTNAME != null && isirPARENTSPOUSE_LASTNAME != null)
{
    if (!isirPARENT_LASTNAME.equalsIgnoreCase(currentDocSignatureLastName) && !isirPARENTSPOUSE_LASTNAME.equalsIgnoreCase(currentDocSignatureLastName)) 
    {
        supportingDoc.addAnyDocuments(["LegalNameChange","Parent"],["MarriageCertificate","Parent"],["DriversLicense","Parent"],["Passport","Parent"],["NonDriversLicenseID","Parent"],["LegalNameChange","Parent Spouse"],["MarriageCertificate","Parent Spouse"],["DriversLicense","Parent Spouse"],["Passport","Parent Spouse"],["NonDriversLicenseID","Parent Spouse"])
    }
}
