//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.NonTermAcyMonthsScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Non-Term Baseline ACY Months Calculation, return type int */

if (acy.getUnits() == null || program.getAydUnits() == null)
{
    log.debug("ACY MONTHS CALCULATION CONFIG: ERROR, null values passed from APIs");
    return 0;
}

log.debug("ACY MONTHS CALCULATION CONFIG: Academic Year Schedule Units = {}, Program AYD Units = {}", acy.getUnits(), program.getAydUnits());

if ((acy.getUnits() / program.getAydUnits()) >= 1)
{
    return 9;
}
else
{
    int returnValue = Math.round(((acy.getUnits()/program.getAydUnits()) * 9));
    return returnValue;
}