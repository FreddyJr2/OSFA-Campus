//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.PellEnrollmentIntensityScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

def countedUnits = 0.0
def enrollmentIntensity = 0.0
def creditsRequiredForFullTimeEnrollment = 12
LocalDate today = LocalDate.now()
LocalDate censusDate

// if enrollment status is X return 0 (0%)
if (primaryProgram.getEnrollmentStatus() != null && primaryProgram.getEnrollmentStatus() == "X") {
    return 0
}

// if not term, return 1 (100%)
if (!program.isTerm()) {
    return 1
}

//term program logic
censusDate = term.getStartDate().plusDays(10)

//count credits and calculate enrollmentIntensity
for (def course: program.getCourses().getAssociatedTo(term)) {
    log.debug("PellEnrollmentIntensity script, COURSE IN LOOP: courseEndDate = {}, courseAraIndicator = {}, courseSchedulingStatus = {}, courseUnitsToAdd = {}", course.getEndDate(), course.getFirstAraIndicator(), course.getSchedulingStatus(), course.getUnits())
    // If the course is withdrawn, only count the credits if the course was withdrawn after the census date. If the course is not withdrawn, count the course credits.
    if ((["WITHDRAWN"].contains(course.getSchedulingStatus()) && course.getLastDateOfAttendance() != null && course.getLastDateOfAttendance() > censusDate) || course.getSchedulingStatus() != "WITHDRAWN") {
        countedUnits = countedUnits + course.getUnits()
    }
    enrollmentIntensity = countedUnits / creditsRequiredForFullTimeEnrollment
}

//census date logic
if (censusDate != null && today <= censusDate) {
    log.debug("PellEnrollmentIntensity script if statement final values: enrollmentIntensity = {}, countedUnits = {}, creditsRequiredForFullTimeEnrollment = {}", enrollmentIntensity, countedUnits, creditsRequiredForFullTimeEnrollment)
    return enrollmentIntensity
}
else {

    //define previousFpo
    log.debug("PellEnrollmentIntensity script else statement begin")
    def previousFpo
    if (previousFinancialPlan.effectiveAt(censusDate) != null) {
        log.debug("PellEnrollmentIntensity script using census date previous FPO")
        previousFpo = previousFinancialPlan.effectiveAt(censusDate)
    } else if (financialPlan.getVersion() > 1) {
        log.debug("PellEnrollmentIntensity script using (FPO version - 1) previous FPO")
        previousFpo = previousFinancialPlan.withVersion(financialPlan.getVersion() - 1)
    }

    //previousFpo is not null logic
    if (previousFpo != null) {
        log.debug("PellEnrollmentIntensity script previousFpo is not null")
        def prevEnrollmentIntensities = previousFpo.getInAwardYear(awy).getOverlappingWith(term).getEnrollmentIntensities()
        if (prevEnrollmentIntensities.isEmpty()) {
            log.debug("PellEnrollmentIntensity script, prevEnrollmentIntensities is empty, final values: enrollmentIntensity = {}, countedUnits = {}, creditsRequiredForFullTimeEnrollment = {}", enrollmentIntensity, countedUnits, creditsRequiredForFullTimeEnrollment)
            return enrollmentIntensity
        }
        enrollmentIntensity = prevEnrollmentIntensities.get()
        log.debug("PellEnrollmentIntensity script, prevEnrollmentIntensities is NOT empty, final values: enrollmentIntensity = {}, countedUnits = {}, creditsRequiredForFullTimeEnrollment = {}", enrollmentIntensity, countedUnits, creditsRequiredForFullTimeEnrollment)
    }
    else {
        //previousFpo is null message
        log.debug("PellEnrollmentIntensity script previousFpo is null")
    }
}

return enrollmentIntensity