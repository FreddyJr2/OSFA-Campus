//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationClearanceRulesScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Baseline Configuration - 2018-2019 170 V1 - Code Clearing */

boolean verificationStatus = false;

/* Document Variables */
    String docVerificationWorksheet = "VW-Dep";
    String docRolloverStatement = "RolloverStatement";
    String docTaxTranscript = "TaxReturnTranscript";
    String doc1040 = "1040";
    String doc1040x = "1040x";
    String docForeignTax = "ForeignTaxTranscript";
    String docW2 = "W2";
    String doc1099 = "1099G";
    String docSelfEmployment = "SelfEmploymentStatement";

/* ISIR Variables */
    String isirPARENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("PARENTSTAXRETURNCOMPLETED");
    String isirPARENTSMARITALSTATUS = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");
    String isirPARENTSIRSREQUESTFLAG = isirRecord.getIsirFieldValue("PARENTSIRSREQUESTFLAG");
    String isirPARENTIRSIRADISTRIBUTIONSFLAG = isirRecord.getIsirFieldValue("PARENTIRSIRADISTRIBUTIONSFLAG");
    String isirPARENTIRSUNTAXEDPENSIONSFLAG = isirRecord.getIsirFieldValue("PARENTIRSUNTAXEDPENSIONSFLAG");
    String isirPARENTSTAXRETURNFILINGSTATUS = isirRecord.getIsirFieldValue("PARENTSTAXRETURNFILINGSTATUS");
    String isirPARENT1DATEOFBIRTH = isirRecord.getIsirFieldValue("PARENT1DATEOFBIRTH");
    String isirPARENT2DATEOFBIRTH = isirRecord.getIsirFieldValue("PARENT2DATEOFBIRTH");

