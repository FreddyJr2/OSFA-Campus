//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.LetterCodeCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

return letterCode.querySql('''
SELECT CASE WHEN :processName = 'VALID_ISIR' and to_number(SUBSTR(:awardYear, INSTR(:awardYear, '-')+1)) < 2025 THEN '110'
            WHEN :processName = 'VALID_ISIR' and to_number(SUBSTR(:awardYear, INSTR(:awardYear, '-')+1)) >= 2025 THEN 'VL1'
       END as letter_code
FROM DUAL
''')