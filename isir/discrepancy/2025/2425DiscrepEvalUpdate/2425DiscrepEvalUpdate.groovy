//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/*
Due to the ISIR field related to this discrepancy being related to an FTI value within the 24/25 ISIR we are no longer automatically
triggering this ISIR discrepancy within our baseline configuration.  If Oracle determines that there is additional discrepancy logic
that can be added for the manually entered field related to this ISIR field we will enhance our baseline configuration in future releases.
 */