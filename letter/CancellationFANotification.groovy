//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.LetterCodeCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

return letterCode.querySql('''
SELECT x1.letter_code
FROM
(SELECT DISTINCT CASE WHEN a.pwd_indicator <> 'True' THEN '520' END letter_code
FROM FASRPT.DISBURSEMENT_ONLY_VW  a
     JOIN FASRPT.STUDENT_FA_VW  b ON a.enrollment_id = b.enrollment_id
WHERE a.disbursement_status  = 'DISBURSEMENT_CANCELED'
AND   a.academic_year_number = :academicYearNumber
AND   a.disbursement_number  = :disbursmentNumber
AND   b.school_student_id    = :externalStudentId
)  x1
''')