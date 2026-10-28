//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.LetterCodeCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

return letterCode.querySql('''
SELECT CASE WHEN :sapStatus = 'RG_REGULAR' THEN '610' 
            WHEN :sapStatus = 'FW_FINANCIAL_AID_WARNING' THEN '620' 
            WHEN :sapStatus = 'FD_FINANCIAL_AID_DISQUALIFICATION' THEN '625' 
       END AS letter_code
FROM DUAL
''')