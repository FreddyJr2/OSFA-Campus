//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.ConfigurableDisbursementCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

def courses = primaryProgram.getCourses().getStartingIn(period);
def firstAra = !courses.isEmpty() && courses.asFluent().toSortedList({ c1, c2 -> c1.getStartDate().compareTo(c2.getStartDate())}).get(0).getFirstAraIndicator();
return firstAra;