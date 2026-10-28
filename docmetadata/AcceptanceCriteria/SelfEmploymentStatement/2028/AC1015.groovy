/**
 * Evaluates student- or parent-owned self-employment statements against 2025
 * IRS filing thresholds. It sums accepted self-employment, W-2, and
 * 1099-G income document values for the owner and spouse, then requests acceptable tax
 * documentation when the applicable conditions are met.
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.DocumentMetadataAdditionalResolutionActionScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import org.joda.time.LocalDate

// Filing-threshold evaluation requires an ISIR.
if (isirRecord == null) {
    return
}

// Use the non-FTI filing status when the student FTI value is unavailable.
def isirSTUDENT_MARITALSTATUS = isirRecord.getField(IsirField.STUDENT_MARITALSTATUS).getOrNull()
def isirSTUDENT_DATEOFBIRTH = isirRecord.getField(IsirField.STUDENT_DATEOFBIRTH).getOrNull()
def isirSTUDENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.STUDENTFTIM_FILINGSTATUSCODE_FTI).getOrNull()
if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == null) {
    isirSTUDENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.STUDENT_TAXRETURNFILINGSTATUS).getOrNull()
}

// Use the non-FTI filing status when the parent FTI value is unavailable.
def isirPARENT_DATEOFBIRTH = isirRecord.getField(IsirField.PARENT_DATEOFBIRTH).getOrNull()
def isirPARENTSPOUSE_DATEOFBIRTH = isirRecord.getField(IsirField.PARENTSPOUSE_DATEOFBIRTH).getOrNull()
def isirPARENTFTIM_FILINGSTATUSCODE_FTI =  isirRecord.getField(IsirField.PARENTFTIM_FILINGSTATUSCODE_FTI).getOrNull()
if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == null) {
    isirPARENTFTIM_FILINGSTATUSCODE_FTI = isirRecord.getField(IsirField.PARENT_TAXRETURNFILINGSTATUS).getOrNull()
}

// Route the statement through its first configured owner type.
def docOwner = currentDocument.getDocumentOwnerTypes().get(0)

// Student path: include spouse income document values when applicable.
if (docOwner == "Student")
{
    // Read the spouse age only from an acceptable independent worksheet.
    boolean hasVerificationWorksheetIndStudent = false
    if (receivedDocuments.hasDoc("VW-Ind", "Student") && receivedDocuments.get("VW-Ind", "Student").isAcceptable())
    {
        hasVerificationWorksheetIndStudent = true
    }

    // Accepted statements supply AC1015 amounts for the student and spouse totals.
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

    // Read and round AC1015 self-employment amounts for the student household.
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

    // Sum and round AC1010 wages from all student and spouse W-2 documents.
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

    // Sum and round AC1013 amounts from all student and spouse 1099-G documents.
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

    // Match the spouse household entry to its age on the independent worksheet.
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

    // For 2025 thresholds, birth dates through 1961-01-01 are age 65 or older.
    boolean studentAgeUnder65 = true
    LocalDate age65Threshold = new LocalDate(1961, 01, 01)
    LocalDate studentBirthday = new LocalDate(2022, 01, 01)
    if (isirSTUDENT_DATEOFBIRTH != null)
    {
        studentBirthday = isirSTUDENT_DATEOFBIRTH.secureValue()
    }
    if (studentBirthday <= age65Threshold)
    {
        studentAgeUnder65 = false
    }

    // Select the 2025 filing threshold from filing status and household age.
    int filingThreshold = 999999
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "1" && studentAgeUnder65)
    {
        filingThreshold = 15750
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "1" && !studentAgeUnder65)
    {
        filingThreshold = 17750
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "2" && studentAgeUnder65 && StudentSpouseAge < 65)
    {
        filingThreshold = 31500
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "2" && (studentAgeUnder65 || StudentSpouseAge < 65))
    {
        filingThreshold = 33100
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "2" && !studentAgeUnder65 && StudentSpouseAge >= 65)
    {
        filingThreshold = 34700
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "3")
    {
        filingThreshold = 5
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "4" && studentAgeUnder65)
    {
        filingThreshold = 23625
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "4" && !studentAgeUnder65)
    {
        filingThreshold = 25625
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "5" && studentAgeUnder65)
    {
        filingThreshold = 31500
    }
    if (isirSTUDENTFTIM_FILINGSTATUSCODE_FTI == "5" && !studentAgeUnder65)
    {
        filingThreshold = 33100
    }

    // Request acceptable student tax documents when the applicable household income total meets the threshold.
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

// Parent path: include parent-spouse income document values in the household evaluation.
if (docOwner == "Parent")
{
    // Check acceptable W-2 documents and apply the 2025 age cutoff to each parent.
    boolean hasW2Parent = receivedDocuments.hasDoc("W2", "Parent") && receivedDocuments.get("W2", "Parent").isAcceptable()
    boolean hasW2ParentSpouse = receivedDocuments.hasDoc("W2", "Parent Spouse") && receivedDocuments.get("W2", "Parent Spouse").isAcceptable()
    boolean ParentUnder65 = true
    boolean ParentSpouseUnder65 = true
    LocalDate age65Threshold = new LocalDate(1961, 01, 01)
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

    // With no acceptable parent W-2s, use $400; otherwise select the 2025 threshold by status and age.
    int filingThreshold = 999999
    if (!hasW2Parent && !hasW2ParentSpouse)
    {
        filingThreshold = 400
    }
    else
    {
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "1" && ParentUnder65)
        {
            filingThreshold = 15750
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "1" && !ParentUnder65)
        {
            filingThreshold = 17750
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "2" && ParentUnder65 && ParentSpouseUnder65)
        {
            filingThreshold = 31500
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "2" && (ParentUnder65 || ParentSpouseUnder65))
        {
            filingThreshold = 33100
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "2" && !ParentUnder65 && ParentSpouseUnder65)
        {
            filingThreshold = 34700
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "3")
        {
            filingThreshold = 5
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "4" && ParentUnder65)
        {
            filingThreshold = 23625
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "4" && !ParentUnder65)
        {
            filingThreshold = 25625
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "5" && ParentUnder65)
        {
            filingThreshold = 31500
        }
        if (isirPARENTFTIM_FILINGSTATUSCODE_FTI == "5" && !ParentUnder65)
        {
            filingThreshold = 33100
        }
    }

    // Read and round AC1015 self-employment amounts for parent and parent spouse.
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

    // Sum and round W-2 AC1010 and 1099-G AC1013 values for the parent household.
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

    // Status "3" requests parent/spouse tax documents at the threshold; other statuses always request parent tax documents.
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