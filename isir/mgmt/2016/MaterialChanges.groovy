//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirUseSubsequentIsirScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

import java.util.regex.Pattern;
List isirFieldsToCompare = ["STUDENTSNUMBEROFFAMILYMEMBERS", "STUDENTSNUMBERINCOLLEGE", "STUDENTSUSINCOMETAXPAID",
      "STUDENTSOTHERUNTAXEDINCOME", "STUDENTSINCOMEEARNEDFROMWORK", "STUDENTSCHILDSUPPORTRECEIVED", "STUDENTSCHILDSUPPORTPAID",
      "STUDENTTANFBENEFITS", "STUDENTWICBENEFITS", "STUDENTFOODSTAMPBENEFITS", "PARENTSNUMBEROFFAMILYMEMBERS",
      "PARENTSNUMBERINCOLLEGE", "PARENTSADJUSTEDGROSSINCOMEFROMIRSFORM", "PARENTSUSINCOMETAXPAID", "PARENTSOTHERUNTAXEDINCOME",
       "FATHERSSTEPFATHERSINCOMEEARNEDFROMWORK", "MOTHERSSTEPMOTHERSINCOMEEARNEDFROMWORK", "PARENTSCHILDSUPPORTRECEIVED",
       "PARENTSCHILDSUPPORTPAID", "PARENTTANFBENEFITS", "PARENTWICBENEFITS", "PARENTFOODSTAMPBENEFITS",
       "STUDENTSUPPLEMENTALSECURITYINCOMEBENEFITS", "STUDENTFREEORREDUCEDPRICESCHOOLLUNCHBENEFITS",
       "PARENTFREEORREDUCEDPRICESCHOOLLUNCHBENEFITS", "PARENTSUPPLEMENTALSECURITYINCOMEBENEFITS",
       "SPOUSESINCOMEEARNEDFROMWORK","STUDENTSADJUSTEDGROSSINCOMEFROMIRSFORM"];
String latestIsirTransactionReceiptDateValue = latestIsirRecord.getIsirFieldValue("TRANSACTIONRECEIPTDATE");
Date latestIsirTransactionReceiptDate = groovyScriptHelper.getIsirDate(latestIsirTransactionReceiptDateValue);
String latestIsirTransactionDataSourceCode = latestIsirRecord.getIsirFieldValue("TRANSACTIONDATASOURCETYPECODE");
String latestIsirVerification = latestIsirRecord.getIsirFieldValue("STUDENTISSELECTEDFORVER");
String previousIsirVerification = previousIsirRecord.getIsirFieldValue("STUDENTISSELECTEDFORVER");
String latestISIR_SARC = latestIsirRecord.getIsirFieldValue("SARCFLAG");
String previousISIR_SARC = previousIsirRecord.getIsirFieldValue("SARCFLAG");
String latestISIR_EFC = latestIsirRecord.getIsirFieldValue("PRIMARYEFC");
String previousISIR_EFC = previousIsirRecord.getIsirFieldValue("PRIMARYEFC");
String latestISIR_DependencyStatus = latestIsirRecord.getIsirFieldValue("DEPENDENCYSTATUS");
String previousISIR_DependencyStatus = previousIsirRecord.getIsirFieldValue("DEPENDENCYSTATUS");
String latestISIR_NSLDSPostscreeningReasonCode1 = latestIsirRecord.getIsirFieldValue("NSLDSPOSTSCREENINGREASONCODE1");
String latestISIR_NSLDSPostscreeningReasonCode2 = latestIsirRecord.getIsirFieldValue("NSLDSPOSTSCREENINGREASONCODE2");
String latestISIR_NSLDSPostscreeningReasonCode3 = latestIsirRecord.getIsirFieldValue("NSLDSPOSTSCREENINGREASONCODE3");
Pattern nsldsCodeListRegEx = ~/01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|20|21|22|23|99/;def withdrawnAfterIsirTransactionDate = (student.getEnrollmentStatus() == "W" && student.getDeterminationDate() != null &&
                latestIsirTransactionReceiptDate <= student.getDeterminationDate());
