//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirValue = isirRecord.getIsirFieldValue("PERMANENTSTATE");
            String sdiState = fasStudent.addressState;

            if (isirValue == null)
            {
                isirValue = "";
            }

            if (sdiState == null)
            {
                sdiState = "";
            }

            if (isirValue == "FC" && sdiState != isirValue)
            {
                discrepancyEvaluationResult.hasDiscrepancy = true;
                discrepancyEvaluationResult.needManualReview = true;
                return;
            }