//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.ExitCounselingWithdrawnCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

boolean returnValue = false;

/* no API available for disbursements, and no way to separate out TEACH criteria based on current design */

if (primaryProgram.getEnrollmentStatus() == "W")
{
    returnValue = true;
}
return returnValue;