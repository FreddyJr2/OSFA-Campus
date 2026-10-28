//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.CoaCostValueScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

if (isirRecords.getCurrentIsirForAwardYear(awy) != null) 
{ 
	String housingCode = isirRecords.getCurrentIsirForAwardYear(awy).getIsirFieldValue("FEDERALSCHOOLCODE1HOUSINGPLANS"); 
	if (housingCode == "3" || housingCode == "2") 
	{ 
		return 5000; 
	} 
} 
return 0;