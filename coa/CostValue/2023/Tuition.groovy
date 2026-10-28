//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.CoaCostValueScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

def referencePeriod = program.isTerm() ? term : acy

// Institutions should define their own add drop policy
LocalDate addDropPolicy = referencePeriod.getStartDate().plusDays(10);
def countedUnits = 0.0;
String programType = primaryProgram.getProgramType();

// If the Program Enrollment Status is not X, then count credits
if (primaryProgram.getEnrollmentStatus() != null && primaryProgram.getEnrollmentStatus() != "X")
{
	for (def course : program.getCourses().getAssociatedTo(referencePeriod))
	{
		log.debug("COA SCRIPT Tuition, COURSE IN LOOP: countedUnits = {}, courseEndDate = {}, courseAraIndicator = {}, courseSchedulingStatus = {}, courseUnitsToAdd = {}", countedUnits, course.getEndDate(), course.getFirstAraIndicator(), course.getSchedulingStatus(), course.getUnits());

		// If the course is withdrawn, only count the credits if the course was withdrawn after the add/drop period. If the course is not withdrawn, count the course credits.
		if ((["WITHDRAWN"].contains(course.getSchedulingStatus()) && course.getLastDateOfAttendance() != null && course.getLastDateOfAttendance() > addDropPolicy) || course.getSchedulingStatus() != "WITHDRAWN")
		{
			countedUnits = countedUnits + course.getUnits();
		}
	}
}

log.debug("COA SCRIPT Tuition - final value of countedUnits = {}", countedUnits);

// Use different cost values for different program types
if (programType == "ASSOCIATE" || programType == "BACHELORS")
{
	return (countedUnits * 175);
}
else
{
	return (countedUnits * 225);
}