def graduatedAfterIsirTransactionDate = (student.getEnrollmentStatus() == "G" && student.getCompletionDate() != null &&
                latestIsirTransactionReceiptDate <= student.getCompletionDate());
def notGraduatedAndNotWithdrawn = (student.getEnrollmentStatus() != null && student.getEnrollmentStatus() != "G") &&
         (student.getEnrollmentStatus() != "W");
def latestIsirTransactionDataSourceCodeStartsWith1_2_3_4_or_6 = ( latestIsirTransactionDataSourceCode ==~ /[1-4,6][A-Z]/ );
def verificationChanged = ((previousIsirVerification == "N" || previousIsirVerification == "*") && latestIsirVerification == "Y");
def sarcChanged =  (previousISIR_SARC != latestISIR_SARC);
def efcChanged = (previousISIR_EFC != latestISIR_EFC);
def noPreviousOrCurrentVerification = ((previousIsirVerification == "N" || previousIsirVerification == "*") &&
         (latestIsirVerification == "N" || latestIsirVerification == "*"));
def efcChangedAndNoPrevOrCurVer = (efcChanged && noPreviousOrCurrentVerification);
def prevAndCurVer = (previousIsirVerification == "Y" && latestIsirVerification == "Y");
def isirFieldsChanged = groovyScriptHelper.isirRecordsDifferForListedFields(previousIsirRecord, latestIsirRecord, isirFieldsToCompare);
def prevAndCurVerAndIsirFieldsChanged = (prevAndCurVer && isirFieldsChanged);
def efcChangedAndNoPrevOrCurVer_Or_PrevAndCurVerAndIsirFieldsChanged =
   (efcChangedAndNoPrevOrCurVer || prevAndCurVerAndIsirFieldsChanged);
def noPrevEFC = (previousISIR_EFC == null ||previousISIR_EFC.isAllWhitespace());
def haveCurEFC = !(latestISIR_EFC == null || latestISIR_EFC.isAllWhitespace());
def prevIsirRejectedCurIsirOfficial = (noPrevEFC && haveCurEFC);
def dependencyStatusChanged = ((previousISIR_DependencyStatus == "X" || previousISIR_DependencyStatus == "Y")  &&
  (latestISIR_DependencyStatus == "D" || latestISIR_DependencyStatus == "I"));
def latestIsirTransactionDataSourceCodeStartsWith5 = ( latestIsirTransactionDataSourceCode ==~ /5[A-Z]/ );
def NSLDSPostscreeningReasonCode1InCodeList = ( latestISIR_NSLDSPostscreeningReasonCode1 ==~ nsldsCodeListRegEx );
def NSLDSPostscreeningReasonCode2InCodeList = ( latestISIR_NSLDSPostscreeningReasonCode2 ==~ nsldsCodeListRegEx );
def NSLDSPostscreeningReasonCode3InCodeList = ( latestISIR_NSLDSPostscreeningReasonCode3 ==~ nsldsCodeListRegEx );
def NSLDSPostscreeningReasonCode1_2_or_3InCodeList = (NSLDSPostscreeningReasonCode1InCodeList || NSLDSPostscreeningReasonCode2InCodeList ||
      NSLDSPostscreeningReasonCode3InCodeList);
boolean haveMaterialChanges = false;

if (latestIsirTransactionDataSourceCode == "1C") {
  haveMaterialChanges = true;
} else {
      if ( withdrawnAfterIsirTransactionDate ||
           graduatedAfterIsirTransactionDate ||
           notGraduatedAndNotWithdrawn) {
              haveMaterialChanges = true;
      } else if ( latestIsirTransactionDataSourceCodeStartsWith1_2_3_4_or_6 ) {
          if (verificationChanged ||
              sarcChanged ||
              efcChangedAndNoPrevOrCurVer_Or_PrevAndCurVerAndIsirFieldsChanged ||
              dependencyStatusChanged ||
              prevIsirRejectedCurIsirOfficial
             ) {
              haveMaterialChanges = true;
          }
      } else if (latestIsirTransactionDataSourceCodeStartsWith5 &&
                 NSLDSPostscreeningReasonCode1_2_or_3InCodeList) {
              haveMaterialChanges = true;
      }
}
return haveMaterialChanges;