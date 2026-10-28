//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NfrPeriodsScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

def lpps = acy.loanPeriods.paymentPeriods
        .findAll {
            it.awardYear == null || it.awardYear == awyPeriod}

return lpps.collect { lpp ->
        def respectiveTerm = acy.terms.getOverlappingWith(lpp)[0]
        def periodNo = respectiveTerm != null
                ? respectiveTerm.number
                : lpps.indexOf(lpp) + 1
        def periodName = respectiveTerm != null
                ? respectiveTerm.name
                : "Period " + periodNo
        new NfrPeriod(periodNo, periodName, lpp.startDate, lpp.endDate)
}