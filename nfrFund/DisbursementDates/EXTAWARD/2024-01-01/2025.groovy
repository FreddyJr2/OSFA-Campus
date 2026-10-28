//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NfrDisbursementDatesScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

import org.joda.time.LocalDate

//Schedule disbursement 5 days after the period started
return period.startDate.plusDays(5)