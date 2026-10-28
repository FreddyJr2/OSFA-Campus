//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.IsirVerificationDocumentsRequiredScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

/* Baseline Configuration - 2019-2020 V4 - Document Request */

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

/* Rules - HS Completion */
    documentRequest.addAnyDocuments([docHsDiploma,"Student"],[docHsTranscript,"Student"],[docGedCert,"Student"],[docGedTranscript,"Student"],[docStateHsEquivalency,"Student"],[docSecondarySchoolCert,"Student"],[docHomeSchoolCert,"Student"]);

/* Rules - Identity */
    documentRequest.addAnyDocuments([docDriversLicense,"Student"],[docNonDriversLicense,"Student"],[docStateId,"Student"],[docPassport,"Student"]);

/* Rules - Remainder */
    documentRequest.addAnyDocuments([docSoepCampus,"Student"],[docSoepNotary,"Student"]);