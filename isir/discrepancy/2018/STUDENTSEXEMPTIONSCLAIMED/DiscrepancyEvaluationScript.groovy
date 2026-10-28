//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirValue = isirRecord.getIsirFieldValue("STUDENTSEXEMPTIONSCLAIMED");
String isirStudentMaritalStatus = isirRecord.getIsirFieldValue("STUDENTMARITALSTATUS");

int isirValueInt = 0;
if (isirValue != null && !isirValue.isAllWhitespace())
{
    isirValueInt = isirValue.toInteger();
}

if (isirStudentMaritalStatus == "1" && isirValue != "01")
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "01";
    return;
}

if (isirStudentMaritalStatus == "2" && isirValueInt < 2)
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "02";
    return;
}

if (isirStudentMaritalStatus == "4" && isirValueInt < 2)
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = "02";
    return;
}