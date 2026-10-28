//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def docOwner = currentDocument.getDocumentOwnerTypes().get(0)
def filingStatus = currentDocument.getDocumentFieldValue("AC1118")

if (docOwner == "Student" && filingStatus != null && !filingStatus.isAllWhitespace() && filingStatus == "Married-Filed Separate Return")
{
    supportingDoc.addAnyDocuments(["TaxReturnTranscript", "Student Spouse"], ["1040", "Student Spouse"], ["1040x", "Student Spouse"],["ForeignTaxTranscript", "Student Spouse"])
}

/*if (docOwner == "Parent" && filingStatus != null && !filingStatus.isAllWhitespace() && filingStatus == "Married Filing Separately")
{
    supportingDoc.addAnyDocuments(["TaxReturnTranscript", "Parent Spouse"], ["1040", "Parent Spouse"], ["1040x", "Parent Spouse"],["ForeignTaxTranscript", "Parent Spouse"])
}*/