/**
 * CUSTOMER ATTENTION GUIDE
 * ------------------------
 * Review this file in the following order:
 *
 * 1. Search for `isLoanLimitException()`
 *    This is the main decision point in this script.
 *
 * 2. Search for `withMaxAmount(0.0)`
 *    This is the rule that effectively shuts off GPLUS packaging when no loan
 *    limit exception applies.
 *
 * Practical framing:
 * - Customers are usually not expected to redesign this rule.
 * - The main customer responsibility is to understand what upstream logic sets
 *   the Loan Limit Exception flag that this script depends on.
 *
 * In plain English:
 * - if GPLUS has a valid loan limit exception, leave it alone
 * - otherwise, force the GPLUS max amount to zero
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.Ob3CriteriaScript)
package CHANGE_ME // this should be updated to your actual package name

import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import java.math.RoundingMode

// Convert the generic award override object into the GPLUS-specific view.
awardInfo = awardInfo.asGplus()

final acyDisbursedDisbursements = disbursements
        .getFund(fundCode)
        .getInAcademicYear(acyNo)
        .getCancelled(false)
        .getWithStatus("DISBURSED")
        .reach()

// CUSTOMER REVIEW AREA:
// If the upstream process has already determined that the student has a valid
// Loan Limit Exception, this script does not apply the new OB3 limit logic here.
log.debug("awardInfo.isLoanLimitException() = {}", awardInfo.isLoanLimitException())
log.debug("awardInfo.getGradeLevel() = {}", awardInfo.getGradeLevel())

if (!awardInfo.isLoanLimitException()) {

//Determine lifetime maximum remaining eligibility
    awardInfo = awardInfo.withLifetimeMaximumRemainingEligibility(
            dloan.startAggregateLoanLimitCalculationForFunds(["DSUB", "DUNSUB", "GPLUS"])
                    .getRemainingAggregateEligibility(257500.0, dloan.getLifetimeMaximumLoanTotal()))
// If disbursed amount exceeds the new limits we assume this is a mixed Interim
// Exception case and try to keep disbursed disbursements
    final disbursedAmount = acyDisbursedDisbursements
            .getTotalDisbursementAmount()

// DELIVERED DEPARTMENT OF EDUCATION / PRODUCT LOGIC:
// If no loan limit exception exists, GPLUS is disabled by forcing the maximum
// award amount to zero.
    return awardInfo = awardInfo.withLoanLimitException(disbursedAmount > 0.0)
            .withMaxAmount(disbursedAmount)
}

// SOR CORE SOLUTION - BEGIN
// Other conditions can be added here to hard-disable SOR
final disableSor = false

final sorCalculationConfig = [
        includedCourseStatuses: [
                "Projected",
                "Scheduled",
                "Enrolled",
                "Passed",
                "Failed",
                "Incomplete"
        ],

//      Either use includedCourseStatuses or excludedCourseStatuses:
//        excludedCourseStatuses: [
//                "Withdrawn"
//        ],

]

final solToTermAmounts = { IOptimizationSolutionAPI sol ->
    acy.getTerms().collectEntries { t ->
        [(t): sol.getTotalAmount(t)]
    } as Map<IAcademicTermPeriodAPI, BigDecimal>
}

if (term) {
    final isLthtTerm = { IAcademicTermPeriodAPI t ->
        t.getStatus() == "LESS_THAN_HALF_TIME"
    }
    final isSapIneligibleTerm = { IAcademicTermPeriodAPI t ->
        sap.getLatestStatus(t) == "FD_FINANCIAL_AID_DISQUALIFICATION"
    }
    // Access to each PP/Term Fund Output
    final ppFundOutputs = currentFundOutput.collect {
        it.asGplus()
    }
    final termToFundOutput = ppFundOutputs.collectEntries {
        // WARNING: currently NA term is not finding fund output. To be fixed in the upcoming release.
        // The current script version is working around that
        it.getPeriodId().findTerm([acy])?.with { t ->
            [(t): it]
        } ?: [:]
    } as Map<IAcademicTermPeriodAPI, IAwardOverrideAPI.Gplus>
    final updateTermToFundOutput = {
        acy.getTerms().forEach { t ->
            optimizers[t]?.getExtraProperty("sorStartFo")?.with {
                termToFundOutput[t] = (IAwardOverrideAPI.Gplus) it
            }
        }
    }
    final isZeroTerm = { IAcademicTermPeriodAPI t ->
        t.isNotAttending() || isLthtTerm(t)
    }
    final isZeroEligibleTerm = { IAcademicTermPeriodAPI t ->
        isZeroTerm(t) && termToFundOutput[t].with { fo ->
            fo != null
                    ? fo.isEligible()
                    : !isSapIneligibleTerm(t)
        }
    }

    // Determine what terms are in this SOR assessment - BEGIN
    final sorPeriod = plusLoan instanceof IActualPlusLoanAPI
            ? (IPeriod) plusLoan
            : term
    final nonSorPeriodTerms = acy.getTerms()
            .not { it.getOverlappingWith(sorPeriod) }
            .toSet()
    final ineligibleTerms = acy.getTerms().findAll { t ->
        /** {@link IAwardOverrideAPI#withEligible} can be leveraged in awardOverridingCriteria script
         * to include/exclude terms in SOR Calculation **/
        termToFundOutput[t].with { fo ->
            fo != null
                    ? !fo.isEligible()
                    : !isZeroEligibleTerm(t)
        }
    }

    // Terms not covered by the SOR Period or otherwise known to be ineligible (e.g. due to Awarding
    // Closeout) must not be considered in SOR Calculation
    final sorIgnoredTerms = nonSorPeriodTerms + ineligibleTerms
    def sorTerms = acy.getTerms()
            .filter { !(it in sorIgnoredTerms) }
    final attendingSorTerms = sorTerms
            .filter { it.isAttending() }
    final isSingleTermLoan = attendingSorTerms.size() == 1
    sorTerms = isSingleTermLoan ? attendingSorTerms : sorTerms
    // Determine what terms are in this SOR assessment - END

    if (term in sorTerms) {
        final sorMustIgnoreFollowingFunds = optimizers.collectMany {
            (it.getExtraProperty("sorMustIgnoreFollowingFunds") as List<String>) ?: []
        }
        final sorMustIgnoreFund = { String fc ->
            sorMustIgnoreFollowingFunds.any { ignoredFc ->
                fc.startsWith(ignoredFc)
            }
        }
        final sorMustRespectTermCoa = optimizers.any {
            it.getExtraProperty("sorMustRespectTermCoa") as Boolean
        }
        final sorMustZeroActualSummerUnits = optimizers.any {
            it.getExtraProperty("sorMustZeroActualSummerUnits") as Boolean
        }
        final sorMustConsiderTermFullTime = { IAcademicTermPeriodAPI t ->
            optimizers[t]?.getExtraProperty("sorMustConsiderThisTermFullTime") as Boolean
        }
        final aydUnitsOverride = optimizers.collect {
            it.getExtraProperty("sorAydUnits") as BigDecimal
        }.max()
        final summerOnlyLoan = !sorTerms.any { it.isStandard() }
        final nOfStandardTerms = acy.getTerms()
                .findAll { it.isStandard() }
                .size()
        final fullTimeStandardTermUnits = nOfStandardTerms == 0
                ? 0.0
                : (aydUnitsOverride ?: program.getAydUnits()) / nOfStandardTerms
        final requiredSummerUnits = (optimizer.getExtraProperty("requiredSummerFullTimeCredits") ?: 0.0) as BigDecimal
        final fullTimeSummerTermUnits = requiredSummerUnits ?:
                (summerOnlyLoan ? fullTimeStandardTermUnits : 0.0)

        final sorTermToFullUnits = sorTerms.collectEntries { t ->
            [(t): t.isStandard()
                    ? fullTimeStandardTermUnits
                    : fullTimeSummerTermUnits]
        } as Map<IAcademicTermPeriodAPI, BigDecimal>
        final fullTimeSorPeriodUnits = sorTermToFullUnits.values().sum(0.0) as BigDecimal

        def shouldIncludeCourse = { ICourseAPI c ->
            if (sorCalculationConfig.includedCourseStatuses != null) {
                return sorCalculationConfig.includedCourseStatuses.any { includedStatus ->
                    includedStatus.equalsIgnoreCase(c.getSchedulingStatus())
                }
            }
            if (sorCalculationConfig.excludedCourseStatuses != null) {
                return sorCalculationConfig.excludedCourseStatuses.any { excludedStatus ->
                    excludedStatus.equalsIgnoreCase(c.getSchedulingStatus())
                }
            }
            true
        }
        final sorTermToActualUnits = sorTerms.collectEntries { t ->
            final givenUnits = (optimizers[t]?.getExtraProperty("sorThisTermActualUnits") as BigDecimal)
            final actualTermUnits = sorMustZeroActualSummerUnits && t.isSummerTrailer()
                    ? 0.0
                    : sorMustConsiderTermFullTime(t)
                    ? sorTermToFullUnits[t]
                    : givenUnits != null
                    ? givenUnits
                    : program.getCourses()
                    .getAssociatedTo(t)
                    .findAll(shouldIncludeCourse)
                    .collect { it.getUnits() }
                    .sum(0.0) as BigDecimal
            [(t): actualTermUnits]
        } as Map<IAcademicTermPeriodAPI, BigDecimal>
        final actualSorPeriodUnits = sorTermToActualUnits.values().sum(0.0) as BigDecimal

        // Basic math helpers
        final roundFactor = { BigDecimal f -> (100.0 * f).setScale(0, RoundingMode.HALF_UP) / 100.0 }
        final toIntHalfUp = { BigDecimal v -> v.setScale(0, RoundingMode.HALF_UP) }
        final toSafeFactor = { BigDecimal v -> [1.0, v].min() }
        final safeDiv = { BigDecimal num, BigDecimal den -> num / (den ?: 1.0) }

        // Returns the ACTUAL SOR Factor for each term: the ratio ActualNumberOfUnitsForThisTerm/fullTimeSorPeriodUnits.
        // This is used to calculate Enrollment Intensity.
        final sorTermToActualSorFactor = sorTerms.collectEntries { t ->
            [(t): safeDiv(sorTermToActualUnits[t], fullTimeSorPeriodUnits)]
        } as Map<IAcademicTermPeriodAPI, BigDecimal>
        final totalActualSorFactor = sorTermToActualSorFactor.values().sum(0.0) as BigDecimal

        final printable = { Map<IAcademicTermPeriodAPI, ? extends Object> m ->
            m.collectEntries {
                [(it.getKey().getNumber()): it.getValue()]
            }
        }
        log.debug("fullUnits={}, actualUnits={}", printable(sorTermToFullUnits), printable(sorTermToActualUnits))

        final enrollmentIntensity = toSafeFactor(
                roundFactor(safeDiv(actualSorPeriodUnits, fullTimeSorPeriodUnits)))
        final sorApplies = !disableSor && enrollmentIntensity < 1.0

        final higherPrioGplus = financialPlan.filterFunds { fc -> fc.startsWith("GPLUS") }
                .getInAcademicYear(acyNo)
                .getOverlappingWith(sorTerms)
                .reach()
        final higherPrioGplusFos = higherPrioGplus.collect { fo ->
            fo.asGplus()
        }
        final thisTermHigherPrioGplusTotal = higherPrioGplus.getOverlappingWith(term).getMaxAmount()
        if (sorApplies) {
            optimizer.setFundingForPeriodFilter { fc, ftype ->
                !fc.startsWith("GPLUS") && !sorMustIgnoreFund(fc)
            }
            final getGivenMaxAmount = { IAcademicTermPeriodAPI t ->
                termToFundOutput[t].with { fo ->
                    if (fo == null || isZeroTerm(t)) {
                        return 0.0
                    }
                    return fo.getGivenMaxAmount()
                }
            }
            awardInfo = awardInfo.withMaxAmount(getGivenMaxAmount(term))

            final getDisbursedOrGivenAmount = { IAcademicTermPeriodAPI t ->
                getGivenMaxAmount(t).with { given ->
                    given != null
                            ? given
                            : acyDisbursedDisbursements
                            .getOverlappingWith(t)
                            .with {
                                isEmpty()
                                        ? null
                                        : getTotalDisbursementAmount()
                            }
                }
            }
            final isTermDisbursedOrGiven = { IAcademicTermPeriodAPI t ->
                getDisbursedOrGivenAmount(t) != null
            }

            // SOR WHITEBOARD
            // Some variable declarations in a scope that can be shared across closures. Their values
            // will only be known at the end of the SOR Base Eligibility Simulation phase.
            Map<IAcademicTermPeriodAPI, BigDecimal> baseTermEligs = null
            final baseAcyElig = {
                baseTermEligs.values().sum(0.0) as BigDecimal
            }
            final thisTermUnpackagedAmount = {
                [0.0,
                 baseTermEligs[term] - thisTermHigherPrioGplusTotal
                ].max()
            }.memoize()
            final curSorTargets = sorTerms.collectEntries { t ->
                getDisbursedOrGivenAmount(t).with { nullableFinalStartingVal ->
                    [(t): nullableFinalStartingVal]
                }
            } as Map<IAcademicTermPeriodAPI, BigDecimal>
            final isSorTargetFinal = sorTerms.collectEntries { t ->
                [(t): curSorTargets[t] != null]
            }
            def hasSorTargetUpdates = true
            def numberOfSorRetries = 0
            final getCurSorAdjustmentFactors = { Iterable<IAcademicTermPeriodAPI> selectedTerms ->
                final ignoreTerm = { IAcademicTermPeriodAPI t ->
                    isSorTargetFinal[t] || !(t in selectedTerms)
                }
                final frozenTotalSorFactor = sorTermToActualSorFactor.collect { t2f ->
                    ignoreTerm(t2f.getKey())
                            ? t2f.getValue()
                            : 0.0
                }.sum(0.0) as BigDecimal

                // Determine the total "free" SOR factor. This will be the actual denominator for
                // proportional value calculations:
                final freeTotalSorFactor = (totalActualSorFactor - frozenTotalSorFactor)
                // Key point - this is the actual SOR Factor to be applied to this term's base eligibility:
                sorTermToActualSorFactor.collectEntries { t2f ->
                    final t = t2f.getKey()
                    final adjF = ignoreTerm(t)
                            ? null
                            : safeDiv(t2f.getValue(), freeTotalSorFactor)
                    [(t): adjF]
                } as Map<IAcademicTermPeriodAPI, BigDecimal>
            }
            final getSorAdjustedTotalElig = {
                final acySorTotal = toIntHalfUp(baseAcyElig() * enrollmentIntensity)
                [0.0,
                 [acySorTotal - higherPrioGplus.getMaxAmount(),
                  awardInfo.getGraduatePlusLoanLimit()
                 ].min()
                ].max()
            }.memoize()
            final updateCurSorTargets = {
                curSorTargets.forEach { t, target ->
                    if (target == null) {
                        curSorTargets[t] = 0.0
                    }
                }
                hasSorTargetUpdates = false
                final activeTerms = sorTerms
                final adjFs = getCurSorAdjustmentFactors(activeTerms)
                final curTotal = curSorTargets.values().sum(0.0) as BigDecimal
                final remainingFreeTotal = [getSorAdjustedTotalElig() - curTotal, 0.0].max()
                activeTerms.forEach { t ->
                    adjFs[t].with { adjF ->
                        if (adjF != null) {
                            final increase = toIntHalfUp(adjF * remainingFreeTotal)
                            if (increase > 0.0) {
                                hasSorTargetUpdates = true
                                curSorTargets[t] += increase
                            }
                        }
                    }
                }
            }
            final updateCurSorSolution = { Map<IAcademicTermPeriodAPI, BigDecimal> sol ->
                sol.forEach { t, curVal ->
                    if (!isSorTargetFinal[t] && curVal < curSorTargets[t]) {
                        curSorTargets[t] = curVal
                        isSorTargetFinal[t] = true
                    }
                }
                updateCurSorTargets()
            }
            final preSorPhaseNo = -1
            final lastResortSorPhasesStartNo = highestDeclaredPhaseNumber == null
                    ? 2
                    : highestDeclaredPhaseNumber + 1
            // A "rehearsal" Fund Optimization phase is set up before any phase declared in awardOverridingScript
            // has the ability to run (hence -1 as phase number). This phase runs under selected conditions (a
            // subset of predefined constraints can be disabled and hard-set values are unset when possible)
            // and its output result is rolled back. However, it allows determining the base eligibility values
            // SOR constraints can be built from.
            optimizer.addPhase(preSorPhaseNo) {
                it.withName("SOR Base Eligibility Simulation")
                        .withBlacklisted(/PLUS Loan Limit|Acy COA/)
                        .withOverride { IAwardOverrideAPI.Gplus fo ->
                            optimizers[term].setExtraProperty("sorStartFo", fo)
                            fo
                        }
                        .withOnSolution { ph, sol ->
                            baseTermEligs = solToTermAmounts(sol)
                            updateTermToFundOutput()
                            updateCurSorTargets()
                            log.debug("baseEligs={}", printable(baseTermEligs))
                            return ph.withOverride { ovr ->
                                ovr.asTivLoan()
                                        .withPreAdjustmentTermAward(thisTermUnpackagedAmount())
                                        .withAdjustedTermAward(curSorTargets[term])
                                        .withEnrollmentIntensity(enrollmentIntensity)
                            }
                        }
                // Schedule posting SOR constraints to all upcoming Fund Optimization phases
            }.forAllPhases(null, 0, null) {
                // Null values in the constraint declarations below mean they have no effect
                it.withAcyMaximum(getSorAdjustedTotalElig(),
                        "SOR Total Limit")
                        .withPpMaximum(curSorTargets[term], "SOR PP Limit")
                        .withGiven(isSorTargetFinal[term] ? curSorTargets[term] : null, "SOR Keep Final")
                        .with {
                            sorMustRespectTermCoa
                                    ? it.withPpMaximum(thisTermUnpackagedAmount(), "SOR Term COA")
                                    : it.withBlacklisted(/Term COA/)
                        }
                // Just in case Award Overriding Criteria fails miserably (with no added phases):
            }.addPhase(highestDeclaredPhaseNumber == null, 1)
                    .addPhase(lastResortSorPhasesStartNo) {
                        it.withOnSolution { ph, sol ->
                            updateCurSorSolution(solToTermAmounts(sol))
                            numberOfSorRetries++
                            ph.withRetry(hasSorTargetUpdates && numberOfSorRetries <= sorTerms.size())
                        }
                    }
        }
    }
}
// SOR CORE SOLUTION - END

// Return the override object with the delivered OB3 limit values applied.
return awardInfo