/**
 * CUSTOMER ATTENTION GUIDE
 * ------------------------
 * Review this file in the following order:
 *
 * 1. Search for `isLoanLimitException()`
 *    This is the main decision point in this script.
 *
 * Practical framing:
 * - Customers are usually not expected to redesign this rule.
 * - The main customer responsibility is to understand what upstream logic sets
 *   the Loan Limit Exception flag that this script depends on.
 *
 * In plain English:
 * - if DSUB has a valid loan limit exception, leave it alone
 * - otherwise, this script does not add any further OB3 limit behavior here and
 *   just returns the DSUB override object unchanged
 */

//file:noinspection UnnecessaryQualifiedReference
@groovy.transform.BaseScript(com.oracle.sfp.scripting.api.sdk.autocomplete.Ob3CriteriaScript)
package CHANGE_ME // this should be updated to your actual package name
import com.oracle.sfp.scripting.api.*
import com.oracle.sfp.scripting.api.util.*
import java.math.RoundingMode

// Convert the generic award override object into the fund-specific view.
awardInfo = awardInfo.asDsub()

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
    final currentLimit = [
            awardInfo.getLifetimeMaximumRemainingEligibility()
    ].min()
    if (disbursedAmount > currentLimit) {
        awardInfo = awardInfo.withLoanLimitException(true)
                .withLifetimeMaximumRemainingEligibility([awardInfo.getLifetimeMaximumRemainingEligibility(), disbursedAmount].max())
    }
}

// SOR CORE SOLUTION - BEGIN
// Other conditions can be added here to hard-disable SOR
final dxsubProrationIsOn = currentFundOutput.isProrated() // this detects any prorated PP in acy
optimizer.setCumulativeGradeLevelLimitApplicable(optimizer.isCumulativeGradeLevelLimitApplicable() && !dxsubProrationIsOn)

