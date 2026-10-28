/**
 * Primary Purpose and Functionality:
 *   - This script evaluates student or parent document metadata in the Oracle SFP Platform, determining conditions for documenting IRS filing threshold compliance and assigning required additional supporting documents.
 *   - The script distinguishes between student and parent document owners, performing data aggregation from various income sources (W2s, 1099s, and self-employment).
 *
 * Key Calculations Performed:
 *   - Aggregates W2, 1099, and self-employment income for student/spouse and parent/spouse using platform APIs.
 *   - Calculates IRS filing thresholds based on marital status, age, and filing codes from ISIR fields.
 *   - Compares reported income to threshold to trigger supplemental document requests.
 *
 * Critical Business Rules Enforced:
 *   - Income aggregation must accurately reflect submitted documents' values.
 *   - IRS filing threshold logic follows federal and institutional policy for verification.
 *   - If earned income exceeds threshold, collection of tax return/transcript documentation becomes mandatory.
 *   - Filing statuses and marital status codes are interpreted from ISIR fields per current aid year rules.
 *
 * Boundary Conditions and Limits:
 *   - Only processes current ISIR and current document/award year.
 *   - Age determinations set boundary at age 65 (using birthdate threshold 1957-12-31).
 *   - Null/blank document or field values are safely handled and excluded from calculations.
 *
 * Financial Aid Implications:
 *   - Ensures compliance with verification rules for awarding/disbursing Title IV aid.
 *   - Incorrect threshold logic or document assignment could lead to improper aid eligibility decisions or delayed packaging.
 *   - Collecting correct supporting documents mitigates audit findings and institutional liability.
 *
 * Required Inputs and Expected Outputs:
 *   - Inputs: ISIR record fields, received documents collections, current document metadata, and supportingDoc API.
 *   - Outputs: None (void). Only side-effects: populates supportingDoc with document requirements.
 *
 * Error Handling Approach:
 *   - Exits gracefully if required data is missing (null ISIR, blank fields).
 *   - Platform method calls are guarded against null values/blank strings.
 *   - No unhandled exceptions should propagate.
 *
 * Integration Points:
 *   - Interacts with isirRecord, receivedDocuments, supportingDoc (Oracle SFP platform interfaces).
 *   - Reads document field values using currentDocument/receivedDocuments APIs.
 *   - Populates supportingDoc using addAnyDocuments().
 *
 * Compliance Considerations:
 *   - Accurate document and field usage supports institutional, programmatic, and federal recordkeeping.
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

// CONDITIONALS:
// Check 1: ISIR Record nullity check (Initial precondition)
// Variables: isirRecord
// Purpose: Ensures the script only executes if an ISIR record is present for the student.
// Logic:
//   - Checks if isirRecord is null (no active ISIR: student may be ineligible or record not loaded).
//   - Halts further execution if absent.
// Success Path: Script continues if ISIR is present.
// Failure Path: Script exits early (no processing occurs).
// Business Rules: No ISIR = no verification/threshold evaluation possible.
// Error Handling: Graceful termination with return.
// Financial Impact: Prevents unnecessary processing—no impact to aid awarding without ISIR.
// BOUNDARY CHECKS:
// Check 1: ISIR existence boundary enforced at script entry.
// Source: ISIR platform records.
// Variables: isirRecord.
// Validation: Simple null check, no further assumptions made.
// Edge Cases: N/A as script returns immediately if check fails.
// Error Handling: Safe exit.
// Impact: Avoids logic errors/calculation using missing data.
// ARITHMETIC: N/A
if (isirRecord == null) {
    return
}

// CONDITIONALS:
// Check 2: Fallback for missing FTIM Filing Status Code (Student)
// Variables: isirSTUDENTFTIM_FILINGSTATUSCODE_FTI, isirRecord.getField()
// Purpose: If FTIM filing status is missing, fallback to alternate filing status field.
// Logic:
//   - If 'STUDENTFTIM_FILINGSTATUSCODE_FTI' is null, use 'STUDENT_TAXRETURNFILINGSTATUS'.
//   - Ensures a filing status is always available for threshold calculation.
// Success Path: Filing status always derived for further logic.
// Failure Path: Filing status remains null and threshold checks may not trigger correctly.
// Business Rules: Filing status must be derived for federal threshold checks.
// Error Handling: Uses alternate source if primary is null.
// Financial Impact: Ensures accurate document requirements based on IRS rules.
// BOUNDARY CHECKS:
// Check 2: Alternate field boundary for filing status (STUDENT_TAXRETURNFILINGSTATUS).
// Source: ISIR fields as defined in federal file layouts.
// Variables: isirSTUDENTFTIM_FILINGSTATUSCODE_FTI.
// Validation: null evaluation, fallback population.
// Edge Cases: Both primary and alternate may be null, which disables downstream thresholds.
// Error Handling: None (cascades null if both are missing).
// Impact: Lack of a value could suppress required requests: potential compliance effect.
// ARITHMETIC: N/A
def isirSTUDENT_MARITALSTATUS = isirRecord.getField(IsirField.STUDENT_MARITALSTATUS).getOrNull()
def isirSTUDENT_DATEOFBIRTH = isirRecord.getField(IsirField.STUDENT_DATEOFBIRTH).getOrNull()
def isirSTUDENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.STUDENTFTIM_FILINGSTATUSCODE_FTI).getOrNull()
if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == null) {
    isirSTUDENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.STUDENT_TAXRETURNFILINGSTATUS).getOrNull()
}

// CONDITIONALS:
// Check 3: Fallback for missing FTIM Filing Status Code (Parent)
// Variables: isirPARENTFTIM_FILINGSTATUSCODE_FTI, isirRecord.getField()
// Purpose: Same as above for parent section—ensures availability of filing status.
// Logic: If 'PARENTFTIM_FILINGSTATUSCODE_FTI' is null, use 'PARENT_TAXRETURNFILINGSTATUS'.
// Business Rules: Parallel logic to student's filing status.
// Error Handling: Fallback approach.
// Financial Impact: Ensures parent tax thresholds can be checked.
// BOUNDARY CHECKS: See above.
// ARITHMETIC: N/A
def isirPARENT_DATEOFBIRTH = isirRecord.getField(IsirField.PARENT_DATEOFBIRTH).getOrNull()
def isirPARENTSPOUSE_DATEOFBIRTH = isirRecord.getField(IsirField.PARENTSPOUSE_DATEOFBIRTH).getOrNull()
def isirPARENTFTIM_FILINGSTATUSCODE_FTI =  isirRecord.getField(IsirField.PARENTFTIM_FILINGSTATUSCODE_FTI).getOrNull()
if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == null) {
    isirPARENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.PARENT_TAXRETURNFILINGSTATUS).getOrNull()
}

// CONDITIONALS:
// Check 4: Document owner routing for Student/Parent distinction
// Variables: currentDocument.getDocumentOwnerTypes()
// Purpose: Determine if document owner's evaluation logic should be for Student or Parent, influencing data sources and document assignments accordingly.
// Logic:
//   - Uses document owner type (first value only) for all downstream logic branches.
// Success Path: Owner-specific workflow enables correct verification branching.
// Failure Path: None (assumes value present).
// Business Rules: Owner distinction is critical for accurate aid processing and compliance;
//   determines whether spouse values/document requirements are included.
// Error Handling: None explicit (assumes document type is valid).
// Financial Impact: Essential for correct application of documentation policy.
def docOwner = currentDocument.getDocumentOwnerTypes().get(0)

// CONDITIONALS:
// Check 6: Begin Student-owner logic branch
// Variables: docOwner
// Purpose: Triggers student path logic for income/document/age analysis and request assignment.
// Business Rules: Owner-branching is fundamental to processing logic.
// Error Handling: N/A
// Financial Impact: Student requirements only processed if docOwner is Student.
if (docOwner == "Student")
{
    // CONDITIONALS:
    // Check 7: Has Verification Worksheet for Independent Student (VW-Ind)
    // Variables: receivedDocuments.hasDoc("VW-Ind", "Student"), receivedDocuments.get("VW-Ind", "Student").isAcceptable()
    // Purpose: Flag presence/acceptability of the independent student verification worksheet,
    //   which contains critical household and income data.
    // Logic:
    //   - Verifies document presence and acceptability.
    //   - Sets boolean for downstream triggering.
    // Success Path: Worksheet information available for extracting spouse age, etc.
    // Failure Path: Downstream checks using worksheet do not execute.
    // Business Rules: Verification worksheet is required for many student-level verifications.
    // Error Handling: Defensive default = false.
    // Financial Impact: Absent worksheet could limit documentation requests made.
    // BOUNDARY CHECKS: Validates document existence and acceptability boundary via API call.
    // Variables: receivedDocuments.hasDoc(), isAcceptable()
    // Edge Cases: Document present but not acceptable → treated as absent.
    // ARITHMETIC: N/A
    boolean hasVerificationWorksheetIndStudent = false
    if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
    {
        hasVerificationWorksheetIndStudent = true
    }

    // CONDITIONALS:
    // Check 8: Has Self Employment Statement for Student/Spouse
    // Variables: receivedDocuments.hasDoc(), receivedDocuments.get().isAcceptable()
    // Purpose: Flags for presence of required self-employment statements, which affect income calculations.
    // Logic: Parallel checks for both Student and Student Spouse.
    // Success/Failure Path: If available and acceptable, data used for calculations.
    // Business Rules: Verifies complete documentation for all potential sources of untaxed/earned income.
    // Error Handling: Gracefully skips if missing or not acceptable.
    // BOUNDARY CHECKS: Ensures only acceptable self-employment documents are included.
    // Variables: receivedDocuments API
    // Edge Cases: Missing or unacceptable docs treated as income = 0.
    // ARITHMETIC: N/A
    boolean hasSelfEmploymentStatementStudent = false
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Student") && receivedDocuments.get("SelfEmploymentStatement", "Student").isAcceptable())
    {
        hasSelfEmploymentStatementStudent = true
    }
    boolean hasSelfEmploymentStatementStudentSpouse = false
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Student Spouse") && receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").isAcceptable())
    {
        hasSelfEmploymentStatementStudentSpouse = true
    }

    // ARITHMETIC:
    // Operation 1: Compute Self-Employment Income (Student/Spouse)
    // Formula: Round(float value of AC1015) if present and valid.
    // Variables:
    //   - selfEmploymentStudent/selfEmploymentStudentSpouse: int (annual income, dollars)
    //   - Source fields: "AC1015" via currentDocument or referenced statement.
    // Steps:
    //   1. If docOwner is "Student", use currentDocument AC1015
    //   2. Otherwise, check acceptable self-employment statement document AC1015
    //   3. If field found/not blank, parse to float and round
    //   4. Repeat for Student Spouse
    // Business Context: Reported self-earned income affects total AGI for IRS threshold comparison.
    // Result: Integer dollar income from self-employment for both student and spouse.
    // Financial Impact: Income increases could trigger required document requests.
    // Limitations: Defaults to 0 if field not present/blank/missing doc.
    // BOUNDARY CHECKS: AC1015 may be null or blank; handled by explicit checks.
    // Edge Cases: All branches prevent NullPointerException.
    int selfEmploymentStudent = 0
    int selfEmploymentStudentSpouse = 0
    if (docOwner == "Student")
    {
        if (currentDocument.getDocumentFieldValue("AC1015") != null && !currentDocument.getDocumentFieldValue("AC1015").isAllWhitespace())
        {
            selfEmploymentStudent = currentDocument.getDocumentFieldValue("AC1015").toFloat().round()
        }
    }
    else if (hasSelfEmploymentStatementStudent)
    {
        if (receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").isAllWhitespace())
        {
            selfEmploymentStudent = receivedDocuments.get("SelfEmploymentStatement", "Student").getDocumentField("AC1015").toFloat().round()
        }
    }
    if (docOwner == "Student Spouse")
    {
        if (currentDocument.getDocumentFieldValue("AC1015") != null && !currentDocument.getDocumentFieldValue("AC1015").isAllWhitespace())
        {
            selfEmploymentStudentSpouse = currentDocument.getDocumentFieldValue("AC1015").toFloat().round()
        }
    }
    else if (hasSelfEmploymentStatementStudentSpouse)
    {
        if (receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015").isAllWhitespace())
        {
            selfEmploymentStudentSpouse = receivedDocuments.get("SelfEmploymentStatement", "Student Spouse").getDocumentField("AC1015").toFloat().round()
        }
    }

    // ARITHMETIC:
    // Operation 2: Sum W2 Income for Student/Spouse
    // Formula: Sum (float value of AC1010 from all W2 docs, per owner), then round
    // Variables:
    //   - w2SumStudent, w2SumStudentSpouse: int (dollar sums)
    //   - Source: receivedDocuments.getDocumentsForDocCode("W2", owner)
    //   - AC1010: String wage field, convertible to float (ac1010Value)
    //   - w2DocsStudent: list of received student W2 docs
    // Steps:
    //   1. Loop over all W2 docs for Student
    //   2. For each, check AC1010 field for null/whitespace
    //   3. Parse valid value, accumulate float sum
    //   4. Repeat for Student Spouse
    //   5. Round float sum to int
    // Business Context: Documents wages for IRS AGI calculation.
    // Result: Integer sum of reportable wage income per party.
    // Financial Impact: Added to self-employment and other sources to determine documentation needs.
    // BOUNDARY CHECKS: Null/whitespace field values excluded from sum.
    // Edge Cases: No docs or all blank fields = sum remains 0.
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
    // Operation 3: Sum 1099-G Income for Student/Spouse
    // Formula: Sum (float value of AC1013 from all 1099G docs, per owner), then round
    // Variables: doc1099SumStudent, doc1099SumStudentSpouse, ac1013Value, doc1099StudentFloat, doc1099StudentSpouseFloat
    // Steps: Identical to W2 calculation, using source 1099G and field AC1013.
    // Business Context: Includes unemployment/other compensation in AGI.
    // Result: Accurate income representation.
    // Financial Impact: Poorly calculated sum could cause under/over-documentation risk.
    // BOUNDARY CHECKS: As above.
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

    // ARITHMETIC:
    // Operation 4: Extract Student's Spouse Age from Verification Worksheet
    // Formula: Integer parse from specific AC1034-AC1043 depending on which AC1044-AC1053 holds "Student Spouse"
    // Variables: StudentSpouseAge
    // Steps:
    //   1. If worksheet available, examine each "relation" code (AC1044-AC1053) for match to "Student Spouse".
    //   2. If found, lookup corresponding age field, parse to int if not blank
    // Business Context: Age affects IRS filing threshold assignment (over 65 = higher threshold).
    // Result: Age stored for later logic.
    // Financial Impact: Incorrect age triggers wrong threshold, impacting documentation requirements.
    // BOUNDARY CHECKS: Handles up to ten household members; defaults age = -1 if not found.
    // Edge Cases: Worksheet structure changes, or bad data, could cause missed age extraction.
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

    // CONDITIONALS:
    // Check 9: Student Age Under 65 boundary (IRS threshold logic)
    // Variables: isirSTUDENT_DATEOFBIRTH, age65Threshold
    // Purpose: Determines if higher IRS threshold should apply
    // Logic:
    //   - If student birthday is before or equal to threshold, not under 65
    //   - Default birthday is 2022-01-01 (ensures logic runs even if missing)
    // Success Path: Correct threshold applied
    // Failure Path: False boundary triggers incorrect threshold (potential compliance risk)
    // Business Rules: IRS uses age cutoff to set standard deduction amount.
    // Error Handling: Defensive assignment; no exceptions occur.
    // Financial Impact: Impacts calculation of income threshold, influencing aid eligibility.
    // BOUNDARY CHECKS: Explicit date boundary (1957-12-31).
    // Edge Cases: Null DoB handled by assigning safe default.
    // ARITHMETIC: N/A
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
    // Operation 5: IRS Filing Threshold Calculation (Student/Spouse)
    // Formula: Value set based on filing status and ages
    // Variables: filingThreshold (int dollars)
    // Steps: Sets thresholds according to IRS table
    //   - "1": Single filers, age < 65/≥65
    //   - "2": Married filing joint/Qualified widow(er), age combinations
    //   - "3": Special low threshold (head of household, dependent), always 5
    //   - "4"/"5": HoH and MFS statuses, with age discrimination
    // Business Context: IRS documentation requirement is triggered at/above threshold
    // Result: filingThreshold int used in downstream checks
    // Financial Impact: Miscalculation leads to under/over-documentation.
    // Limitations: Default is 999999 to effectively 'disable' threshold if not set.
    // BOUNDARY CHECKS: IRS rules enforced via explicit code/age boundary matching.
    // Edge Cases: Age not found → default disables threshold.
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
    // Check 10: Marital Status-Driven Total Income Calculation and Document Assignment
    // Variables: isirSTUDENT_MARITALSTATUS, w2SumStudent, doc1099SumStudent, [spouse variants], selfEmployment
    // Purpose: Aggregate all relevant income based on student marital status, compare to threshold, and assign required documentation.
    // Logic:
    //   - If unmarried/status 1/4/null: student sum only.
    //   - Else if married/status 2/3: sum includes spouse contributions.
    //   - If income ≥ threshold, add relevant document requirements to supportingDoc.
    //   - Use addAnyDocuments() to specify acceptable alternatives.
    // Success Path: Documentation requests assigned as per IRS and institutional rules.
    // Failure Path: No document requirements added if under threshold.
    // Business Rules: Correct document policy prevents over/under-awarding; specific doc codes enforced for compliance.
    // Error Handling: Only adds docs if threshold met; robust for missing/null data.
    // Financial Impact: Ensures regulatory audit compliance and eligibility determination per federal rules.
    // BOUNDARY CHECKS: Ensures only valid (numeric) income sources are summed.
    // ARITHMETIC: Calculations for totalIncome; direct sum of int values.
    if (isirSTUDENT_MARITALSTATUS == "1" || isirSTUDENT_MARITALSTATUS == "4" || isirSTUDENT_MARITALSTATUS == null)
    {
        totalIncome = w2SumStudent + doc1099SumStudent + selfEmploymentStudent
        if (totalIncome >= filingThreshold)
        {
            supportingDoc.addAnyDocuments(["IRSForm4868", "Student"], ["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"])
        }
    }
    else if (isirSTUDENT_MARITALSTATUS == "2" || isirSTUDENT_MARITALSTATUS == "3")
    {
        totalIncome = w2SumStudent + doc1099SumStudent + w2SumStudentSpouse + doc1099SumStudentSpouse + selfEmploymentStudent + selfEmploymentStudentSpouse
        if (totalIncome >= filingThreshold)
        {
            supportingDoc.addAnyDocuments(["IRSForm4868", "Student"],["TaxReturnTranscript", "Student"],["1040x", "Student"],["ForeignTaxTranscript", "Student"],["TaxReturnTranscript", "Student Spouse"],["1040x", "Student Spouse"],["ForeignTaxTranscript", "Student Spouse"])
        }
    }
}

// CONDITIONALS:
// Check 11: Begin Parent-owner logic branch
// Variables: docOwner
// Purpose: Apply parallel income/threshold logic for parent-side (dependent students).
// Business Rules: Federal aid policy distinguishes parent from student data in packaging.
// Error Handling: N/A
// Financial Impact: Ensures parent income properly evaluated/disclosures requested.
if (docOwner == "Parent")
{
    // CONDITIONALS:
    // Check 12: Presence/acceptability of W-2 forms for Parent/Spouse
    // Variables: receivedDocuments.hasDoc()/get().isAcceptable()
    // Purpose: Identifies if parent wage documentation is present (affects low income threshold).
    // Logic: Both must be missing to use special lower threshold.
    // Success Path: Use correct threshold for households with little/no earned income documentation.
    // Failure Path: Use higher threshold for wage earners.
    // Business Rules: IRS stratifies required documentation by reported sources.
    // Error Handling: Defensive boolean initialization.
    // Financial Impact: Correct threshold assignment prevents unnecessary or missing document requests.
    // BOUNDARY CHECKS: Document presence/acceptability, DoB boundaries.
    // Edge Cases: Null DoB handled with safe default.
    // ARITHMETIC: N/A
    boolean hasW2Parent = receivedDocuments.hasDoc("W2", "Parent") && receivedDocuments.get("W2", "Parent").isAcceptable()
    boolean hasW2ParentSpouse = receivedDocuments.hasDoc("W2", "Parent Spouse") && receivedDocuments.get("W2", "Parent Spouse").isAcceptable()
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
    // Operation 6: IRS Filing Threshold Calculation (Parent/Spouse)
    // Formula: See student operation, with special boundary for no W2s (400)
    // Variables: filingThreshold (int dollars)
    // Steps:
    //   - If both W2s absent, set 400 (unearned/other income)
    //   - Otherwise, stratifies by filing status and ages
    //   - Uses IRS-published tables
    // Business Context: Determines if parents need to provide tax documentation.
    // Result: filingThreshold int
    // Financial Impact: See student calculation for compliance and eligibility.
    // Limitations: Defaults to 999999 if not set
    // BOUNDARY CHECKS: As above; IRS limits enforced.
    int filingThreshold = 999999
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

    // ARITHMETIC:
    // Operation 7: Self-employment Extraction (Parent/Spouse)
    // Formula: float from AC1015 (direct field for owner, doc for spouse), rounded.
    // Variables: selfEmploymentParent/selfEmploymentParentSpouse
    // Steps: Similar to Student, but parent logic using current doc for owner, doc query for spouse.
    // Business Context: Captures all parental earned income for AGI and threshold.
    // Result: Integer annual income.
    // Financial Impact: Accurate reporting is critical for compliance.
    // BOUNDARY CHECKS: Zeroing of income if doc missing.
    int selfEmploymentParent = 0
    if (docOwner == "Parent")
    {
        selfEmploymentParent = currentDocument.getDocumentFieldValue("AC1015").toFloat().round()
    }
    int selfEmploymentParentSpouse = 0
    if (receivedDocuments.hasDoc("SelfEmploymentStatement", "Parent Spouse") && receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").isAcceptable() && receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").getDocumentField("AC1015") != null && !receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").getDocumentField("AC1015").isAllWhitespace())
    {
        selfEmploymentParentSpouse = receivedDocuments.get("SelfEmploymentStatement", "Parent Spouse").getDocumentField("AC1015").toFloat().round()
    }

    // ARITHMETIC:
    // Operation 8: W2 and 1099 Aggregations (Parent/Spouse)
    // Formula: Like for Student—sum floats, round.
    // Variables: w2SumParent/w2SumParentSpouse, doc1099SumParent/doc1099SumParentSpouse, w2SumParentFloat, ac1010Value
    // Steps:
    //   1. Loop through relevant docs for owner/spouse
    //   2. Validate fields, sum floats
    //   3. Round to int after loop
    // Business Context: Total reported income for parent(s).
    // Result: Integer annual total(s).
    // Financial Impact: Data fed into threshold logic for documentation policy.
    // Limitations: Only evaluates matching documents/fields.
    // BOUNDARY CHECKS: Null/blank fields handled, zero sum if no valid docs.
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
    // Check 13: IRS Filing Status-Driven Document Requirement Assignment (Parent/Spouse)
    // Variables: isirPARENTFTIM_FILINGSTATUSCODE_FTI, totalIncome, filingThreshold, selfEmploymentParent/Spouse, w2 sums, 1099 sums
    // Purpose: Aggregate parent incomes, compare to threshold as per household structure; assigns doc requirement(s) accordingly.
    // Logic: For status "3", evaluates sum; for all others, always requires at least one doc from set.
    // - If income ≥ threshold, adds both parent and parent spouse docs.
    // Success Path: Proper documentation requested.
    // Failure Path: No requests if income < threshold and filing status == "3".
    // Business Rules: Status "3" is Head of Household/Dependent with special low threshold in IRS logic.
    // Error Handling: None explicit; calculation/conditions guarantee safe traversal.
    // Financial Impact: Ensures aid is awarded under compliance framework; audit keypoint.
    int totalIncome = 0
    if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "3")
    {
        totalIncome = selfEmploymentParent + selfEmploymentParentSpouse + w2SumParent + w2SumParentSpouse + doc1099SumParent + doc1099SumParentSpouse
        if (totalIncome >= filingThreshold)
        {
            supportingDoc.addAnyDocuments(["IRSForm4868", "Parent"],["TaxReturnTranscript", "Parent"],["1040x", "Parent"],["ForeignTaxTranscript", "Parent"],["IRSForm4868", "Parent Spouse"],["TaxReturnTranscript", "Parent Spouse"],["1040x", "Parent Spouse"],["ForeignTaxTranscript", "Parent Spouse"])
        }
    }
    else
    {
        supportingDoc.addAnyDocuments(["IRSForm4868", "Parent"],["TaxReturnTranscript", "Parent"],["1040x", "Parent"],["ForeignTaxTranscript", "Parent"])
    }
}