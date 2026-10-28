//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationDocumentsRequiredScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Baseline Configuration - 2018-2019 170 V5 - Document Request */

/* V1 Document Variables */
    String docVerificationWorksheet = "VW-Dep";
    String docRolloverStatement = "RolloverStatement";
    String docTaxTranscript = "TaxReturnTranscript";
    String doc1040 = "1040";
    String doc1040x = "1040x";
    String docForeignTax = "ForeignTaxTranscript";
    String docW2 = "W2";
    String doc1099 = "1099G";
    String docSelfEmployment = "SelfEmploymentStatement";

/* V4 Document Variables */
    String docHsDiploma = "HSDiploma";
    String docHsTranscript = "HSTranscript";
    String docGedCert = "GEDCert";
    String docGedTranscript = "GEDTranscript";
    String docStateHsEquivalency = "StateHSEquivalencyCert";
    String docSecondarySchoolCert = "SecondarySchoolLeavingCert";
    String docHomeSchoolCert = "HomeSchoolCert";
    String docDriversLicense = "DriversLicense";
    String docNonDriversLicense = "NonDriversLicenseID";
    String docStateId = "StateIssuedID";
    String docPassport = "Passport";
    String docSoepCampus = "SOEP-Campus";
    String docSoepNotary = "SOEP-Notary";

/* ISIR Variables */
    String isirPARENTSTAXRETURNCOMPLETED = isirRecord.getIsirFieldValue("PARENTSTAXRETURNCOMPLETED");
    String isirPARENTSMARITALSTATUS = isirRecord.getIsirFieldValue("PARENTSMARITALSTATUS");
    String isirPARENTSIRSREQUESTFLAG = isirRecord.getIsirFieldValue("PARENTSIRSREQUESTFLAG");
    String isirPARENTIRSIRADISTRIBUTIONSFLAG = isirRecord.getIsirFieldValue("PARENTIRSIRADISTRIBUTIONSFLAG");
    String isirPARENTIRSUNTAXEDPENSIONSFLAG = isirRecord.getIsirFieldValue("PARENTIRSUNTAXEDPENSIONSFLAG");
    String isirPARENTSTAXRETURNFILINGSTATUS = isirRecord.getIsirFieldValue("PARENTSTAXRETURNFILINGSTATUS");

/* V1 Rules */
    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "4") && isirPARENTSIRSREQUESTFLAG == "02")
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
    
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG == "02")
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
    
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
            documentRequest.addDocument(docRolloverStatement, "Parent2");
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "4") && (isirPARENTSIRSREQUESTFLAG != "02" && isirPARENTSIRSREQUESTFLAG != "07"))
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
        documentRequest.addAnyDocuments([docTaxTranscript, "Parent1"],[doc1040, "Parent1"],[doc1040x, "Parent1"],[docForeignTax, "Parent1"]);
    
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5") && (isirPARENTSIRSREQUESTFLAG != "02" && isirPARENTSIRSREQUESTFLAG != "07"))
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
        documentRequest.addAnyDocuments([docTaxTranscript, "Parent1"],[doc1040, "Parent1"],[doc1040x, "Parent1"],[docForeignTax, "Parent1"]);
        documentRequest.addAnyDocuments([docTaxTranscript, "Parent2"],[doc1040, "Parent2"],[doc1040x, "Parent2"],[docForeignTax, "Parent2"]);
    
         if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
            documentRequest.addDocument(docRolloverStatement, "Parent2");
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "1" || isirPARENTSMARITALSTATUS == "4") && isirPARENTSIRSREQUESTFLAG == "07")
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
        documentRequest.addAnyDocuments([docTaxTranscript, "Parent1"],[doc1040, "Parent1"]);
        documentRequest.addDocument(doc1040x, "Parent1");
    
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED == "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5") && isirPARENTSIRSREQUESTFLAG == "07")
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
        documentRequest.addAnyDocuments([docTaxTranscript, "Parent1"],[doc1040, "Parent1"]);
        documentRequest.addDocument(doc1040x, "Parent1");
        documentRequest.addAnyDocuments([docTaxTranscript, "Parent2"],[doc1040, "Parent2"]);
        documentRequest.addDocument(doc1040x, "Parent2");
    
        if (isirPARENTIRSIRADISTRIBUTIONSFLAG == "1" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "2" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "3" || isirPARENTIRSIRADISTRIBUTIONSFLAG == "4" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "1" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "2" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "3" || isirPARENTIRSUNTAXEDPENSIONSFLAG == "4")
        {
            documentRequest.addDocument(docRolloverStatement, "Parent1");
            documentRequest.addDocument(docRolloverStatement, "Parent2");
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED != "1" && isirPARENTSMARITALSTATUS == "1")
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
        documentRequest.addAnyDocuments([docW2, "Parent1"],[doc1099, "Parent1"],[docSelfEmployment, "Parent1"]);
        if (isirPARENTSTAXRETURNFILINGSTATUS == "3")
        {
            documentRequest.addAnyDocuments([docW2, "Parent2"],[doc1099, "Parent2"],[docSelfEmployment, "Parent2"]);
        }
    }

    if (isirPARENTSTAXRETURNCOMPLETED != "1" && (isirPARENTSMARITALSTATUS == "2" || isirPARENTSMARITALSTATUS == "3" || isirPARENTSMARITALSTATUS == "5"))
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
        documentRequest.addAnyDocuments([docW2, "Parent1"],[doc1099, "Parent1"],[docSelfEmployment, "Parent1"]);
        documentRequest.addAnyDocuments([docW2, "Parent2"],[doc1099, "Parent2"],[docSelfEmployment, "Parent2"]);
    }

    if (isirPARENTSTAXRETURNCOMPLETED != "1" && isirPARENTSMARITALSTATUS == "4")
    {
        documentRequest.addDocument(docVerificationWorksheet, "Student");
        documentRequest.addAnyDocuments([docW2, "Parent1"],[doc1099, "Parent1"],[docSelfEmployment, "Parent1"]);
    }
    
/* V4 Rules */

/* Rules - HS Completion */
    documentRequest.addAnyDocuments([docHsDiploma,"Student"],[docHsTranscript,"Student"],[docGedCert,"Student"],[docGedTranscript,"Student"],[docStateHsEquivalency,"Student"],[docSecondarySchoolCert,"Student"],[docHomeSchoolCert,"Student"]);

/* Rules - Identity */
    documentRequest.addAnyDocuments([docDriversLicense,"Student"],[docNonDriversLicense,"Student"],[docStateId,"Student"],[docPassport,"Student"]);

/* Rules - Remainder */
    documentRequest.addAnyDocuments([docSoepCampus,"Student"],[docSoepNotary,"Student"]);