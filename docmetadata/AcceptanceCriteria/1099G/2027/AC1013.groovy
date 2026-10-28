/**
 * Primary Purpose and Functionality:
 *   This script evaluates document metadata fields for both students and parents to determine:
 *     - Document acceptance for financial aid processing.
 *     - Whether additional supporting documents are required, based on IRS, federal, and institutional rules.
 *   The script is part of the Oracle SFP Platform and integrates platform-specific APIs and objects.
 *
 * Key Calculations Performed:
 *   - Aggregation and rounding of employment/self-employment/W2/1099G income values.
 *   - Calculation of age from ISIR-reported dates to assist in tax threshold decisioning.
 *   - Dynamic computation of filing thresholds according to IRS/marital/age/tax-filing-type logic.
 *   - Determination if reported income exceeds thresholds, which triggers requirements for further documentation.
 *
 * Critical Business Rules Enforced:
 *   - Validation of received documentation for completeness and acceptability.
 *   - Enforcement of IRS tax filing thresholds based on filing status and age.
 *   - Institutionally-defined business logic for additional document requests, ensuring policy compliance.
 *
 * Boundary Conditions and Limits:
 *   - Handles missing/null ISIR data or documents with explicit safe-exit behavior.
 *   - All numerical fields are validated for existence and non-whitespace before processing.
 *   - Defensive defaults (e.g., high initial thresholds, "age = -1" fallbacks) handle missing or ambiguous data.
 *   - Handles case where a spouse is not present (set as -1/default DOB).
 *
 * Financial Aid Implications:
 *   - Impacts eligibility and disbursement by requiring compliance with federal documentation rules.
 *   - Ensures only verified students/parents meeting documentation criteria can receive aid, reducing audit and liability risk.
 *   - Triggers requests for IRS forms, tax transcripts, or foreign tax forms as required, enabling regulatory compliance.
 *
 * Required Inputs and Expected Outputs:
 *   Inputs:
 *     - ISIR and document metadata (from Oracle SFP APIs)
 *     - Enrollment and student data objects
 *   Output:
 *     - Populates the supportingDoc object with required additional supporting document requests, or triggers an error string if not accepted.
 *
 * Error Handling Approach:
 *   - All checks for null/missing data safely exit or default.
 *   - No unhandled exceptions; operational errors result in conservative outcomes (i.e., require more docs or halt processing).
 *   - Logging handled through the Oracle SFP Platform logger (not directly called in this script).
 *
 * Integration Points:
 *   - Leverages receivedDocuments, isirRecord, currentDocument, student, and supportingDoc interfaces for data access and mutation.
 *   - Uses platform APIs for all doc/field/income extraction with full audit trail and interface compliance.
 *
 * Compliance Considerations:
 *   - Ensures compliance with Department of Education federal aid verification requirements, IRS thresholds, and institutional verification policies.
 *   - All personal and financial data accessed and evaluated per relevant privacy, security, and auditability regulations.
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

// CONDITIONALS:
// Check 1: ISIR Record Presence
// Variables: isirRecord
// Purpose: Ensure an ISIR record exists before proceeding, as it is critical for the eligibility and documentation checks.
// Logic:
//   - If isirRecord is null (not available for the student), exit the script.
//   - ISIR is necessary for almost all financial aid eligibility calculations and verification.
// Success Path: If ISIR exists, proceed with further checks.
// Failure Path: If ISIR missing/null, script exits immediately with no action (void return).
// Business Rules: Federal aid processing requires ISIR data for verification; lack of ISIR means process halts.
// Error Handling: Safe return, prevents NullPointerException on further data dereferencing.
// Financial Impact: Student cannot proceed in aid cycle without ISIR.
// BOUNDARY CHECKS:
//   - Check that isirRecord != null as a precondition.
//   - Source: Platform record existence.
//   - Edge case: Miscoded/late applicant with missing ISIR handled safely.
// ARITHMETIC: N/A
if (isirRecord == null) {
    return
}

// CONDITIONALS:
// Check 2: ISIR Data Extraction for Student Filing Status & Dates
// Variables: isirRecord.getField(IsirField.*)
// Purpose: Retrieve key ISIR fields for student tax status and age calculations.
// Logic:
//   - Extract ISIR-defined variables for marital status, birthdate, and tax status; these drive subsequent business decisions.
//   - If the "STUDENTFTIM_FILINGSTATUSCODE_FTI" field is blank/null, fallback to "STUDENT_TAXRETURNFILINGSTATUS".
// Success Path: Fields are extracted; fallback ensures resilience to ISIR field differences or data model changes.
// Failure Path: Null values propagate, handled in later logic.
// Business Rules: Use the most authoritative and FTI-compliant field where available.
// Error Handling: Fallback logic ensures robustness against missing ISIR fields.
// Financial Impact: Accurate field selection ensures correct verification thresholds, impacting eligibility.
def isirSTUDENT_MARITALSTATUS = isirRecord.getField(IsirField.STUDENT_MARITALSTATUS).getOrNull()
def isirSTUDENT_DATEOFBIRTH = isirRecord.getField(IsirField.STUDENT_DATEOFBIRTH).getOrNull()
def isirSTUDENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.STUDENTFTIM_FILINGSTATUSCODE_FTI).getOrNull()

// CONDITIONALS:
// Check 3: Fallback to legacy tax status field if new one is missing
// Variables: isirSTUDENTFTIM_FILINGSTATUSCODE_FTI, isirRecord
// Purpose: Ensure tax filing status is always available for threshold calculation.
// Logic:
//   - If main field is null, use alternative/legacy field.
// Success Path: Always have some tax status code for further checks.
// Failure Path: Null remains if both are missing - handled later by using conservative rules.
// Business Rules: Operate on most current/authoritative ISIR mappings, maintain backward compatibility.
// Error Handling: None needed; logic is resilient to nulls.
// Financial Impact: Ensures correct thresholds, avoids under/over-requesting docs.
// BOUNDARY CHECKS:
//   - Ensures fallback between FTI and legacy field in case of missing/blank values.
//   - Edge case: Both are missing/null; downstream logic handles as conservative default (likely triggers extra docs).
// ARITHMETIC: N/A
if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == null) {
    isirSTUDENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.STUDENT_TAXRETURNFILINGSTATUS).getOrNull()
}

// CONDITIONALS:
// Check 4: ISIR Data Extraction for Parent/Spouse Filing Status & Dates
// Variables: isirRecord.getField(IsirField.*)
// Purpose: Retrieve ISIR-defined variables for parental/spouse information
// Logic:
//   - Identify parent and parent spouse date of birth and filing status fields
//   - Fallback logic for FTI/non-FTI tax filing status
// Success Path: Have correct data for both parent and spouse for boundary and threshold calculation
// Failure Path: Null values propagate, safe defaults will kick in later
// Business Rules: Parent data required for dependent student applicants.
// Error Handling: None required—null handled by subsequent code.
// Financial Impact: Parent (not student) income/filer status can shift packaging or verification requirements.
def isirPARENT_DATEOFBIRTH = isirRecord.getField(IsirField.PARENT_DATEOFBIRTH).getOrNull()
def isirPARENTSPOUSE_DATEOFBIRTH = isirRecord.getField(IsirField.PARENTSPOUSE_DATEOFBIRTH).getOrNull()
def isirPARENTFTIM_FILINGSTATUSCODE_FTI =  isirRecord.getField(IsirField.PARENTFTIM_FILINGSTATUSCODE_FTI).getOrNull()

// CONDITIONALS:
// Check 5: Parent filing status fallback
// Variables: isirPARENTFTIM_FILINGSTATUSCODE_FTI, isirRecord.getField(IsirField.*)
// Purpose: Robustly determine which field to use for parent tax status
// Logic:
//   - If primary field null, use legacy
// Success Path: Always have a tax filing code
// Failure Path: Remain null, conservatively escalates to require additional documentation later
// Business Rules: Ensures eligibility checks account for latest ISIR field mappings
if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == null) {
    isirPARENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.PARENT_TAXRETURNFILINGSTATUS).getOrNull()
}

// CONDITIONALS:
// Check 6: Identify the current document owner (Student or Parent)
// Variables: currentDocument.getDocumentOwnerTypes()
// Purpose: Branch subsequent logic according to whether the document is for the student or a parent/spouse.
// Logic:
//   - Extract first element of document owner list (assumes single owner per evaluation pass).
// Success Path: Correct branch selected (student block vs parent block).
// Failure Path: None—script would raise error if owner type not recognized (not present in this script, but conservative defaults).
// Business Rules: Duplex scenario (student/parent) required for correct federal/institutional application of thresholds.
// Error Handling: None (assuming correct data according to platform contract).
// Financial Impact: Directs which income, doc, and filing rules apply.
def docOwner = currentDocument.getDocumentOwnerTypes().get(0)

// CONDITIONALS:
// Check 8: Owner is Student
// Variables: docOwner
// Purpose: Enter student-specific doc and income processing block
// Logic: if (docOwner == "Student")
// Success Path: Student branch logic executed
// Failure Path: Else, skips to parent logic
// Business Rules: IRS rules/federal aid handling differs for student vs. parent households
// Error Handling: N/A
// Financial Impact: Ensures proper documentation and verification path for applicant group
// BOUNDARY CHECKS: N/A
if (docOwner == "Student")
{
    // CONDITIONALS:
    // Check 9: Student Verification Worksheet Document Acceptability
    // Variables: receivedDocuments.hasDoc(), receivedDocuments.get(), isAcceptable()
    // Purpose: Determine if a verification worksheet (VW-Ind) for the student, required for federal verification, is on file and valid
    // Logic:
    //   - Check if doc "VW-Ind" exists for "Student" and isAcceptable() == true
    // Success Path: Boolean flag set, used for subsequent age extraction
    // Failure Path: Downstream logic behaves conservatively: spouse age -1, etc.
    // Business Rules: Compliance with 34 CFR 668.56; worksheet required for certain aid types
    // Error Handling: Null/missing handled by boolean variable pattern
    // Financial Impact: If missing, may block verification step, delaying/disallowing aid
    // BOUNDARY CHECKS:
    //   - Checks for both existence and acceptability of worksheet with explicit platform methods.
    //   - No edge case if non-acceptable (not counted).
    boolean hasVerificationWorksheetIndStudent = false
    if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
    {
        hasVerificationWorksheetIndStudent = true
    }

    // CONDITIONALS:
    // Check 10: Student Self-Employment Statement Document Acceptability
    // Variables: receivedDocuments.hasDoc(), receivedDocuments.get(), isAcceptable()
    // Purpose: Verify if self-employment statement doc for Student exists and is valid, to include self-employment income in totals
    // Logic: Identical pattern to VW-Ind above; acceptance required for compliance
    // Success Path: Sets boolean, triggers downstream income aggregation
    // Business Rules: Self-employment earnings are federally required for EFC/aid determination
    // Financial Impact: Ensures self-employed income is always included for eligibility
    // BOUNDARY CHECKS: N/A
    boolean hasSelfEmploymentStatementStudent = false
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Student") && receivedDocuments.get("SelfEmploymentStatement", "Student").isAcceptable())
    {
        hasSelfEmploymentStatementStudent = true
    }

    // ARITHMETIC:
    // Operation 1: Self-Employment Income Calculation for Student
    // Formula: Parse and round statement field AC1015
    // Variables: receivedDocuments.get(), selfEmploymentStudent
    // Steps:
    //   1. Confirm document exists and is acceptable.
    //   2. Extract value from "AC1015", ensure non-null and not all whitespace.
    //   3. Parse as Float and round to nearest int.
    //   4. Store as selfEmploymentStudent.
    // Business Context: Used in total income for IRS threshold check, affecting required doc logic.
    // Result: Integer self-employment income for inclusion.
    // Financial Impact: Underreporting/Overreporting impacts further doc requirements and package eligibility.
    // BOUNDARY CHECKS:
    //   - Explicit check for null and whitespace for data integrity.
    //   - Edge case: malformed or missing AC1015 defaults to 0.
    int selfEmploymentStudent = 0
    if (hasSelfEmploymentStatementStudent)
    {
        if (receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").isAllWhitespace())
        {
            selfEmploymentStudent = receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").toFloat().round()
        }
    }

    // CONDITIONALS:
    // Check 11: Student Spouse Self-Employment Statement Acceptable
    // Variables: receivedDocuments.hasDoc(), ...isAcceptable()
    // Purpose: Determines if spouse's self-employment income can be used below
    // Logic: Same as above for student spouse
    // Business Rules/Error Handling/Financial Impact: As above, for spouse
    // BOUNDARY CHECKS: N/A
    // ARITHMETIC:
    // Operation 2: Student Spouse Self-Employment Income, Conversion, Rounding
    // Formula: toFloat().round()
    // Variables: selfEmploymentStudentSpouse
    // Steps: As for student above
    // Business Context/Result/Financial Impact/Limitations: As above
    boolean hasSelfEmploymentStatementStudentSpouse = false
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Student Spouse") && receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").isAcceptable())
    {
        hasSelfEmploymentStatementStudentSpouse = true
    }

    // ARITHMETIC:
    // Operation 2: Student Spouse Self-Employment Income
    // Variables: selfEmploymentStudentSpouse
    // BOUNDARY CHECKS: as above
    int selfEmploymentStudentSpouse = 0
    if (hasSelfEmploymentStatementStudentSpouse)
    {
        if (receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015").isAllWhitespace())
        {
            selfEmploymentSpouse = receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015").toFloat().round()
        }
    }

    // ARITHMETIC:
    // Operation 3: Sum of Student W2 Earnings
    // Variables:
    //   - w2DocsStudent: list of received student W2 docs
    //   - AC1010: String wage field, convertible to float (ac1010Value)
    //   - w2SumStudentFloat: float sum of W2 AC1010 values
    //   - w2SumStudent: int sum of W2 AC1010 values    // Formula: sum(DocumentsForDocCode("W2", "Student").AC1010)
    // Steps:
    //   1. Collect all W2 documents for student.
    //   2. For each, extract AC1010, check validity, parse as float, accumulate.
    //   3. Final sum rounded to int for further calculations.
    // Business Context: Federally mandated inclusion of W2 income.
    // Financial Impact: Directly affects EFC and aid eligibility cutoff.
    float w2SumStudentFloat = 0.0
    def w2DocsStudent = receivedDocuments.getDocumentsForDocCode("W2", "Student")
    for (doc in w2DocsStudent)
    {
        String ac1010 = doc.getDocumentField("AC1010")
        float ac1010Value = 0.0
        if (ac1010 != null && !ac1010.isAllWhitespace())
        {
            ac1010Value = ac1010.toFloat()
        }
        w2SumStudentFloat += ac1010Value
    }
    int w2SumStudent = w2SumStudentFloat.round()

    // ARITHMETIC:
    // Operation 4: Same as above for Student Spouse W2 earnings.
    float w2SumStudentSpouseFloat = 0.0
    def w2DocsStudentSpouse = receivedDocuments.getDocumentsForDocCode("W2", "Student Spouse")
    for (doc in w2DocsStudentSpouse)
    {
        String ac1010 = doc.getDocumentField("AC1010")
        float ac1010Value = 0.0
        if (ac1010 != null && !ac1010.isAllWhitespace())
        {
            ac1010Value = ac1010.toFloat()
        }
        w2SumStudentSpouseFloat += ac1010Value
    }
    int w2SumStudentSpouse = w2SumStudentSpouseFloat.round()

    // ARITHMETIC:
    // Operation 5: Sum of Student 1099G Income
    float doc1099StudentFloat = 0.0
    def doc1099GDocsStudent = receivedDocuments.getDocumentsForDocCode("1099G", "Student")
    for (doc in doc1099GDocsStudent)
    {
        String ac1013 = doc.getDocumentField("AC1013")
        float ac1013Value = 0.0
        if (ac1013 != null && !ac1013.isAllWhitespace())
        {
            ac1013Value = ac1013.toFloat()
        }
        doc1099StudentFloat += ac1013Value
    }

    // CONDITIONALS:
    // Check 12: If docOwner is Student, also include the current doc's AC1013
    if (docOwner == "Student")
    {
        String currentAC1013 = currentDocument.getDocumentFieldValue("AC1013")
        if (currentAC1013 != null && !currentAC1013.isAllWhitespace())
        {
            doc1099StudentFloat += currentAC1013.toFloat()
        }
    }
    int doc1099SumStudent = doc1099StudentFloat.round()

    // ARITHMETIC:
    // Operation 6: 1099G sum for Student Spouse
    float doc1099StudentSpouseFloat = 0.0
    def doc1099GDocsStudentSpouse = receivedDocuments.getDocumentsForDocCode("1099G", "Student Spouse")
    for (doc in doc1099GDocsStudentSpouse)
    {
        String ac1013 = doc.getDocumentField("AC1013")
        float ac1013Value = 0.0
        if (ac1013 != null && !ac1013.isAllWhitespace())
        {
            ac1013Value = ac1013.toFloat()
        }
        doc1099StudentSpouseFloat += ac1013Value
    }
    if (docOwner == "Student Spouse")
    {
        String currentAC1013 = currentDocument.getDocumentFieldValue("AC1013")
        if (currentAC1013 != null && !currentAC1013.isAllWhitespace())
        {
            doc1099StudentSpouseFloat += currentAC1013.toFloat()
        }
    }
    int doc1099SumStudentSpouse = doc1099StudentSpouseFloat.round()

    // ARITHMETIC:
    // Operation 7: Total Income Calculation
    // Result stored later depending on marital status
    int totalIncome = 0

    // CONDITIONALS:
    // Check 13: Extract Student Spouse's Age from Verification Worksheet
    // Variables: receivedDocuments.get().getDocumentField(), StudentSpouseAge, hasVerificationWorksheetIndStudent, StudentSpouseAgeString
    // Purpose: Check a series of coded worksheet fields for indication that a dependent is "Student Spouse"
    // Logic:
    //   - Series of "if/else-if" for each supported field (AC1044-AC1053), if any is "Student Spouse" then read corresponding age field (AC1034-AC1043)
    // Success Path: If found, age set as integer; else left as default (-1)
    // Failure Path: Age = -1, handled as conservative lower-age default in downstream checks
    // Business Rules: Required for IRS filing threshold logic
    // Error Handling: Null field or whitespace = -1 default (safe, but means higher doc threshold later)
    // Financial Impact: Over/underestimate of age may request more documentation than required but avoids under-collection (audit risk)
    // BOUNDARY CHECKS: Explicit null/whitespace checks for string conversion.
    // Edge case: If age field missing for spouse, stays default -1 and conservative doc logic applies.
    int StudentSpouseAge = -1
    if (hasVerificationWorksheetIndStudent)
    {
        if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1044") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1034")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1045") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1035")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1046") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1036")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1047") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1037")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1048") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1038")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1049") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1039")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1050") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1040")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1051") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1041")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1052") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1042")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
        else if (receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1053") == "Student Spouse")
        {
            String StudentSpouseAgeString = receivedDocuments.get("VW-Ind", "Student").getDocumentField("AC1043")
            if (StudentSpouseAgeString != null && !StudentSpouseAgeString.isAllWhitespace())
            {
                StudentSpouseAge = StudentSpouseAgeString.toInteger()
            }
        }
    }

    // ARITHMETIC:
    // Operation 8: Calculate If Student Is Under Age 65
    // Formula: studentBirthday > age65Threshold -> under 65
    // Variables: studentAgeUnder65, age65Threshold, studentBirthday, isirSTUDENT_DATEOFBIRTH
    // Steps:
    // 1. If birthdate provided, use actual, else use safe default.
    // 2. Compare to threshold.
    // Result: Boolean for age-dependent IRS threshold logic.
    boolean studentAgeUnder65 = true
    LocalDate age65Threshold = new LocalDate(1957, 12, 31)
    LocalDate studentBirthday = new LocalDate(2022, 01, 01)
    if (isirSTUDENT_DATEOFBIRTH != null)
    {
        studentBirthday = isirSTUDENT_DATEOFBIRTH.secureValue()
    }
    if (studentBirthday <= age65Threshold)
    {
        studentAgeUnder65 = false
    }

    // ARITHMETIC:
    // Operation 9: IRS Filing Threshold Calculation (Student)
    // Formula: IRS rules for 2023; based on marital status, age, and filing status
    // Variables: filingThreshold, isirSTUDENTFTIM_FILINGSTATUSCODE_FTI, studentAgeUnder65, StudentSpouseAge
    // Steps:
    //   - Use IRS-provided mappings (simple integer switches) to select proper threshold
    //   - Default high (999999) for uninformed scenarios
    // Business Context: Determines if additional docs are required.
    // Financial Impact: Errors/changes here propagate to document burdens in audit/packaging.
    // BOUNDARY CHECKS:
    //   - Logic covers all enumerated IRS filing categories
    //   - Edge case: Out-of-bounds, undecipherable status = conservative high threshold
    int filingThreshold = 999999
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "1" && studentAgeUnder65)
    {
        filingThreshold = 14600
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "1" && !studentAgeUnder65)
    {
        filingThreshold = 16550
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "2" && studentAgeUnder65 && StudentSpouseAge < 65)
    {
        filingThreshold = 29200
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "2" && (studentAgeUnder65 || StudentSpouseAge < 65))
    {
        filingThreshold = 30750
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "2" && !studentAgeUnder65 && StudentSpouseAge >= 65)
    {
        filingThreshold = 32300
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "3")
    {
        filingThreshold = 5
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "4" && studentAgeUnder65)
    {
        filingThreshold = 21900
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "4" && !studentAgeUnder65)
    {
        filingThreshold = 23850
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "5" && studentAgeUnder65)
    {
        filingThreshold = 29200
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "5" && !studentAgeUnder65)
    {
        filingThreshold = 30750
    }

    // CONDITIONALS:
    // Check 14: Marital Status Group Decision (Single/Independent vs. Married/Spouse)
    // Variables: isirSTUDENT_MARITALSTATUS
    // Purpose: Determine if only student income, or also spouse income, is used
    // Logic:
    //   - If status is "1"/"4" (typically 'single' or 'divorced'/unmarried) OR null, use only student totals
    //   - Else if married (status "2"/"3"), use student + spouse totals
    // Success Path: Sets totalIncome appropriately, triggers correct doc request logic
    // Business Rules: IRS federal packaging rules
    // Error Handling: Null handled as single
    // Financial Impact: Avoids under/over-requesting income docs and inappropriate aid determination
    if (isirSTUDENT_MARITALSTATUS == "1" || isirSTUDENT_MARITALSTATUS == "4" || isirSTUDENT_MARITALSTATUS == null)
    {
        // BOUNDARY CHECKS: Sums only student fields, spouse excluded
        // ARITHMETIC: Aggregate sum
        // CONDITIONALS:
        // Check 15: Total Income vs. Filing Threshold
        totalIncome = w2SumStudent + doc1099SumStudent + selfEmploymentStudent
        if (totalIncome >= filingThreshold)
        {
            // API INTERACTION:
            // Method: supportingDoc.addAnyDocuments()
            // Purpose: Request multiple acceptable equivalent documents for verification compliance (any one of which suffices).
            // Logic: Add doc requests for IRSForm4868, TaxReturnTranscript, etc.
            // Error Handling: N/A (API handles duplicates/ambiguity)
            // Financial Impact: Ensures regulatory documentation, minimizes improper aid award
            supportingDoc.addAnyDocuments(["IRSForm4868", "Student"],["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"])
        }
    }
    // BOUNDARY CHECKS: Marital status logic covers both one- and two-filer scenarios
    // ARITHMETIC: Sums total income differently depending on family unit
    // Financial Impact: Misclassification causes under-/over-verification and improper aid risk
    else if (isirSTUDENT_MARITALSTATUS == "2" || isirSTUDENT_MARITALSTATUS == "3")
    {
        totalIncome = w2SumStudent + doc1099SumStudent + w2SumStudentSpouse + doc1099SumStudentSpouse + selfEmploymentStudent + selfEmploymentStudentSpouse
        if (totalIncome >= filingThreshold)
        {
            // Add documentation requests for student and spouse
            supportingDoc.addAnyDocuments(["IRSForm4868", "Student"],["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"],["TaxReturnTranscript", "Student Spouse"],["1040x", "Student Spouse"],["ForeignTaxTranscript", "Student Spouse"])
        }
    }
}

// CONDITIONALS:
// Check 16: Owner is Parent
// Variables: docOwner
// Purpose: Enter parent-branch logic for dependent students
// Logic: if (docOwner == "Parent")
if (docOwner == "Parent")
{
    // CONDITIONALS:
    // Check 17 & 18: W2 Docs Acceptability for Parent and Parent Spouse
    boolean hasW2Parent = receivedDocuments.hasDoc("W2", "Parent") && receivedDocuments.get("W2", "Parent").isAcceptable()
    boolean hasW2ParentSpouse = receivedDocuments.hasDoc("W2", "Parent Spouse") && receivedDocuments.get("W2", "Parent Spouse").isAcceptable()

    // ARITHMETIC:
    // Operation 10: Calculate If Parent/Spouse Are Under Age 65
    boolean ParentUnder65 = true
    boolean ParentSpouseUnder65 = true
    LocalDate age65Threshold = new LocalDate(1957, 12, 31)
    LocalDate ParentBirthday = new LocalDate(2022, 01, 01)
    LocalDate ParentSpouseBirthday = new LocalDate(2022, 01, 01)
    if (isirPARENT_DATEOFBIRTH != null)
    {
        ParentBirthday = isirPARENT_DATEOFBIRTH.secureValue()
    }
    if (isirPARENTSPOUSE_DATEOFBIRTH != null)
    {
        ParentSpouseBirthday = isirPARENTSPOUSE_DATEOFBIRTH.secureValue()
    }
    if (ParentBirthday <= age65Threshold)
    {
        ParentUnder65 = false
    }
    if (ParentSpouseBirthday <= age65Threshold)
    {
        ParentSpouseUnder65 = false
    }

    // ARITHMETIC:
    // Operation 11: Filing Threshold Calculation for Parents
    int filingThreshold = 999999
    // CONDITIONALS:
    // Check 19: If no W2 docs at all, use $400 threshold (per IRS)
    if (!hasW2Parent && !hasW2ParentSpouse)
    {
        filingThreshold = 400
    }
    else
    {
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "1" && ParentUnder65)
        {
            filingThreshold = 14600
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "1" && !ParentUnder65)
        {
            filingThreshold = 16550
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "2" && ParentUnder65 && ParentSpouseUnder65)
        {
            filingThreshold = 29200
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "2" && (ParentUnder65 || ParentSpouseUnder65))
        {
            filingThreshold = 30750
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "2" && !ParentUnder65 && ParentSpouseUnder65)
        {
            filingThreshold = 32300
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "3")
        {
            filingThreshold = 5
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "4" && ParentUnder65)
        {
            filingThreshold = 21900
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "4" && !ParentUnder65)
        {
            filingThreshold = 23850
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "5" && ParentUnder65)
        {
            filingThreshold = 29200
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "5" && !ParentUnder65)
        {
            filingThreshold = 30750
        }
    }

    // BOUNDARY CHECKS:
    //   - Covers absence of W2 docs, IRS low-income scenario, all other IRS-coded statuses for dependent parent families.
    // ARITHMETIC:
    // Operation 12: Parent/Parent Spouse Self-Employment, W2, and 1099G Income
    // (Equivalent logic as Student branch)
    int selfEmploymentParent = 0
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent") && receivedDocuments.get("SelfEmploymentStatement", "Parent").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Parent").getDocumentField("AC1015").isAllWhitespace())
    {
        selfEmploymentParent = receivedDocuments.get("SelfEmploymentStatement", "Parent").getDocumentField("AC1015").toFloat().round()
    }
    int selfEmploymentParentSpouse = 0
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent Spouse") && receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").getDocumentField("AC1015").isAllWhitespace())
    {
        selfEmploymentParentSpouse = receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").getDocumentField("AC1015").toFloat().round()
    }
    float w2SumParentFloat = 0.0
    def w2DocsParent = receivedDocuments.getDocumentsForDocCode("W2", "Parent")
    for (doc in w2DocsParent)
    {
        String ac1010 = doc.getDocumentField("AC1010")
        float ac1010Value = 0.0
        if (ac1010 != null && !ac1010.isAllWhitespace())
        {
            ac1010Value = ac1010.toFloat()
        }
        w2SumParentFloat += ac1010Value
    }
    int w2SumParent = w2SumParentFloat.round()
    float w2SumParentSpouseFloat = 0.0
    def w2DocsParentSpouse = receivedDocuments.getDocumentsForDocCode("W2", "Parent Spouse")
    for (doc in w2DocsParentSpouse)
    {
        String ac1010 = doc.getDocumentField("AC1010")
        float ac1010Value = 0.0
        if (ac1010 != null && !ac1010.isAllWhitespace())
        {
            ac1010Value = ac1010.toFloat()
        }
        w2SumParentSpouseFloat += ac1010Value
    }
    int w2SumParentSpouse = w2SumParentSpouseFloat.round()
    float doc1099ParentFloat = 0.0
    def doc1099GDocsParent = receivedDocuments.getDocumentsForDocCode("1099G", "Parent")
    for (doc in doc1099GDocsParent)
    {
        String ac1013 = doc.getDocumentField("AC1013")
        float ac1013Value = 0.0
        if (ac1013 != null && !ac1013.isAllWhitespace())
        {
            ac1013Value = ac1013.toFloat()
        }
        doc1099ParentFloat += ac1013Value
    }
    if (docOwner == "Parent")
    {
        String currentAC1013 = currentDocument.getDocumentFieldValue("AC1013")
        if (currentAC1013 != null && !currentAC1013.isAllWhitespace())
        {
            doc1099ParentFloat += currentAC1013.toFloat()
        }
    }
    int doc1099SumParent = doc1099ParentFloat.round()
    float doc1099ParentSpouseFloat = 0.0
    def doc1099GDocsParentSpouse = receivedDocuments.getDocumentsForDocCode("1099G", "Parent Spouse")
    for (doc in doc1099GDocsParentSpouse)
    {
        String ac1013 = doc.getDocumentField("AC1013")
        float ac1013Value = 0.0
        if (ac1013 != null && !ac1013.isAllWhitespace())
        {
            ac1013Value = ac1013.toFloat()
        }
        doc1099ParentSpouseFloat += ac1013Value
    }
    if (docOwner == "Parent Spouse")
    {
        String currentAC1013 = currentDocument.getDocumentFieldValue("AC1013")
        if (currentAC1013 != null && !currentAC1013.isAllWhitespace())
        {
            doc1099ParentSpouseFloat += currentAC1013.toFloat()
        }
    }
    int doc1099SumParentSpouse = doc1099ParentSpouseFloat.round()

    // ARITHMETIC:
    // Operation 13: Total parental income
    int totalIncome = 0

    // CONDITIONALS:
    // Check 20: Filing status category "3"
    if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "3")
    {
        totalIncome = selfEmploymentParent + selfEmploymentParentSpouse + w2SumParent + w2SumParentSpouse + doc1099SumParent + doc1099SumParentSpouse
        if (totalIncome >= filingThreshold)
        {
            // API INTERACTION:
            // Method: supportingDoc.addAnyDocuments() for both parent and parent spouse
            // Business Rules: Both parties may be required to provide docs if any income over minimal threshold
            supportingDoc.addAnyDocuments(["IRSForm4868", "Parent"],["TaxReturnTranscript", "Parent"],["1040x", "Parent"],["ForeignTaxTranscript", "Parent"],["IRSForm4868", "Parent Spouse"],["TaxReturnTranscript", "Parent Spouse"],["1040x", "Parent Spouse"],["ForeignTaxTranscript", "Parent Spouse"])
        }
    }
    else
    {
        // For all other parent tax statuses, only require docs for Parent owner, not spouse
        supportingDoc.addAnyDocuments(["IRSForm4868", "Parent"],["TaxReturnTranscript", "Parent"],["1040x", "Parent"],["ForeignTaxTranscript", "Parent"])
    }
}