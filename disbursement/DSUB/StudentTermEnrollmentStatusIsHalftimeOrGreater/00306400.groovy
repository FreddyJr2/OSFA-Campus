//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.ConfigurableDisbursementCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

if (program.isTerm()) {
	def termEnrollmentStatus = term.getStatus();

	if (["HALF_TIME", "THREE_QUARTER_TIME", "FULL_TIME"].contains(termEnrollmentStatus)) {
		return true;
	}
	return false;
}
return null;