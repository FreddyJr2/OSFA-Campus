//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.LetterCodeCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

return letterCode.querySql('''
SELECT x1.letter_code
FROM
(SELECT DISTINCT
       CASE WHEN :lateDisbursementIndicator = 'true' AND :pwdIndicator = 'false' AND :letterFor = 'student' AND :fundCode <> 'PLUS' 
            THEN '325'
            WHEN :lateDisbursementIndicator = 'false' AND :pwdIndicator = 'true' AND :letterFor = 'student' AND :fundCode <> 'PLUS'
                 AND a.anticipated_date <= b.official_last_date_attendance
            THEN '725'
       END    as letter_code 
FROM FASRPT.DISBURSEMENT_ONLY_VW  a
     JOIN FASRPT.program_vw b ON a.enrollment_id = b.enrollment_id and b.safi_rank = 1 and b.primary_program_indicator = 'True'
WHERE a.enrollment_id = :enrollmentId
)  x1
WHERE x1.letter_code IS NOT NULL
''')