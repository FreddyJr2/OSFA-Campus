//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.CoaCostValueScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

if (receivedDocuments.forAwardYear(awy).hasDoc("Housing")) {
	def housingDoc = receivedDocuments.forAwardYear(awy).get("Housing")
	def housingStatus = housingDoc.getDocumentField("AC1116")
	if (housingStatus == "Off Campus" || housingStatus == "With Parent") {
		return 5000
	}
}
else if (isirRecords.getCurrentIsirForAwardYear(2024) != null) {
	def housingCode = isirRecords.getCurrentIsirForAwardYear(2024).getIsirFieldValue("FEDERALSCHOOLCODE1HOUSINGPLANS")
	if (housingCode == "3" || housingCode == "2") {
		return 5000
	}
}

return 0