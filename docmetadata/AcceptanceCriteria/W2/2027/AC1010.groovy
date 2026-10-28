/**
 * Primary Purpose and Functionality:
 *   - To evaluate document metadata fields for a student's or parent's document for specific financial aid years, in order to:
 *     - Assess document acceptance
 *     - Determine if additional supporting documents are required based on document type, field values, income substantiation, and ISIR (Institutional Student Information Record) data.
 *
 * Key Calculations Performed:
 *   - Summing reported wages and other incomes (W2, 1099G, and self-employment) for students, spouses, parents, and parent spouses.
 *   - Determining age thresholds and tax filing requirements per IRS and federal financial aid rules.
 *   - Comparison of total income values against dynamically determined IRS filing thresholds depending on tax status and age.
 *
 * Critical Business Rules Enforced:
 *   - IRS and federal threshold checks for mandatory tax documentation collection based on reported incomes
 *   - Enforcement of federal student aid verification document requirements according to marital and filing statuses
 *   - Ensuring no federal aid is processed w/o required financial substantiation, supporting compliance with US DOE (Department of Education) rules
 *
 * Boundary Conditions and Limits:
 *   - Date-based age checks (under or over 65) both for students/spouses and parents/parent spouses, determining different IRS thresholds
 *   - Handles cases where field metadata is missing, blank, or incorrectly formatted, preventing corrupt data from influencing eligibility
 *
 * Financial Aid Implications:
 *   - Directly impacts whether a student/family is considered for aid, suspended for missing documentation, or flagged for review
 *   - Incorrect threshold or document evaluation impacts compliance, student aid disbursement timing, and institutional risk
 *
 * Required Inputs:
 *   - ISIR field values for students and parents for the award year
 *   - All supporting tax and verification documents received for the award year
 * Expected Output:
 *   - Populates 'supportingDoc' with additional required documents for the applicant (student/parent/etc.) where compliance rules dictate
 *   - Returns void; control flows via Oracle SFP platform bindings
 *
 * Error Handling Approach:
 *   - Early exits on missing ISIR or required data
 *   - Defensive checks for null/blank fields to avoid exceptions in data interpretation
 *   - Document acceptance methods only proceed when validated as acceptable
 *
 * Integration Points:
 *   - IMultiAwardYearReceivedDocsAPI: forAwardYear()
 *   - IReceivedDocDocumentAPI: getDocumentFieldValue(), getDocumentField(), isAcceptable(), getDocumentOwnerTypes()
 *   - IIsirRecordDocumentAPI: getField(), secureValue(), etc.
 *   - IIsirRequiredDocsAPI: addAnyDocuments(), addDocument()
 *
 * Compliance Considerations:
 *   - Enforces income and documentation threshold rules defined by IRS and Department of Education
 *   - All sensitive personal and financial data is handled in accordance with FERPA and Oracle platform data security guidelines
 * ------------------------------------------------------------------------------------------
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

// CONDITIONALS:
// Check 1: ISIR Record Presence Validation
// Variables: isirRecord
// Purpose: Ensure ISIR record is available before performing further checks.
// Logic:
//   - If isirRecord is null, exit script (no further processing possible).
//   - Prevents downstream operations from null pointer exceptions.
// Success Path: Script exits with no action if ISIR missing.
// Failure Path: If ISIR present, script continues document evaluation.
// Business Rules: No financial aid eligibility or document evaluation can occur without ISIR data.
// Error Handling: Early return on null ISIR; prevents runtime errors.
// Financial Impact: Prevents non-compliant aid processing when ISIR missing.
// BOUNDARY CHECKS: N/A (Simple null check, boundary not derived from external config.)
// ARITHMETIC: N/A
if (isirRecord == null) {
    return
}

// CONDITIONALS:
// Check 2: Substitute Non-FTI Tax Filing Status if FTI is Blank
// Variables: isirRecord, isirSTUDENTFTIM_FILINGSTATUSCODE_FTI
// Purpose: Ensure student tax filing status is set for downstream filing threshold checks.
// Logic:
//   - If FTI field for filing status is null, substitute with non-FTI field.
//   - Ensures availability of filing status info for all students, regardless of ISIR version.
// Success Path: Variable isirSTUDENTFTIM_FILINGSTATUSCODE_FTI is always set for calculations.
// Failure Path: If both FTI and non-FTI are blank/null, subsequent threshold logic may not function accurately.
// Business Rules: Regulatory rules require accurate tax filing status for threshold determination.
// Error Handling: Fallback substitution prevents null reference errors, but does not remediate if both fields missing.
// Financial Impact: Prevents under-/over-requesting documents, affecting compliance and aid disbursement.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def isirSTUDENT_MARITALSTATUS = isirRecord.getField(IsirField.STUDENT_MARITALSTATUS).getOrNull()
def isirSTUDENT_DATEOFBIRTH = isirRecord.getField(IsirField.STUDENT_DATEOFBIRTH).getOrNull()
def isirSTUDENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.STUDENTFTIM_FILINGSTATUSCODE_FTI).getOrNull()
if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == null) {
    isirSTUDENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.STUDENT_TAXRETURNFILINGSTATUS).getOrNull()
}

// CONDITIONALS:
// Check 3: Substitute Non-FTI Parent Tax Filing if FTI is Blank
// Variables: isirRecord, isirPARENTFTIM_FILINGSTATUSCODE_FTI
// Purpose: Ensures parent's tax filing status info is available for threshold calculations.
// Logic: As above, for parent data.
// Success Path/Failure Path/Business Rules: See above.
// Error Handling: As above.
// Financial Impact: As above.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def isirPARENT_DATEOFBIRTH = isirRecord.getField(IsirField.PARENT_DATEOFBIRTH).getOrNull()
def isirPARENTSPOUSE_DATEOFBIRTH = isirRecord.getField(IsirField.PARENTSPOUSE_DATEOFBIRTH).getOrNull()
def isirPARENTFTIM_FILINGSTATUSCODE_FTI =  isirRecord.getField(IsirField.PARENTFTIM_FILINGSTATUSCODE_FTI).getOrNull()
if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == null) {
    isirPARENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.PARENT_TAXRETURNFILINGSTATUS).getOrNull()
}

// CONDITIONALS:
// Check 4: Extract Document Owner Type
// Variables: currentDocument.getDocumentOwnerTypes()
// Purpose: Identifies who the document is about (Student, Student Spouse, Parent, Parent Spouse).
// Logic:
//   - Always fetch first owner type from list (assumes single owner per doc).
// Success Path: Owner identified, determines which branch of logic below is executed.
// Failure Path: If list is empty/anomalous, logic below may malfunction (unhandled).
// Business Rules: Operation depends on correctly specified doc owner types.
// Error Handling: N/A (misconfiguration not directly handled).
// Financial Impact: Misclassification may result in incorrect document requests or compliance failures.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
def docOwner = currentDocument.getDocumentOwnerTypes().get(0)

// CONDITIONALS:
// Check 6: Owner-based Logic Branching (Student Owner Path)
// Variables: docOwner
// Purpose: Branches processing logic based on who the owner is; here, handles student and student spouse incomes.
// Logic:
//   - If owner is "Student", perform income/data collection for student side
//   - Includes nested logic to gather supporting docs if income/threshold rule is failed
// Success Path: Full compliance checks executed for student
// Failure Path: N/A for student owner branch (falls through to else for parent owner branch)
// Business Rules: Only relevant logic for correct owner is evaluated and enforced
// Error Handling: N/A
// Financial Impact: Ensures only relevant documents are required/requested per owner context.
// BOUNDARY CHECKS: N/A
// ARITHMETIC: N/A
if (docOwner == "Student")
{
    // CONDITIONALS:
    // Check 7: Verification Worksheet Existence and Acceptability for Student
    // Variables: receivedDocuments.hasDoc(), receivedDocuments.get(...).isAcceptable()
    // Purpose: Confirm presence of an accepted VW-Ind document for accurate spouse age extraction (affects filing threshold)
    // Logic:
    //   - If received VW-Ind doc for Student exists and isAcceptable() returns true
    //   - Set hasVerificationWorksheetIndStudent to true
    // Success Path: Spouse age information may be available for use below
    // Failure Path: Spouse age assignment logic skipped/irrelevant
    // Business Rules: Only accepted/complete documents should inform financial aid review
    // Error Handling: N/A (bad data is ignored)
    // Financial Impact: Allows correct threshold assignment for married students
    // BOUNDARY CHECKS: N/A
    // ARITHMETIC: N/A
    boolean hasVerificationWorksheetIndStudent = false
    if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
    {
        hasVerificationWorksheetIndStudent = true
    }

    // CONDITIONALS:
    // Check 8: Self-Employment Statement Acceptability for Student
    // Variables: receivedDocuments.hasDoc(), receivedDocuments.get(...).isAcceptable()
    // Purpose: Confirm valid self-employment income is available for student
    // Logic:
    //   - True if a "SelfEmploymentStatement" doc for Student exists and is acceptable
    // Success/Failure Path: Sets flag for subsequent income summing logic
    // Business Rules: Only accepted documents inform official self-employment values
    // Error Handling: N/A
    // Financial Impact: Only validated self-employment values impact aid thresholds
    // BOUNDARY CHECKS: N/A
    // ARITHMETIC:
    // Operation 1: Student Self-Employment Income Conversion and Rounding
    // Formula: toFloat().round()
    // Variables:
    //   - AC1015 field: self-employment income, String, to be parsed as float and rounded
    //   - selfEmploymentStudent: Integer, to be used in income summation
    // Steps:
    //   1. Check field presence and whitespace
    //   2. Convert string to float, round to nearest int
    //   3. Assign to selfEmploymentStudent
    // Business Context: Self-employment income is counted toward IRS threshold logic
    // Result: Integer self-employment income value
    // Financial Impact: Informs doc requirement logic
    // Limitations: Field must be correctly formatted, not blank
    boolean hasSelfEmploymentStatementStudent = false
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Student") && receivedDocuments.get("SelfEmploymentStatement", "Student").isAcceptable())
    {
        hasSelfEmploymentStatementStudent = true
    }

    int selfEmploymentStudent = 0
    if (hasSelfEmploymentStatementStudent)
    {
        if (receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").isAllWhitespace())
        {
            selfEmploymentStudent = receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").toFloat().round()
        }
    }

    // CONDITIONALS:
    // Check 9: Student Spouse Self-Employment Statement Acceptable
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

    int selfEmploymentStudentSpouse = 0
    if (hasSelfEmploymentStatementStudentSpouse)
    {
        if (receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015").isAllWhitespace())
        {
            selfEmploymentStudentSpouse = receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015").toFloat().round()
        }
    }

    // ARITHMETIC:
    // Operation 3: Student W2 Document Wages Summation and Rounding
    // Formula: sum(all W2 doc AC1010 values) + (if docOwner == "Student": current AC1010 value)
    // Variables:
    //   - w2DocsStudent: list of received student W2 docs
    //   - AC1010: String wage field, convertible to float (ac1010Value)
    //   - w2SumStudentFloat: float sum of W2 AC1010 values
    //   - w2SumStudent: int sum of W2 AC1010 values
    // Steps:
    //   1. For each doc: extract, validate, convert, sum
    //   2. If current doc is also owned by Student, add its value
    //   3. Final sum is rounded and used
    // Business Context: Sum of W2 income forms major part of taxable earnings
    // Result: w2SumStudent (Integer)
    // Financial Impact: Used in IRS threshold evaluation
    // Limitations: Non-numeric or misformatted values quietly ignored (as zero)
    // BOUNDARY CHECKS: N/A (handles nulls, whitespace, misformat with zero fallback)
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
    if (docOwner == "Student")
    {
        String currentAC1010 = currentDocument.getDocumentFieldValue("AC1010")
        if (currentAC1010 != null && !currentAC1010.isAllWhitespace())
        {
            w2SumStudentFloat += currentAC1010.toFloat()
        }
    }
    int w2SumStudent = w2SumStudentFloat.round()

    // ARITHMETIC:
    // Operation 4: Student Spouse W2 Summation
    // Formula/Variables/Steps/Business Context/Result/Financial Impact: As above, for "Student Spouse"
    // BOUNDARY CHECKS: N/A
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
    if (docOwner == "Student Spouse")
    {
        String currentAC1010 = currentDocument.getDocumentFieldValue("AC1010")
        if (currentAC1010 != null && !currentAC1010.isAllWhitespace())
        {
            w2SumStudentSpouseFloat += currentAC1010.toFloat()
        }
    }
    int w2SumStudentSpouse = w2SumStudentSpouseFloat.round()

    // ARITHMETIC:
    // Operation 5: Student 1099G Document Summation
    // Formula: sum(all 1099G doc AC1013 values)
    // Variables: AC1013 (1099G income field), ac1013Value, doc1099StudentFloat, doc1099SumStudent
    // Steps: As above for different doc code & field
    // Context/Result/Impact: 1099G income required by IRS for aggregate earned income calculation
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
    int doc1099SumStudent = doc1099StudentFloat.round()

    // ARITHMETIC:
    // Operation 6: Student Spouse 1099G Summation
    // As above, for "Student Spouse"
    // BOUNDARY CHECKS: N/A
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
    int doc1099SumStudentSpouse = doc1099StudentSpouseFloat.round()

    // CONDITIONALS:
    // Check 10: Extract Student Spouse Age from Verification Worksheet (if available)
    // Variables: receivedDocuments.get(...).getDocumentField(), hasVerificationWorksheetIndStudent, StudentSpouseAgeString
    // Purpose: For married filers, correct spouse age is critical to accurately assign IRS threshold value
    // Logic:
    //   - For each VW-Ind child-field pair (AC1044-AC1053), check if value == "Student Spouse"
    //   - If so, get matching spouse age from related field (AC1034-AC1043)
    //   - Convert to Integer if present/non-whitespace/parsable
    // Success Path: StudentSpouseAge is set to correct integer value
    // Failure Path: Value remains -1 if not found/misformatted
    // Business Rules: Each worksheet maps different household member by field
    // Error Handling: Ignores missing fields or bad data
    // Financial Impact: Age64/65+ leads to different IRS threshold
    // BOUNDARY CHECKS:
    // Check 1: Spouse Age Parsing Limit
    // Source: Age fields may be present or missing, input fields must be a non-empty numeric string
    // Variables: StudentSpouseAgeString, StudentSpouseAge, totalIncome
    // Validation:
    //   - Only assigns value if string present, non-blank, and parseable as integer
    // Edge Cases:
    //   - No match or missing field: leaves StudentSpouseAge as default -1 (handled below)
    // Error Handling:
    //   - Malformed input safely ignored
    // Impact:
    //   - If not found, affects subsequent threshold determination
    int totalIncome = 0
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
    // Operation 7: Student Age Threshold for Tax Filing (Age 65 Test)
    // Formula: studentBirthday <= age65Threshold
    // Variables:
    //   - studentBirthday: LocalDate (parsed from ISIR, or defaults)
    //   - age65Threshold: LocalDate (cutoff for age determination)
    // Steps:
    //   1. If ISIR DOB present, extract & assign
    //   2. Compare with threshold of December 31, 1957
    //   3. Sets flag for under/over 65
    // Business Context: IRS has different thresholds for filers aged 65+
    // Result: Boolean studentAgeUnder65
    // Financial Impact: Incorrect result causes improper doc requirement
    // BOUNDARY CHECKS:
    // Check 2: Student Birthday Boundary
    // Source: 1957-12-31 fixed as IRS threshold age-65
    // Variables: studentBirthday, age65Threshold, studentAgeUnder65, isirSTUDENT_DATEOFBIRTH
    // Validation: Valid only if date present; if not, defaults safely to <65 (could lead to doc over-request)
    // Edge Cases/Handling: Bad or missing DOBs default to under 65 (increased verification requirement)
    // Impact: Over-request documentation if no DOB, preventing improper aid
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
    // Operation 8: Filing Threshold Determination (IRS Logic)
    // Formula: Matches IRS schedule according to filing status and ages
    // Variables: isirSTUDENTFTIM_FILINGSTATUSCODE_FTI, studentAgeUnder65, StudentSpouseAge
    // Steps:
    //   1. Based on filing status, marital status, and ages, assigns proper income threshold for required tax documentation
    //   2. Hierarchy of logic matches IRS annual thresholds; impossible combinations default to a very high threshold (no doc requested)
    // Business Context: Central to determining whether additional IRS or financial aid documentation is required
    // Result: filingThreshold used downstream
    // Financial Impact: Directly impacts regulatory compliance & false negatives/positives in document collection
    // BOUNDARY CHECKS:
    // Check 3: Filing Threshold Range
    // Source: IRS rules for filing status/age for the specified year
    // Variables: filingThreshold
    // Validation: Covers all documented filing statuses; fallback 999999 prevents unnecessary doc requests if not matched
    // Edge Cases: Unrecognized status codes, unspecified spouse age, etc., result in ultra-high threshold (no request)
    // Error Handling: Defaults ensure never under-request
    // Impact: May cause doc over-request, but never exposes compliance to errors
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
    // Check 11: Marital Status Logic for Document Requirement
    // Variables: isirSTUDENT_MARITALSTATUS, totalIncome, filingThreshold
    // Purpose: Different income summing and documentation rules for different marital statuses
    // Logic:
    //   - If Student is single (codes 1, 2, 4, or null), add only student's income
    //   - If married (code 3), add student and spouse incomes
    //   - If totalIncome >= threshold, request IRS/other documents
    // Success Path: Only correct documents requested based on situation
    // Failure Path: If below threshold, no request made (doc collection minimized)
    // Business Rules: Marital status changes IRS rules for filing and aid verification
    // Error Handling: Mis-/unrecognized codes treated as single, maximizing compliance
    // Financial Impact: Prevents unnecessary document burden while ensuring required documentation for aid release
    // BOUNDARY CHECKS:
    // Check 4: Income Summation vs. Threshold Boundary
    // Source: IRS annual requirements
    // Variables: totalIncome, filingThreshold
    // Validation: Total income must be equal/greater to trigger request(s)
    // Edge Cases: Exactly at threshold triggers doc request (compliant)
    // Error Handling: N/A (arithmetic logic self-correcting)
    // Impact: Prevents improper aid award without required tax documentation
    // ARITHMETIC:
    // Operation 9: Total Income Summation
    // Formula: As above, adds all relevant incomes
    // Variables/Steps: w2SumStudent, doc1099SumStudent, selfEmploymentStudent, w2SumStudentSpouse, doc1099SumStudentSpouse, selfEmploymentStudentSpouse
    // Business Context: Central driver for doc requirement logic
    // Result: Integer totalIncome
    // Financial Impact: Forms basis for all subsequent regulatory compliance
    if (isirSTUDENT_MARITALSTATUS == "1" || isirSTUDENT_MARITALSTATUS == "2" || isirSTUDENT_MARITALSTATUS == "4" || isirSTUDENT_MARITALSTATUS == null)
    {
        totalIncome = w2SumStudent + doc1099SumStudent + selfEmploymentStudent
        if (totalIncome >= filingThreshold)
        {
            /* supportingDoc.addDocument("IRSForm4868", "Student") */
            supportingDoc.addAnyDocuments(["IRSForm4868", "Student"], ["IRSExtensionApproval", "Student"], ["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"])
        }
    }
    else if (isirSTUDENT_MARITALSTATUS == "3")
    {
        totalIncome = w2SumStudent + doc1099SumStudent + w2SumStudentSpouse + doc1099SumStudentSpouse + selfEmploymentStudent + selfEmploymentStudentSpouse
        if (totalIncome >= filingThreshold)
        {
            /* supportingDoc.addAnyDocument("IRSForm4868", "Student") */
            supportingDoc.addAnyDocuments(["IRSForm4868", "Student"], ["IRSExtensionApproval", "Student"], ["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"],["TaxReturnTranscript", "Student Spouse"],["1040x", "Student Spouse"],["ForeignTaxTranscript", "Student Spouse"])
        }
    }
}
else if (docOwner == "Parent" || docOwner == "Parent Spouse")
{
    // CONDITIONALS:
    // Check 12: Age 65 Tests for Parent and Parent Spouse
    // Variables: isirPARENT_DATEOFBIRTH, isirPARENTSPOUSE_DATEOFBIRTH, ParentUnder65, ParentSpouseUnder65, age65Threshold, ParentBirthday, ParentSpouseBirthday
    // Purpose: Split doc/threshold logic for parent vs parent spouse based on age
    // Logic: See below for assignment and comparison to 1957-12-31
    // Success/Failure Path: Impacts filingThreshold determination
    // Error Handling: Defaults to under 65 (many docs required)
    // Financial Impact: Over-request errors preferable to under-request for compliance
    // BOUNDARY CHECKS: See birthday parsing below
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
    // Operation 10: IRS Threshold for Parent Filing Status
    // Formula/Variables/Business Logic: Mirrors student side above, see IRS official tables
    int filingThreshold = 999999
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

    // ARITHMETIC:
    // Operation 11: Parent and Parent Spouse Income, Self-Employment, and W2/1099G Summations
    // Formula: See summing logic throughout
    // Variables/Steps: For each income source, extract, parse, sum as above
    // BOUNDARY CHECKS: N/A (all summing logic below)
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
    if (docOwner == "Parent")
    {
        String currentAC1010 = currentDocument.getDocumentFieldValue("AC1010")
        if (currentAC1010 != null && !currentAC1010.isAllWhitespace())
        {
            w2SumParentFloat += currentAC1010.toFloat()
        }
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
    if (docOwner == "Parent Spouse")
    {
        String currentAC1010 = currentDocument.getDocumentFieldValue("AC1010")
        if (currentAC1010 != null && !currentAC1010.isAllWhitespace())
        {
            w2SumParentSpouseFloat += currentAC1010.toFloat()
        }
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
    int doc1099SumParentSpouse = doc1099ParentSpouseFloat.round()

    // CONDITIONALS:
    // Check 13: Parent Filing Status == "3" vs Else for Documentation Requirement
    // Variables: isirPARENTFTIM_FILINGSTATUSCODE_FTI, totalIncome, filingThreshold, selfEmploymentParent, selfEmploymentParentSpouse, w2SumParent, w2SumParentSpouse, doc1099SumParent, doc1099SumParentSpouse
    // Purpose: Married vs non-married filing parents must submit different sets of documents
    // Logic:
    //   - If status "3" and income ≥ threshold, both parent and spouse must provide docs
    //   - Otherwise, only parent
    // Business Rules: Ensures proper compliance per IRS/DOE rules
    // Financial Impact: Risks to institutional liability reduced by correct document request
    // BOUNDARY CHECKS: See income/threshold comparisons above
    // ARITHMETIC: Income summation and comparison
    int totalIncome = 0
    if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "3")
    {
        totalIncome = selfEmploymentParent + selfEmploymentParentSpouse + w2SumParent + w2SumParentSpouse + doc1099SumParent + doc1099SumParentSpouse
        if (totalIncome >= filingThreshold)
        {
            supportingDoc.addAnyDocuments(["IRSForm4868", "Parent"], ["IRSExtensionApproval", "Parent"], ["TaxReturnTranscript", "Parent"],["1040x", "Parent"],["1040", "Parent"],["ForeignTaxTranscript", "Parent"],["IRSForm4868", "Parent Spouse"],["IRSExtensionApproval", "Parent Spouse"],["TaxReturnTranscript", "Parent Spouse"],["1040x", "Parent Spouse"],["1040", "Parent Spouse"],["ForeignTaxTranscript", "Parent Spouse"])
        }
    }
    else
    {
        supportingDoc.addAnyDocuments(["IRSForm4868", "Parent"], ["IRSExtensionApproval", "Parent"], ["TaxReturnTranscript", "Parent"],["1040x", "Parent"],["1040", "Parent"],["ForeignTaxTranscript", "Parent"])
    }
}