/* Document Booleans */
    boolean hasVwsStudent = false;
    if (receivedDocuments.hasDoc(docVerificationWorksheet, "Student") && receivedDocuments.get(docVerificationWorksheet, "Student").isAcceptable())
    {
        hasVwsStudent = true;
    }

    boolean hasRolloverParent1 = false;
    if (receivedDocuments.hasDoc(docRolloverStatement, "Parent1") && receivedDocuments.get(docRolloverStatement, "Parent1").isAcceptable())
    {
        hasRolloverParent1 = true;
    }

    boolean hasRolloverParent2 = false;
    if (receivedDocuments.hasDoc(docRolloverStatement, "Parent2") && receivedDocuments.get(docRolloverStatement, "Parent2").isAcceptable())
    {
        hasRolloverParent2 = true;
    }

    boolean hasTaxTranscriptParent1 = false;
    if (receivedDocuments.hasDoc(docTaxTranscript, "Parent1") && receivedDocuments.get(docTaxTranscript, "Parent1").isAcceptable())
    {
        hasTaxTranscriptParent1 = true;
    }

    boolean hasTaxTranscriptParent2 = false;
    if (receivedDocuments.hasDoc(docTaxTranscript, "Parent2") && receivedDocuments.get(docTaxTranscript, "Parent2").isAcceptable())
    {
        hasTaxTranscriptParent2 = true;
    }
    
    boolean has1040Parent1 = false;
    if (receivedDocuments.hasDoc(doc1040, "Parent1") && receivedDocuments.get(doc1040, "Parent1").isAcceptable())
    {
        has1040Parent1 = true;
    }

    boolean has1040Parent2 = false;
    if (receivedDocuments.hasDoc(doc1040, "Parent2") && receivedDocuments.get(doc1040, "Parent2").isAcceptable())
    {
        has1040Parent2 = true;
    }

    boolean has1040xParent1 = false;
    if (receivedDocuments.hasDoc(doc1040x, "Parent1") && receivedDocuments.get(doc1040x, "Parent1").isAcceptable())
    {
        has1040xParent1 = true;
    }

    boolean has1040xParent2 = false;
    if (receivedDocuments.hasDoc(doc1040x, "Parent2") && receivedDocuments.get(doc1040x, "Parent2").isAcceptable())
    {
        has1040xParent2 = true;
    }

    boolean hasForeignTaxParent1 = false;
    if (receivedDocuments.hasDoc(docForeignTax, "Parent1") && receivedDocuments.get(docForeignTax, "Parent1").isAcceptable())
    {
        hasForeignTaxParent1 = true;
    }

    boolean hasForeignTaxParent2 = false;
    if (receivedDocuments.hasDoc(docForeignTax, "Parent2") && receivedDocuments.get(docForeignTax, "Parent2").isAcceptable())
    {
        hasForeignTaxParent2 = true;
    }

    boolean hasW2Parent1 = false;
    if (receivedDocuments.hasDoc(docW2, "Parent1") && receivedDocuments.get(docW2, "Parent1").isAcceptable())
    {
        hasW2Parent1 = true;
    }

    boolean hasW2Parent2 = false;
    if (receivedDocuments.hasDoc(docW2, "Parent2") && receivedDocuments.get(docW2, "Parent2").isAcceptable())
    {
        hasW2Parent2 = true;
    }

    boolean has1099Parent1 = false;
    if (receivedDocuments.hasDoc(doc1099, "Parent1") && receivedDocuments.get(doc1099, "Parent1").isAcceptable())
    {
        has1099Parent1 = true;
    }

    boolean has1099Parent2 = false;
    if (receivedDocuments.hasDoc(doc1099, "Parent2") && receivedDocuments.get(doc1099, "Parent2").isAcceptable())
    {
        has1099Parent2 = true;
    }

    boolean hasSelfEmploymentParent1 = false;
    if (receivedDocuments.hasDoc(docSelfEmployment, "Parent1") && receivedDocuments.get(docSelfEmployment, "Parent1").isAcceptable())
    {
        hasSelfEmploymentParent1 = true;
    }

    boolean hasSelfEmploymentParent2 = false;
    if (receivedDocuments.hasDoc(docSelfEmployment, "Parent2") && receivedDocuments.get(docSelfEmployment, "Parent2").isAcceptable())
    {
        hasSelfEmploymentParent2 = true;
    }
    
	boolean hasIrsExtensionParent1 = false;
	if (receivedDocuments.hasDoc("IRSForm4868", "Parent1") && receivedDocuments.get("IRSForm4868", "Parent1").isAcceptable() && ReceivedDocuments.hasDoc("IRSExtensionApproval", "Parent1") && receivedDocuments.get("IRSExtensionApproval", "Parent1").isAcceptable())
	{
		hasIrsExtensionStudent = true;
	}
	
	boolean hasIrsExtensionParent2 = false;
	if (receivedDocuments.hasDoc("IRSForm4868", "Parent2") && receivedDocuments.get("IRSForm4868", "Parent2").isAcceptable() && ReceivedDocuments.hasDoc("IRSExtensionApproval", "Parent2") && receivedDocuments.get("IRSExtensionApproval", "Parent2").isAcceptable())
	{
		hasIrsExtensionStudent = true;
	}
    
/* Calculate Filing Threshold */
    int filingThreshold = 99999;
    
    if (!hasW2Parent1 && !hasW2Parent2)
    {
        filingThreshold = 400;
    }
    else
    {
        boolean parent1Under65 = true;
        boolean parent2Under65 = true;
        Date age65Threshold = groovyScriptHelper.getIsirDate("19511231");
        Date parent1Birthday = groovyScriptHelper.getIsirDate("20170101");
        Date parent2Birthday = groovyScriptHelper.getIsirDate("20170101");
        if (isirPARENT1DATEOFBIRTH != null && !isirPARENT1DATEOFBIRTH.isAllWhitespace())
        {
            parent1Birthday = groovyScriptHelper.getIsirDate(isirPARENT1DATEOFBIRTH);
        }
        if (isirPARENT2DATEOFBIRTH != null && !isirPARENT2DATEOFBIRTH.isAllWhitespace())
        {
            parent2Birthday = groovyScriptHelper.getIsirDate(isirPARENT2DATEOFBIRTH);
        }
        if (parent1Birthday <= age65Threshold)
        {
            parent1Under65 = false;
        }
        if (parent2Birthday <= age65Threshold)
        {
            parent2Under65 = false;
        }
        
        if (isirPARENTSTAXRETURNFILINGSTATUS == "1")
        {
            if (parent1Under65)
            {
                filingThreshold = 10400;
            }
            else
            {
                filingThreshold = 11950;
            }
        }
        if (isirPARENTSTAXRETURNFILINGSTATUS == "4")
        {
            if (parent1Under65)
            {
                filingThreshold = 13400;
            }
            else
            {
                filingThreshold = 14950;
            }
        }
        if (isirPARENTSTAXRETURNFILINGSTATUS == "2")
        {
            if (parent1Under65 && parent2Under65)
            {
                filingThreshold = 20800;
            }
            else if ((parent1Under65 && !parent2Under65) || (!parent1Under65 && parent2Under65))
            {
                filingThreshold = 22050;
            }
            else
            {
                filingThreshold = 23300;
            }
        }
        if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
        {
            filingThreshold = 4050;
        }
        if (isirPARENTSTAXRETURNFILINGSTATUS == "5")
        {
            if (parent1Under65)
            {
                filingThreshold = 16750;
            }
            else
            {
                filingThreshold = 18000;
            }
        }
    }
    
