//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

if (isirRecord == null)
{
    return;
}

String currentDocSignatureLastName = currentDocument.getDocumentFieldValue("AC1114");

String isirParent1LastName = isirRecord.getIsirFieldValue("PARENT1LASTNAME");
String isirParent2LastName = isirRecord.getIsirFieldValue("PARENT2LASTNAME");

if (currentDocSignatureLastName != null && !currentDocSignatureLastName.isAllWhitespace() && isirParent1LastName != null && !isirParent1LastName.isAllWhitespace() && isirParent2LastName != null && !isirParent2LastName.isAllWhitespace())
{
    if (!isirParent1LastName.equalsIgnoreCase(currentDocSignatureLastName) && !isirParent2LastName.equalsIgnoreCase(currentDocSignatureLastName)) 
    {
        supportingDoc.addAnyDocuments(["LegalNameChange","Parent1"],["MarriageCertificate","Parent1"],["DriversLicense","Parent1"],["Passport","Parent1"],["NonDriversLicenseID","Parent1"],["LegalNameChange","Parent2"],["MarriageCertificate","Parent2"],["DriversLicense","Parent2"],["Passport","Parent2"],["NonDriversLicenseID","Parent2"]);
    }
}
