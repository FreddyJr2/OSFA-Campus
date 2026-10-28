//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

//Variable definitions
def isirSTUDENT_HIGHSCHOOLCOMPLETIONSTATUS = isirRecord.getField(IsirField.STUDENT_HIGHSCHOOLCOMPLETIONSTATUS).getOrNull()

if (receivedDocuments.hasDoc("HSDiploma","Student") && receivedDocuments.get("HSDiploma","Student").isAcceptable())
{
    if (isirSTUDENT_HIGHSCHOOLCOMPLETIONSTATUS != "1")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.correctedValue = "1"
        return
    }
}


if (receivedDocuments.hasDoc("HSTranscript","Student") && receivedDocuments.get("HSTranscript","Student").isAcceptable())
{
    if (isirSTUDENT_HIGHSCHOOLCOMPLETIONSTATUS != "1")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.correctedValue = "1"
        return
    }
}

if (receivedDocuments.hasDoc("GEDCert","Student") && receivedDocuments.get("GEDCert","Student").isAcceptable())
{
    if (isirSTUDENT_HIGHSCHOOLCOMPLETIONSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.correctedValue = "2"
        return
    }
}

if (receivedDocuments.hasDoc("GEDTranscript","Student") && receivedDocuments.get("GEDTranscript","Student").isAcceptable())
{
    if (isirSTUDENT_HIGHSCHOOLCOMPLETIONSTATUS != "2")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.correctedValue = "2"
        return
    }
}

if (receivedDocuments.hasDoc("HomeSchoolCert","Student") && receivedDocuments.get("HomeSchoolCert","Student").isAcceptable())
{
    if (isirSTUDENT_HIGHSCHOOLCOMPLETIONSTATUS != "3")
    {
        discrepancyEvaluationResult.hasDiscrepancy = true
        discrepancyEvaluationResult.correctedValue = "3"
        return
    }
}