/* Calculate Finances */

    int selfEmploymentParent1 = 0;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent1") && receivedDocuments.get("SelfEmploymentStatement", "Parent1").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("Parent1").isAllWhitespace())
    {
        selfEmploymentParent1 = receivedDocuments.get("SelfEmploymentStatement", "Parent1").getDocumentField("AC1015").toFloat().round();
    }
    
    int selfEmploymentParent2 = 0;
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent2") && receivedDocuments.get("SelfEmploymentStatement", "Parent2").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent2").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("Parent2").isAllWhitespace())
    {
        selfEmploymentParent2 = receivedDocuments.get("SelfEmploymentStatement", "Parent2").getDocumentField("AC1015").toFloat().round();
    }
    
    int w2SumParent1 = 0;
    float w2SumParent1Float = 0.0;
    def w2DocsParent1 = receivedDocuments.getDocumentsForDocCode("W2", "Parent1");
    for (doc in w2DocsParent1)
    {
        String ac1010 = doc.getDocumentField("AC1010");
        float ac1010Value = 0.0;
        if (ac1010 != null && !ac1010.isAllWhitespace())
        {
            ac1010Value = ac1010.toFloat();
        }
        w2SumParent1Float = w2SumParent1Float + ac1010Value;
    }
    w2SumParent1 = w2SumParent1Float.round();
    
    int w2SumParent2 = 0;
    float w2SumParent2Float = 0.0;
    def w2DocsParent2 = receivedDocuments.getDocumentsForDocCode("W2", "Parent2");
    for (doc in w2DocsParent2)
    {
        String ac1010 = doc.getDocumentField("AC1010");
        float ac1010Value = 0.0;
        if (ac1010 != null && !ac1010.isAllWhitespace())
        {
            ac1010Value = ac1010.toFloat();
        }
        w2SumParent2Float = w2SumParent2Float + ac1010Value;
    } 
    w2SumParent2 = w2SumParent2Float.round();
    
    int doc1099SumParent1 = 0;
    float doc1099Parent1Float = 0.0;
    def doc1099GDocsParent1 = receivedDocuments.getDocumentsForDocCode("1099G", "Parent1");
    for (doc in doc1099GDocsParent1)
    {
        String ac1013 = doc.getDocumentField("AC1013");
        float ac1013Value = 0.0;
        if (ac1013 != null && !ac1013.isAllWhitespace())
        {
            ac1013Value = ac1013.toFloat();
        }
        doc1099Parent1Float = doc1099Parent1Float + ac1013Value;
    }
    doc1099SumParent1 = doc1099Parent1Float.round();
    
    int doc1099SumParent2 = 0;
    float doc1099Parent2Float = 0.0;
    def doc1099GDocsParent2 = receivedDocuments.getDocumentsForDocCode("1099G", "Parent2");
    for (doc in doc1099GDocsParent2)
    {
        String ac1013 = doc.getDocumentField("AC1013");
        float ac1013Value = 0.0;
        if (ac1013 != null && !ac1013.isAllWhitespace())
        {
            ac1013Value = ac1013.toFloat();
        }
        doc1099Parent2Float = doc1099Parent2Float + ac1013Value;
    }
    doc1099SumParent2 = doc1099Parent2Float.round();

