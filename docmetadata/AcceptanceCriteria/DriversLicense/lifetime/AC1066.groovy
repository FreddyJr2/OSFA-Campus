//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String IssueAgency= currentDocument.getDocumentFieldValue("AC1066")
if (IssueAgency != null && !IssueAgency.isAllWhitespace())
{   
    if (IssueAgency == "Other")
    {
        returnList.get(0).setScriptReturnErrorString("Document must be reviewed manually")
        return
    }    
}