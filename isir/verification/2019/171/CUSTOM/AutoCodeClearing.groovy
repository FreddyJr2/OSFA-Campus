//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationClearanceRulesScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Baseline Configuration - 2018-2019 V4 - Code Clearing */

/* Document Variables */
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
    
/* Logical Booleans */

    boolean hsPass = false;
    if ((receivedDocuments.hasDoc(docHsDiploma, "Student") && receivedDocuments.get(docHsDiploma, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docHsTranscript, "Student") && receivedDocuments.get(docHsTranscript, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docGedCert, "Student") && receivedDocuments.get(docGedCert, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docGedTranscript, "Student") && receivedDocuments.get(docGedTranscript, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docStateHsEquivalency, "Student") && receivedDocuments.get(docStateHsEquivalency, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docSecondarySchoolCert, "Student") && receivedDocuments.get(docSecondarySchoolCert, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docHomeSchoolCert, "Student") && receivedDocuments.get(docHomeSchoolCert, "Student").isAcceptable()))
    {
        hsPass = true;
    }
    
    boolean identityPass = false;
    if ((receivedDocuments.hasDoc(docDriversLicense, "Student") && receivedDocuments.get(docDriversLicense, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docNonDriversLicense, "Student") && receivedDocuments.get(docNonDriversLicense, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docStateId, "Student") && receivedDocuments.get(docStateId, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docPassport, "Student") && receivedDocuments.get(docPassport, "Student").isAcceptable()))
    {
        identityPass = true;
    }
    
    boolean soepPass = false;
    if ((receivedDocuments.hasDoc(docSoepCampus, "Student") && receivedDocuments.get(docSoepCampus, "Student").isAcceptable()) || (receivedDocuments.hasDoc(docSoepNotary, "Student") && receivedDocuments.get(docSoepNotary, "Student").isAcceptable()))
    {
        soepPass = true;
    }
    
return hsPass && identityPass && soepPass;