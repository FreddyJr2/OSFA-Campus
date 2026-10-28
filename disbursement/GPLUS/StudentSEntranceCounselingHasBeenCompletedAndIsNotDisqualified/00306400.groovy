//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.ConfigurableDisbursementCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

return cod.getCrecs().firstMatch({ crec -> crec.getCounselingCompleteDate() != null && crec.getCounselingType() == "DLPEntranceCounseling"; }).isPresent();