//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String docOwner = currentDocument.getDocumentOwnerTypes().get(0);
String filingStatus = currentDocument.getDocumentFieldValue("AC1065");

if (docOwner == "Student" && filingStatus != null && !filingStatus.isAllWhitespace() && filingStatus == "Married Filing Separately")
{
    supportingDoc.addAnyDocuments(["TaxReturnTranscript", "Spouse"], ["1040", "Spouse"], ["1040x", "Spouse"],["ForeignTaxTranscript", "Spouse"]);
}

/*if (docOwner == "Parent1" && filingStatus != null && !filingStatus.isAllWhitespace() && filingStatus == "Married Filing Separately")
{
    supportingDoc.addAnyDocuments(["TaxReturnTranscript", "Parent2"], ["1040", "Parent2"], ["1040x", "Parent2"],["ForeignTaxTranscript", "Parent2"]);
}*/