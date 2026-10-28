//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.GlobalEnrollmentStatusCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Global Enrollment Status - SFP Baseline - Combined Term and Non-Term */

/*
def actualCourses = helper.filterCourses(program)
            .period(term)
            .safiCreatedDate(safiCreatedDate)
            .go();
*/

import org.joda.time.LocalDate;

if (program.isTerm())
{

    // If the student decides to cancel their enrollment and the Program Enrollment Status is X, then return NOT_ATTENDING
    if (primaryProgram.getEnrollmentStatus() != null && primaryProgram.getEnrollmentStatus() == "X")
    {
        return "NOT_ATTENDING";
    }

    def countedUnits = 0.0;
    def safiTerm = program.getTerms().getOverlappingWith(term).get(0);

    // Institutions should define their own add drop policy
    def addDropPolicy = term.getStartDate().plusDays(10);

    log.debug ("Term start date = {}, term start date + 10 = {}", term.getStartDate(), addDropPolicy);

    for (def course: program.getCourses().getAssociatedTo(period))
    {
        log.debug("GLOBAL ENROLLMENT SCRIPT, COURSE IN LOOP: courseEndDate = {}, courseAraIndicator = {}, courseSchedulingStatus = {}, courseUnitsToAdd = {}", course.getEndDate(), course.getFirstAraIndicator(), course.getSchedulingStatus(), course.getUnits());

        // If the course is withdrawn, only count the credits if the course was withdrawn after the add/drop period. If the course is not withdrawn, count the course credits.
        if ((["WITHDRAWN"].contains(course.getSchedulingStatus()) && course.getLastDateOfAttendance() != null && course.getLastDateOfAttendance() > addDropPolicy) || course.getSchedulingStatus() != "WITHDRAWN")
        {
            countedUnits = countedUnits + course.getUnits();
        }
    }

    def NA = 0.0;
    def LTHT = 6.0;
    def HT = 9.0;
    def TQT = 12.0;

    return helper.mapRanges()
            .le(NA, "NOT_ATTENDING")
            .lt(LTHT, "LESS_THAN_HALF_TIME")
            .lt(HT, "HALF_TIME")
            .lt(TQT, "THREE_QUARTER_TIME")
            .defaultValue("FULL_TIME")
            .apply(countedUnits);
}
return "FULL_TIME";