/* Additional Setup */
    boolean filingStatusTogetherParent1 = false;
    boolean filingStatusTogetherPartialParent1 = false;
    if (hasTaxTranscriptParent1)
    {
        String process = receivedDocuments.get(docTaxTranscript, "Parent1").getDocumentField("AC1065");
        if (process != null && !process.isAllWhitespace())
        {
            if (process == "Married Filing Separately")
            {
                filingStatusTogetherParent1 = true;
                filingStatusTogetherPartialParent1 = true;
            }
        }
    }
    if (has1040Parent1)
    {
        String process = receivedDocuments.get(doc1040, "Parent1").getDocumentField("AC1065");
        if (process != null && !process.isAllWhitespace())
        {
            if (process == "Married Filing Separately")
            {
                filingStatusTogetherParent1 = true;
                filingStatusTogetherPartialParent1 = true;
            }
        }
    }
    if (has1040xParent1)
    {
        String process = receivedDocuments.get(doc1040x, "Parent1").getDocumentField("AC1065");
        if (process != null && !process.isAllWhitespace())
        {
            if (process == "Married Filing Separately")
            {
                filingStatusTogetherParent1 = true;
                filingStatusTogetherPartialParent1 = true;
            }
        }
    }
    if (hasForeignTaxParent1)
    {
        String process = receivedDocuments.get(docForeignTax, "Parent1").getDocumentField("AC1065");
        if (process != null && !process.isAllWhitespace())
        {
            if (process == "Married Filing Separately")
            {
                filingStatusTogetherParent1 = true;
            }
        }
    }

