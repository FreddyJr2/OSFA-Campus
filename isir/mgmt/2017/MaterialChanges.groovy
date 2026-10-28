//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirUseSubsequentIsirScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String latestIsirTransactionDataSourceCode = latestIsirRecord.getIsirFieldValue("TRANSACTIONDATASOURCETYPECODE");
if (!latestIsirTransactionDataSourceCode.isAllWhitespace())
{
    return true;
}
