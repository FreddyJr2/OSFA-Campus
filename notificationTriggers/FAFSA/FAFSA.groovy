//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NotificationRequiredCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

// Determine if the student has an ISIR for the current award year
boolean hasIsirForCurrentAwardYear() {
    return !allAwardYearIsirs.forAwardYear(awy).isEmpty()
}

// Either no acy exists because the student isn't packaged yet, or an acy exist where
boolean hasPaymentPeriodInCurrentAwardYear() {
    if (acys == null) return true;
    return acys.grantPaymentPeriods.find {
        helper.awardYear.forDate(it.startDate).endYear == awy || helper.awardYear.forDate(it.endDate).endYear == awy
    } != null
}

// Is the FAFSA regulatory period still open
def isOpened = fafsaAwardYearInfoList.find { it.awardYear.endYear == awy }.open
def hasIsirForCurrentAwardYear = hasIsirForCurrentAwardYear()
def hasPaymentPeriodInCurrentAwardYear = hasPaymentPeriodInCurrentAwardYear()

log.debug("Notification Triggers FAFSA.groovy - hasIsirForCurrentAwardYear={} && hasPaymentPeriodInCurrentAwardYear={} && isOpened={}",
        hasIsirForCurrentAwardYear, hasPaymentPeriodInCurrentAwardYear, isOpened)

return !hasIsirForCurrentAwardYear && hasPaymentPeriodInCurrentAwardYear && isOpened