final disableSor = dxsubProrationIsOn
final disableSingleTermCap = dxsubProrationIsOn

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
        it.asDxsub()
    }
    final termToFundOutput = ppFundOutputs.collectEntries {
        // WARNING: currently NA term is not finding fund output. To be fixed in the upcoming release.
        // The current script version is working around that
        it.getPeriodId().findTerm([acy])?.with { t ->
            [(t): it]
        } ?: [:]
    } as Map<IAcademicTermPeriodAPI, IAwardOverrideAPI.Dxsub>
    final updateTermToFundOutput = {
        acy.getTerms().forEach { t ->
            optimizers[t]?.getExtraProperty("sorStartFo") ?.with {
                termToFundOutput[t] = (IAwardOverrideAPI.Dxsub) it
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
    final sorPeriod = acy
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

    if (term in ineligibleTerms || isZeroTerm(term)) {
        optimizer.setCumulativeGradeLevelLimitApplicable(false)
    }
    // Terms not covered by the SOR Period or otherwise known to be ineligible (e.g. due to Awarding
    // Closeout) must not be considered in SOR Calculation
    final sorIgnoredTerms = nonSorPeriodTerms + ineligibleTerms
    def sorTerms = acy.getTerms()
            .filter { !(it in sorIgnoredTerms) }
    final attendingSorTerms = sorTerms
            .filter { it.isAttending() }
    final isSingleTermLoan = attendingSorTerms.size() == 1
    final singleTermFactor = isSingleTermLoan ? 0.5 : 1.0
    sorTerms = isSingleTermLoan ? attendingSorTerms : sorTerms
    // Determine what terms are in this SOR assessment - END

    if (term in sorTerms) {
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

        final baseDsubGlSorLimit = awardInfo.getGradeLevelLoanLimit()?.with {
            singleTermFactor * it
        }

        final getAvailableSorGlLimit = { IAcademicTermPeriodAPI t ->
            termToFundOutput[t]?.getGradeLevelLoanLimit()?.with { fullGlLimit ->
                toIntHalfUp(singleTermFactor * fullGlLimit * enrollmentIntensity)
            }
        }

        if (isSingleTermLoan && baseDsubGlSorLimit != null && !disableSingleTermCap) {
            // This will be taken into account by SOR Simulation, concurrently to other constraints
            optimizer.forAllPhases(null, null, null) {
                it.withAcyMaximum(baseDsubGlSorLimit, "DXSUB Single-Term Limit")
            }
        }
        if (sorApplies) {
            final sorTotallyBypassesEnhancedNeedFirstPhase = requiredSummerUnits > 0.0 ||
                    optimizers.any {
                        it.getExtraProperty("sorTotallyBypassesEnhancedNeedFirstPhase") as Boolean
                    }
            final isEnhancedNeedFirstPhase = dxsubIsTwoPhaseCalculation && dxsubPhaseIdx == 0
            final isEnhancedNeedSecondPhase = dxsubIsTwoPhaseCalculation && dxsubPhaseIdx == 1
            final enhancedNeedPostponesSummer = dxsubIsTwoPhaseCalculation && !sorMustZeroActualSummerUnits
            final restoreSummerBackToZero = enhancedNeedPostponesSummer &&
                    !sorTotallyBypassesEnhancedNeedFirstPhase &&
                    isEnhancedNeedFirstPhase &&
                    term.isSummerTrailer()
            if (enhancedNeedPostponesSummer && isEnhancedNeedFirstPhase) {
                optimizer.setEnsureFinalZeroAmountIsIneligible(false)
            }
            final getGivenMaxAmount = { IAcademicTermPeriodAPI t ->
                termToFundOutput[t].with { fo ->
                    if (fo == null || isZeroTerm(t)) {
                        return 0.0
                    }
                    if (enhancedNeedPostponesSummer) {
                        if (sorTotallyBypassesEnhancedNeedFirstPhase) {
                            if (isEnhancedNeedFirstPhase) {
                                if (fo.isEligible() && t.isStandard() && fo.getGivenMaxAmount() == null) {
                                    return 0.0
                                }
                            } else if (isEnhancedNeedSecondPhase) {
                                if (fo.isEligible() && t.isStandard() && fo.getGivenMaxAmount() == 0.0) {
                                    return null
                                }
                            }
                        } else {
                            if (isEnhancedNeedFirstPhase &&
                                    fo.isEligible() &&
                                    t.isSummerTrailer()) {
                                return null
                            }
                        }
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

            // Grade Level Boundary Terms: those that must hard-enforce a SOR-reduced Annual Grade Level Limit
            final firstSorTermForEachGradeLevel = sorTerms
                    .inject([:] as Map<Integer, IAcademicTermPeriodAPI>) { curMap, t ->
                        if (t.isAttending() && curMap[t.getGradeLevel()] == null) {
                            curMap[t.getGradeLevel()] = t
                        }
                        curMap
                    }

            // SOR WHITEBOARD
            // Some variable declarations in a scope that can be shared across closures. Their values
            // will only be known at the end of the SOR Base Eligibility Simulation phase.
            Map<IAcademicTermPeriodAPI, BigDecimal> baseTermEligs = null
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
            final getSorAdjustedTotalElig = { IAcademicTermPeriodAPI t ->
                final acyBaseTotal = baseTermEligs.values().sum(0.0) as BigDecimal
                final acySorTotal = toIntHalfUp(acyBaseTotal * enrollmentIntensity)
                final sorGlLimit = getAvailableSorGlLimit(t)
                [sorGlLimit, acySorTotal].min()
            }.memoize()
            final updateCurSorTargets = {
                curSorTargets.forEach { t, target ->
                    if (target == null) {
                        curSorTargets[t] = 0.0
                    }
                }
                hasSorTargetUpdates = false
                firstSorTermForEachGradeLevel.forEach { gl, boundaryTerm ->
                    final activeTerms = sorTerms.findAll { t ->
                        boundaryTerm.getStartDate() <= t.getStartDate()
                    }
                    final adjFs = getCurSorAdjustmentFactors(activeTerms)
                    final curTotal = curSorTargets.values().sum(0.0) as BigDecimal
                    final remainingFreeTotal = [getSorAdjustedTotalElig(boundaryTerm) - curTotal, 0.0].max()
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
                        .withBlacklisted(/COA/)
                        .withOverride {
                            optimizers[term].setExtraProperty("sorStartFo", it)
                            it
                        }
                        .withOnSolution { ph, sol ->
                            baseTermEligs = solToTermAmounts(sol)
                            updateTermToFundOutput()
                            updateCurSorTargets()
                            log.debug("baseEligs={}", printable(baseTermEligs))
                            return ph.withOverride { ovr ->
                                ovr.asTivLoan()
                                        .withPreAdjustmentTermAward(baseTermEligs[term])
                                        .withAdjustedTermAward(curSorTargets[term])
                                        .withEnrollmentIntensity(enrollmentIntensity)
                            }
                        }
                // Schedule posting SOR constraints to all upcoming Fund Optimization phases
            }.forAllPhases(null, 0, null) {
                // Null values in the constraint declarations below mean they have no effect
                it.withAcyRetroactiveMaximum(getSorAdjustedTotalElig(term),
                        "SOR Total Limit")
                        .withPpMaximum(curSorTargets[term], "SOR PP Limit")
                        .withGiven(isSorTargetFinal[term] ? curSorTargets[term] : null, "SOR Keep Final")
                        .withBlacklisted(/Predef\/Grade Level Limit \(Cumulative\)/)
                        .with {
                            sorMustRespectTermCoa
                                    ? it
                                    : it.withBlacklisted(/Term COA/)
                        }
                // Just in case Award Overriding Criteria fails miserably (with no added phases):
            }.addPhase(highestDeclaredPhaseNumber == null, 1)
                    .addPhase(lastResortSorPhasesStartNo) {
                        it.withOnSolution { ph, sol ->
                            updateCurSorSolution(solToTermAmounts(sol))
                            numberOfSorRetries++
                            final doRetry = hasSorTargetUpdates && numberOfSorRetries <= sorTerms.size()
                            ph.withRetry(doRetry)
                                    .withOverride { ovr ->
                                        restoreSummerBackToZero && !doRetry
                                                ? ovr.withMaxAmount(0.0)
                                                : ovr
                                    }
                        }
                    }
        }
    }
}
// SOR CORE SOLUTION - END

// Return the override object with the delivered OB3 limit values applied.
return awardInfo