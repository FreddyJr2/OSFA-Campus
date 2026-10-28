//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.ExitCounselingGraduatingCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

import org.joda.time.LocalDate;

boolean returnValue = false;
LocalDate academicCompletionDate = primaryProgram.getAcademicCompletionDate();

/* no API available for disbursements, and no way to separate out TEACH criteria based on current design */

if (academicCompletionDate != null)
{
    returnValue = true;
}
return returnValue;