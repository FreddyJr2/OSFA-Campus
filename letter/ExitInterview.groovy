//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.LetterCodeCriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

return letterCode.querySql('''
SELECT CASE WHEN :notificationType IN ('TITLE_IV_LOANS_EXIT_COUNSELING_FOR_GRADUATING_STUDENT', 'TEACH_EXIT_COUNSELING_FOR_GRADUATING_STUDENT',  
	                                   'TITLE_IV_LOANS_EXIT_COUNSELING_FOR_WITHDRAWN_STUDENT',  'TEACH_EXIT_COUNSELING_FOR_WITHDRAWN_STUDENT', 'TITLE_IV_LOANS_EXIT_COUNSELING_FOLLOW_UP_FOR_GRADUATING_STUDENT', 
	                                   'TEACH_EXIT_COUNSELING_FOLLOW_UP_FOR_GRADUATING_STUDENT', 'TITLE_IV_LOANS_EXIT_COUNSELING_FOLLOW_UP_FOR_WITHDRAWN_STUDENT', 'TEACH_EXIT_COUNSELING_FOLLOW_UP_FOR_WITHDRAWN_STUDENT'
	                                  )
	        THEN '200' 
       END AS letter_code
FROM DUAL
''')