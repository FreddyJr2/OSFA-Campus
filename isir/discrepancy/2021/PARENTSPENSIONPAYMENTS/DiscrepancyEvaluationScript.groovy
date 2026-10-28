//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirDiscrepancyEvaluationScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

String isirParentsPensionPayments = isirRecord.getIsirFieldValue("PARENTSPENSIONPAYMENTS");

int isirParentsPensionPaymentsValue = 0;
boolean isirParentsPensionPaymentsBlank = true;
if (!isirParentsPensionPayments.isAllWhitespace())
{
    isirParentsPensionPaymentsValue = isirParentsPensionPayments.toInteger();
    isirParentsPensionPaymentsBlank = false;
}

float totalBox12Amount = 0.0;

boolean hasW2Parent1 = false;
if (receivedDocuments.hasDoc("W2", "Parent1") && receivedDocuments.get("W2", "Parent1").isAcceptable())
{
    hasW2Parent1 = true;
    def w2DocsParent1 = receivedDocuments.getDocumentsForDocCode("W2", "Parent1");
    for (doc in w2DocsParent1)
    {
        String ac1085 = "";
        String ac1086 = "";
        String ac1087 = "";
        String ac1088 = "";
        String ac1089 = "0.0";
        String ac1090 = "0.0";
        String ac1091 = "0.0";
        String ac1092 = "0.0";
        
        if (doc.getDocumentField("AC1085") != null)
        {
            ac1085 = doc.getDocumentField("AC1085");
        }
        if (doc.getDocumentField("AC1086") != null)
        {
            ac1086 = doc.getDocumentField("AC1086");
        }
        if (doc.getDocumentField("AC1087") != null)
        {
            ac1087 = doc.getDocumentField("AC1087");
        }
        if (doc.getDocumentField("AC1088") != null)
        {
            ac1088 = doc.getDocumentField("AC1088");
        }
        if (doc.getDocumentField("AC1089") != null)
        {
            ac1089 = doc.getDocumentField("AC1089");
        }
        if (doc.getDocumentField("AC1090") != null)
        {
            ac1090 = doc.getDocumentField("AC1090");
        }
        if (doc.getDocumentField("AC1091") != null)
        {
            ac1091 = doc.getDocumentField("AC1091");
        }
        if (doc.getDocumentField("AC1092") != null)
        {
            ac1092 = doc.getDocumentField("AC1092");
        }
        
        if (ac1085 == "D" || ac1085 == "E" || ac1085 == "F" || ac1085 == "G" || ac1085 == "H" || ac1085 == "S")
        {
            totalBox12Amount = totalBox12Amount + ac1089.toFloat();
        }
        if (ac1086 == "D" || ac1086 == "E" || ac1086 == "F" || ac1086 == "G" || ac1086 == "H" || ac1086 == "S")
        {
            totalBox12Amount = totalBox12Amount + ac1090.toFloat();
        }
        if (ac1087 == "D" || ac1087 == "E" || ac1087 == "F" || ac1087 == "G" || ac1087 == "H" || ac1087 == "S")
        {
            totalBox12Amount = totalBox12Amount + ac1091.toFloat();
        }
        if (ac1088 == "D" || ac1088 == "E" || ac1088 == "F" || ac1088 == "G" || ac1088 == "H" || ac1088 == "S")
        {
            totalBox12Amount = totalBox12Amount + ac1092.toFloat();
        }
    }
}

boolean hasW2Parent2 = false;
if (receivedDocuments.hasDoc("W2", "Parent2") && receivedDocuments.get("W2", "Parent2").isAcceptable())
{
    hasW2Parent2 = true;
    def w2DocsParent2 = receivedDocuments.getDocumentsForDocCode("W2", "Parent2");
    for (doc in w2DocsParent2)
    {
        String ac1085 = "";
        String ac1086 = "";
        String ac1087 = "";
        String ac1088 = "";
        String ac1089 = "0.0";
        String ac1090 = "0.0";
        String ac1091 = "0.0";
        String ac1092 = "0.0";
        
        if (doc.getDocumentField("AC1085") != null)
        {
            ac1085 = doc.getDocumentField("AC1085");
        }
        if (doc.getDocumentField("AC1086") != null)
        {
            ac1086 = doc.getDocumentField("AC1086");
        }
        if (doc.getDocumentField("AC1087") != null)
        {
            ac1087 = doc.getDocumentField("AC1087");
        }
        if (doc.getDocumentField("AC1088") != null)
        {
            ac1088 = doc.getDocumentField("AC1088");
        }
        if (doc.getDocumentField("AC1089") != null)
        {
            ac1089 = doc.getDocumentField("AC1089");
        }
        if (doc.getDocumentField("AC1090") != null)
        {
            ac1090 = doc.getDocumentField("AC1090");
        }
        if (doc.getDocumentField("AC1091") != null)
        {
            ac1091 = doc.getDocumentField("AC1091");
        }
        if (doc.getDocumentField("AC1092") != null)
        {
            ac1092 = doc.getDocumentField("AC1092");
        }
        
        if (ac1085 == "D" || ac1085 == "E" || ac1085 == "F" || ac1085 == "G" || ac1085 == "H" || ac1085 == "S")
        {
            totalBox12Amount = totalBox12Amount + ac1089.toFloat();
        }
        if (ac1086 == "D" || ac1086 == "E" || ac1086 == "F" || ac1086 == "G" || ac1086 == "H" || ac1086 == "S")
        {
            totalBox12Amount = totalBox12Amount + ac1090.toFloat();
        }
        if (ac1087 == "D" || ac1087 == "E" || ac1087 == "F" || ac1087 == "G" || ac1087 == "H" || ac1087 == "S")
        {
            totalBox12Amount = totalBox12Amount + ac1091.toFloat();
        }
        if (ac1088 == "D" || ac1088 == "E" || ac1088 == "F" || ac1088 == "G" || ac1088 == "H" || ac1088 == "S")
        {
            totalBox12Amount = totalBox12Amount + ac1092.toFloat();
        }
    }
}

if ((hasW2Parent1 || hasW2Parent2) && (totalBox12Amount.round() != isirParentsPensionPaymentsValue))
{
    discrepancyEvaluationResult.hasDiscrepancy = true;
    discrepancyEvaluationResult.correctedValue = String.format("%07d",totalBox12Amount.round());
    return;
}