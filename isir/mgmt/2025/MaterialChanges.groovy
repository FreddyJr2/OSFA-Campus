//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirUseSubsequentIsirScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

def isirTXID_TRANSACTIONSOURCE = latestIsirRecord.getField(IsirField.TXID_TRANSACTIONSOURCE).getOrNull()

if (isirTXID_TRANSACTIONSOURCE != null) {
    return true
}