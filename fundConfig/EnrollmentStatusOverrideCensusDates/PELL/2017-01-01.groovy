//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.EnrollmentStatusOverrideScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Pell Enrollment Status Override with Census Date - combined Term and Non-Term*/

import org.joda.time.LocalDate;

if (program.isTerm())
{
    LocalDate termStartDate = term.getStartDate();
    LocalDate termEndDate = term.getEndDate();
    LocalDate censusDate;
    LocalDate today = LocalDate.now();

    String programType = primaryProgram.getProgramType();

    if (termStartDate < new LocalDate(2017, 10, 2) && termStartDate >= new LocalDate(2017, 7, 1))
    {
        censusDate = new LocalDate(2017, 7, 24);
    }
    else if (termStartDate < new LocalDate(2018, 1, 2) && termStartDate >= new LocalDate(2017, 10, 2))
    {
        censusDate = new LocalDate(2017, 10, 23);
    }
    else if (termStartDate < new LocalDate(2018, 4, 2) && termStartDate >= new LocalDate(2018, 1, 2))
    {
        censusDate = new LocalDate(2018, 1, 22);
    }
    else if (termStartDate < new LocalDate(2018, 7, 2) && termStartDate >= new LocalDate(2018, 4, 2))
    {
        censusDate = new LocalDate(2018, 4, 23);
    }
    else if (termStartDate < new LocalDate(2018, 10, 1) && termStartDate >= new LocalDate(2018, 7, 2))
    {
        censusDate = new LocalDate(2018, 7, 23);
    }
    else if (termStartDate < new LocalDate(2019, 1, 2) && termStartDate >= new LocalDate(2018, 10, 1))
    {
        censusDate = new LocalDate(2018, 10, 22);
    }

    log.debug("CONFIG LOGGING STARTS HERE");
    log.debug("censusDate = {}", censusDate);
    log.debug("today = {}", today);
    log.debug("termStartDate = {}", termStartDate);
    
    def safiTerm = program.getTerms().getOverlappingWith(term).get(0);
    
    if (censusDate != null && today > censusDate && (program.getEnrollmentStatus() == "W" || safiTerm.getStudentsTermStatus() == "WITHDRAWN"))
    {
        return enrollmentStatus;
        
            /*
            def countedUnits = 0.0;
        
            for (def course: program.getCourses().getAssociatedTo(period)) 
            {   
                if (["WITHDRAWN"].contains(course.getSchedulingStatus()))
                {
                    if (course.getLastDateOfAttendance() != null && ((program.getOfficialLastDateOfAttendance() != null && course.getLastDateOfAttendance() == program.getOfficialLastDateOfAttendance()) || (safiTerm.getOlda() != null && course.getLastDateOfAttendance() == safiTerm.getOlda())))
                    {
                        log.debug("GLOBAL ENROLLMENT SCRIPT - WITHDRAWN: courseLDA = {}, courseSchedulingStatus = {}, programOLDA = {}, courseUnitsToAdd = {}", course.getLastDateOfAttendance(), course.getSchedulingStatus(), program.getOfficialLastDateOfAttendance(), course.getUnits());
                    
                        countedUnits = countedUnits + course.getUnits();
                    }
                }
                else if (["SCHEDULED", "PASSED", "FAILED"].contains(course.getSchedulingStatus()))
                {
                    log.debug("GLOBAL ENROLLMENT SCRIPT - SCHEDULED PASSED FAILED: courseSchedulingStatus = {}, courseUnitsToAdd = {}", course.getSchedulingStatus(), course.getUnits());
                
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
            */
    }
    else if (censusDate != null && today > censusDate)
    {   
        log.debug("FIRST IF PASSED");
        def previousFpo;
        if (previousFinancialPlan.effectiveAt(censusDate) != null)
        {
            log.debug("USING CENSUS DATE PREVIOUS FPO");
            previousFpo = previousFinancialPlan.effectiveAt(censusDate);
        }
        else if (financialPlan.getVersion() > 1)
        {
            log.debug("USING FPO VERSION - 1 PREVIOUS FPO");
            previousFpo = previousFinancialPlan.withVersion(financialPlan.getVersion() - 1);
        }
    
        if (previousFpo != null)
        {
			def pellFundStatuses = previousFpo.getFund("PELL").getInAwardYear(awy).getFundStatuses();
			if (!pellFundStatuses.contains("AWARDED") && !pellFundStatuses.contains("ESTIMATED"))
			{
				return enrollmentStatus;
			}        
            log.debug("PREVIOUS FPO IS NOT NULL");
            def previousPell = previousFpo.getFund("PELL").getInAcademicYear(acyNo).getInPaymentPeriod(ppNo);
            if (previousPell.isEligible())
            {
                log.debug("PREVIOUS PELL IS ELIGIBLE");
                if (!previousPell.getEnrollmentStatuses().isEmpty())
                {
                    log.debug("PREVIOUS PELL HAS NON NULL ENROLLMENT STATUS");
                    log.debug("PREVIOUS PELL ENROLLMENT STATUS = {}", previousPell.getEnrollmentStatuses().get());
                    return previousPell.getEnrollmentStatuses().get();
                }
            }
        }
    }

    return enrollmentStatus;
}
return enrollmentStatus;