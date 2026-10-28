/**
 * CUSTOMER ATTENTION GUIDE
 * ------------------------
 * Review this file in the following order:
 *
 * 1. Search for `professionalProgramCipCodes`
 *    This is the main first-release policy hook. Update it if your institution
 *    needs a different definition of which current programs count as professional.
 *
 * 2. Search for `if (awy >= 2027)`
 *    This is the OB3 override section for 2027+.
 *
 * 3. Search for `JTA1`
 *    These are testing placeholders used only to prove that all new grade-level
 *    values 8-13 can be emitted. They should not be treated as a realistic
 *    institutional data model.
 *
 * 4. Everything above the Graduate and Professional Program definitions (ie. professionalProgramCipCodes,
 *    hasProfessionalProgramCipCode, isInGraduateProgram, isInProfessionalProgram)
 *    This is the normal grade-level calculation logic based on units and prior
 *    progress. Most schools will probably leave this alone unless they already
 *    customize grade-level progression heavily.
 *
 * First-release practical intent:
 * for 2026 and prior award years, calculate the normal grade level as before for grade levels 1 through 7
 * for 2027+, apply OB3 override behavior to grade levels 8 through 13 to identify if the student is
 * graduate or professional
 *
 * Future product direction:
 * more advanced prior/concurrent graduate/professional grade level determination logic
 * will be provided in a later release that utilizes OSFA program and NSLDS-driven logic
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.GradeLevelProgressionScript)
package CHANGE_ME // this should be updated to your actual package name
import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*

// Use the term start date for term programs, otherwise the academic-year start.
// This defines the cutoff for which current-year periods count toward progression.
def referenceDate = program.isTerm() ? term.getStartDate() : acy.getStartDate();

// Sum units from academic years before the current one.
// ACTUAL years use completed units; other years use scheduled units.
def totalPrevAcyUnits = acys
        .findAll { it.getNumber() < acyNo }
        .collect { it.getStatus() == "ACTUAL" ? it.getCompletedUnits() : it.getUnits() }
        .sum(0.0);

// Sum units from current academic-year payment periods that:
// - are not canceled
// - started before the reference date
def totalCurAcyUnits = acy.getLoanPaymentPeriods()
        .findAll { it.getStatus() != "CANCELED" && it.getStartDate() < referenceDate }
        .collect { it.getUnits() }
        .sum(0.0);

// Count passed coursework completed before the student's actual program start.
def totalCreditsCompletedBeforeProgramStartDate = program.getCourses()
        .findAll {
            it.getSchedulingStatus() == "PASSED" &&
                    it.getEndDate() < actualStartDate &&
                    (it.getIncompleteResolutionDate() == null || it.getIncompleteResolutionDate() < actualStartDate)
        }
        .collect { it.getUnits() }
        .sum(0.0);

// Total academic progress used for the base grade-level calculation.
def totalCreditUnits = (program.getTotalAcceptedTransferUnits() +
        program.getAssessedUnits() +
        totalPrevAcyUnits +
        totalCurAcyUnits +
        totalCreditsCompletedBeforeProgramStartDate);

// Convert total units into academic-year-equivalent progress.
def creditPerAydUnit = totalCreditUnits / program.getAydUnits();

// Establish the starting grade level before adding progress.
def baseGradeLevel = (program.isGraduate()
        ? 6
        : program.getProgramType() == "CERTIFICATE_POST_BACCALAUREATE"
        ? 5
        : 1);

// Add integer progress to the base grade level.
def unboundedGradeLevel = baseGradeLevel + creditPerAydUnit.intValue();

// Apply the standard upper bound by program type.
def isAssociate = program.getProgramType() == "ASSOCIATE";
def gradeLevelUb = (isAssociate
        ? 2
        : program.isUndergraduate()
        ? 5
        : 7);

// Final pre-OB3 grade level after applying the standard cap.
def gradeLevel = [unboundedGradeLevel, gradeLevelUb].min();

// CUSTOMER REVIEW AREA:
// This is the baseline's first-release way to identify graduate vs professional programs.
// If your institution uses a different rule than CIP codes, update this section.
//
// Note:
// Current delivered logic only distinguishes CURRENT graduate vs CURRENT
// professional program status here. It does not yet implement prior/concurrent
// NSLDS-history-based scenarios.
//
// In this example we are defining whether the student is currently in a graduate program
// by checking their their SAFI's <ProgramType>, or if they are in a professional program by checking
// their SAFI's <ProgramType> and <ProgramCipCode>
def professionalProgramCipCodes = [
        "01.8001",
        "22.0101",
        "39.0602",
        "39.0605",
        "42.2801",
        "42.2803",
        "42.2805",
        "42.2807",
        "42.2810",
        "42.2811",
        "42.2812",
        "42.2899",
        "51.0101",
        "51.0202",
        "51.0203",
        "51.0401",
        "51.0809",
        "51.0912",
        "51.0913",
        "51.1201",
        "51.1202",
        "51.1203",
        "51.1701",
        "51.2001",
        "51.2306",
        "51.2308",
        "51.3801",
        "51.3804",
        "51.3818"
]

def hasProfessionalProgramCipCode = professionalProgramCipCodes.contains(program.getCipCode())
def isInGraduateProgram = program.isGraduate()
def isInProfessionalProgram = (isInGraduateProgram && hasProfessionalProgramCipCode)

// CUSTOMER REVIEW AREA:
// This commented block shows the simpler conceptual first-release business rule:
// - graduate -> 8
// - professional -> 10
// For the first release, this is the active logic.
if (awy >= 2027) {
    if (isInGraduateProgram) {
        gradeLevel = 8
    }
    if (isInProfessionalProgram) {
        gradeLevel = 10
    }
}

// CUSTOMER REVIEW AREA:
// The JTA1/JTA2/JTA3 mapping below is testing logic
// meant to prove that values 8-13 can be output. It is not expected to be the
// realistic long-term institutional model for customers.
//
// In the near term, most schools should focus on:
// - how they classify current programs as graduate vs professional
// - whether they need any institutional specific logic beyond the SAFI to make that decision
/*
OB3 grade-level meanings from ED regulations:
- 8  = Graduate Never Professional
- 9  = Graduate Was Professional
- 10 = Professional Never Graduate
- 11 = Professional Was Graduate
- 12 = Graduate Concurrent Enrollment
- 13 = Professional Concurrent Enrollment
*/
/*if (awy >= 2027) {
    if (isInGraduateProgram && program.getCode() == "JTA1") {
        gradeLevel = 8
    }
    if (isInGraduateProgram && program.getCode() == "JTA2") {
        gradeLevel = 9
    }
    if (isInProfessionalProgram && program.getCode() == "JTA3") {
        gradeLevel = 10
    }
    if (isInProfessionalProgram && program.getCode() == "JTA4") {
        gradeLevel = 11
    }
    if (isInGraduateProgram && program.getCode() == "JTA5") {
        gradeLevel = 12
    }
    if (isInProfessionalProgram && program.getCode() == "JTA6") {
        gradeLevel = 13
    }
}*/

// Future product context only:
// More sophisticated prior/concurrent graduate/professional scenarios will
// likely depend on future OSFA-delivered NSLDS FAH/TSM-based logic. Customers
// can build institutional specific logic themselves if desired.

// Log statement to output values of variables above
log.debug("actualStartDate={}, referenceDate={}, totalPrevAcyUnits={}, totalCurAcyUnits={}, totalCreditsCompletedBeforeProgramStartDate={}, totalCreditUnits={}, creditPerAydUnit={}, baseGradeLevel={}, isAssociate={}, gradeLevelUb={}, gradeLevel={}, hasProfessionalProgramCipCode={}, isInGraduateProgram={}, isInProfessionalProgram={}",
        actualStartDate,
        referenceDate,
        totalPrevAcyUnits,
        totalCurAcyUnits,
        totalCreditsCompletedBeforeProgramStartDate,
        totalCreditUnits,
        creditPerAydUnit,
        baseGradeLevel,
        isAssociate,
        gradeLevelUb,
        gradeLevel,
        hasProfessionalProgramCipCode,
        isInGraduateProgram,
        isInProfessionalProgram
)

// Return the final calculated grade level
return gradeLevel