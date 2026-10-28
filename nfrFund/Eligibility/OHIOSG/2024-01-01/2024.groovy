//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NfrEligibilityScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

def NfrEligibilityField automatedFundEligibility = new NfrEligibilityField("Automated Fund", true)

return [automatedFundEligibility]