/* Rules */
    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "4") && isirPARENTSIRSREQUESTFLAG == "02" && hasVwsStudent)
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            verificationStatus = true;
            return verificationStatus;
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG == "02" && hasVwsStudent)
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            verificationStatus = true;
            return verificationStatus;
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1 && hasRolloverParent2)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "4") && isirPARENTSIRSREQUESTFLAG != "02" && isirPARENTSIRSREQUESTFLAG != "07" && hasVwsStudent && (hasTaxTranscriptParent1 || has1040Parent1 || has1040xParent1 || hasForeignTaxParent1) && filingStatusTogetherParent1 && (hasTaxTranscriptParent2 || has1040Parent2 || has1040xParent2 || hasForeignTaxParent2))
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            verificationStatus = true;
            return verificationStatus;
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG != "02" && isirPARENTSIRSREQUESTFLAG != "07" && hasVwsStudent && (hasTaxTranscriptParent1 || has1040Parent1 || has1040xParent1 || hasForeignTaxParent1) && (hasTaxTranscriptParent2 || has1040Parent2 || has1040xParent2 || hasForeignTaxParent2))
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            verificationStatus = true;
            return verificationStatus;
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1 && hasRolloverParent2)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "4") && isirPARENTSIRSREQUESTFLAG == "07" && hasVwsStudent && (hasTaxTranscriptParent1 || has1040Parent1) && has1040xParent1 && filingStatusTogetherPartialParent1 && (hasTaxTranscriptParent2 || has1040Parent2 || has1040xParent2))
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            verificationStatus = true;
            return verificationStatus;
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG == "07" && hasVwsStudent && (hasTaxTranscriptParent1 || has1040Parent1) && has1040xParent1 && (hasTaxTranscriptParent2 || has1040Parent2) && has1040xParent2)
    {
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG != "1" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "2" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "3" && isirPARENTIRSIRADISTRIBUTIONSFLAG != "4" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "1" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "2" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "3" && isirPARENTIRSUNTAXEDPENSIONSFLAG != "4")
        {
            verificationStatus = true;
            return verificationStatus;
        }
        if ((isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4") && hasRolloverParent1 && hasRolloverParent2)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED != "1" && isirPARENTSMARITALSTATUS == "1" && isirPARENTSTAXRETURNFILINGSTATUS != "3" && hasVwsStudent && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1))
    {
        if ((selfEmploymentParent1 + w2SumParent1 + doc1099SumParent1) < filingThreshold)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }
    
    if (isirPARENTSTAXRETURNCOMPLETED != "1" && isirPARENTSMARITALSTATUS == "1" && isirPARENTSTAXRETURNFILINGSTATUS == "3" && hasVwsStudent && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1) && (hasW2Parent2 || has1099Parent2 || hasSelfEmploymentParent2))
    {
        if ((selfEmploymentParent1 + selfEmploymentParent2 + w2SumParent1 + w2SumParent2 + doc1099SumParent1 + doc1099SumParent2) < filingThreshold)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "4") && isirPARENTSTAXRETURNFILINGSTATUS == "3" && hasVwsStudent && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1) && (hasW2Parent2 || has1099Parent2 || hasSelfEmploymentParent2))
    {
        if ((selfEmploymentParent1 + selfEmploymentParent2 + w2SumParent1 + w2SumParent2 + doc1099SumParent1 + doc1099SumParent2) < filingThreshold)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }
    
    if (isirPARENTSTAXRETURNCOMPLETED != "1" && isirPARENTSMARITALSTATUS == "4" && isirPARENTSTAXRETURNFILINGSTATUS == "3" && hasVwsStudent && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1))
    {
        if ((selfEmploymentParent1 + w2SumParent1 + doc1099SumParent1) < filingThreshold)
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }
    
    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "4") && isirPARENTSTAXRETURNFILINGSTATUS != "3" && hasVwsStudent && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1))
    {
        if ((selfEmploymentParent1 + w2SumParent1 + doc1099SumParent1) >= filingThreshold && (hasTaxTranscriptParent1 || has1040Parent1 || has1040xParent1 || hasForeignTaxParent1))
        {
            if (!filingStatusTogetherParent1 || (filingStatusTogetherParent1 && (hasTaxTranscriptParent2 || has1040Parent2 || has1040xParent2)))
            {
                verificationStatus = true;
                return verificationStatus;
            }
        }
    }
    
    if (isirPARENTSTAXRETURNCOMPLETED != "1" && isirPARENTSMARITALSTATUS == "1" && isirPARENTSTAXRETURNFILINGSTATUS == "3" && hasVwsStudent && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1) && (hasW2Parent2 || has1099Parent2 || hasSelfEmploymentParent2))
    {
        if ((selfEmploymentParent1 + selfEmploymentParent2 + w2SumParent1 + w2SumParent2 + doc1099SumParent1 + doc1099SumParent2) >= filingThreshold && (hasTaxTranscriptParent1 || has1040Parent1 || has1040xParent1 || hasForeignTaxParent1))
        {
            if (!filingStatusTogetherParent1 || (filingStatusTogetherParent1 && (hasTaxTranscriptParent2 || has1040Parent2 || has1040xParent2)))
            {
                verificationStatus = true;
                return verificationStatus;
            }
        }
    }
    
    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5") && hasVwsStudent && (hasW2Parent1 || has1099Parent1 || hasSelfEmploymentParent1) && (hasW2Parent2 || has1099Parent2 || hasSelfEmploymentParent2))
    {
        if ((selfEmploymentParent1 + selfEmploymentParent2 + w2SumParent1 + w2SumParent2 + doc1099SumParent1 + doc1099SumParent2) >= filingThreshold && (hasTaxTranscriptParent1 || has1040Parent1 || has1040xParent1 || hasForeignTaxParent1) && (hasTaxTranscriptParent2 || has1040Parent2 || has1040xParent2 || hasForeignTaxParent2))
        {
            verificationStatus = true;
            return verificationStatus;
        }
    }
    
 	if (hasIrsExtensionParent1 && !hasTaxTranscriptParent1 && !has1040Parent1 && !has1040xParent1 && !hasForeignTaxParent1)
    {
        verificationStatus = false;
        return verificationStatus;
    }
    
    if (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5")
	{
		if (hasIrsExtensionParent1 && hasIrsExtensionParent2 && !hasTaxTranscriptParent1 && !hasTaxTranscriptParent2 && !has1040Parent1 && !has1040Parent2 && !has1040xParent1 && !has1040xParent2 && !hasForeignTaxParent1 && !hasForeignTaxParent2)
    	{
        	verificationStatus = false;
        	return verificationStatus;
    	}
	}        
    
return verificationStatus;