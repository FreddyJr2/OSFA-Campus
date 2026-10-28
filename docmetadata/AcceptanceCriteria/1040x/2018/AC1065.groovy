//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String filingStatus = currentDocument.getDocumentFieldValue("AC1065");

if (filingStatus != null && !filingStatus.isAllWhitespace() && filingStatus == "Married Filing Separately")
{
    supportingDoc.addAnyDocuments(["TaxReturnTranscript", "Spouse"], ["1040x", "Spouse"],["ForeignTaxTranscript", "Spouse"]);
}