//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.TermPaymentPeriodMonthsScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Term Baseline PP Months Calculation, return type BigDecimal */

import org.joda.time.LocalDate;

def returnValue = 1.0;

def programTermType = program.getTermType();

log.debug("PP MONTHS CALCULATION CONFIG: Program Term Type = {}", program.getTermType());

def safiTerm = program.getTerms().getOverlappingWith(term).get(0);

if (program.getTermType() == null)
{
	log.debug("PP MONTHS CALCULATION CONFIG: ERROR, null value passed from API");
	returnValue = 1.0;
}
else if (safiTerm.isSummer()) 
{
    returnValue = 2.0;
}
// Standard from this point on
else if (programTermType == "Semester")
{
    returnValue = 4.5;
}
else if (programTermType == "Trimester")
{
    returnValue = 3.0;
}
else if (programTermType == "Quarter")
{
    returnValue = 3.0;
}

return returnValue;