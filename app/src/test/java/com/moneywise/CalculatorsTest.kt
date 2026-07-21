package com.moneywise

import com.moneywise.data.*
import com.moneywise.util.InputValidator
import org.junit.Assert.*
import org.junit.Test

class CalculatorsTest {

    @Test
    fun `work time calculation - basic`() {
        val profile = SalaryProfile(hourlyWage = 20.0, hoursPerWeek = 40.0)
        val result = Calculators.calculateWorkTime(100.0, profile)
        assertTrue("Hours should be > 0", result.hours > 0)
        assertTrue("Workdays should be > 0", result.workdays > 0)
    }

    @Test
    fun `work time - less than an hour`() {
        val profile = SalaryProfile(hourlyWage = 50.0, hoursPerWeek = 40.0)
        val result = Calculators.calculateWorkTime(5.0, profile)
        assertTrue("Hours should be < 1", result.hours < 1.0)
        assertTrue(result.humanReadable.contains("minuten"))
    }

    @Test
    fun `work time - large purchase shows days`() {
        val profile = SalaryProfile(hourlyWage = 20.0, hoursPerWeek = 40.0)
        val result = Calculators.calculateWorkTime(1000.0, profile)
        assertTrue("Should show workdays for large amounts", result.humanReadable.contains("werkdagen"))
    }

    @Test
    fun `savings calculation - zero interest`() {
        val result = Calculators.calculateSavings(
            initialAmount = 1000.0,
            monthlyContribution = 100.0,
            annualRate = 0.0,
            years = 1
        )
        assertEquals(2200.0, result.finalAmount, 0.01)
        assertEquals(2200.0, result.totalContributed, 0.01)
        assertEquals(0.0, result.totalInterest, 0.01)
    }

    @Test
    fun `savings calculation - with interest`() {
        val result = Calculators.calculateSavings(
            initialAmount = 10000.0,
            monthlyContribution = 500.0,
            annualRate = 5.0,
            years = 10
        )
        assertTrue(result.finalAmount > result.totalContributed)
        assertTrue(result.totalInterest > 0)
    }

    @Test
    fun `investment calculation - nominal vs real`() {
        val result = Calculators.calculateInvestment(
            initialAmount = 10000.0,
            monthlyContribution = 500.0,
            annualReturn = 7.0,
            years = 20,
            inflationRate = 2.5
        )
        assertTrue(result.finalAmount > result.finalRealAmount)
        assertTrue(result.finalAmount > result.totalContributed)
    }

    @Test
    fun `months to goal - reachable`() {
        val months = Calculators.calculateMonthsToGoal(
            monthlyContribution = 500.0,
            annualRate = 5.0,
            targetAmount = 10000.0
        )
        assertNotNull(months)
        assertTrue(months!! > 0)
        assertTrue(months < 24)
    }

    @Test
    fun `months to goal - not reachable with zero contribution`() {
        val months = Calculators.calculateMonthsToGoal(
            monthlyContribution = 0.0,
            annualRate = 5.0,
            targetAmount = 10000.0
        )
        assertNull(months)
    }

    @Test
    fun `purchase comparisons - filter zeros`() {
        val comparisons = Calculators.getPurchaseComparisons(2.0)
        assertTrue(comparisons.all { it.count > 0 })
    }

    @Test
    fun `purchase comparisons - has items for large price`() {
        val comparisons = Calculators.getPurchaseComparisons(100.0)
        assertTrue(comparisons.size >= 3)
    }
}

class TaxConfigTest {

    @Test
    fun `Netherlands tax - low income has zero tax`() {
        val tax = TaxConfig.Netherlands.calculateTax(20000.0)
        assertEquals(0.0, tax, 1.0)
    }

    @Test
    fun `Netherlands tax - never negative`() {
        val tax = TaxConfig.Netherlands.calculateTax(5000.0)
        assertTrue("Tax should not be negative, was: $tax", tax >= 0.0)
    }

    @Test
    fun `Netherlands tax - higher income has higher tax`() {
        val taxLow = TaxConfig.Netherlands.calculateTax(30000.0)
        val taxHigh = TaxConfig.Netherlands.calculateTax(80000.0)
        assertTrue(taxHigh > taxLow)
    }

    @Test
    fun `Belgium tax - social security applied`() {
        val net = TaxConfig.Belgium.calculateNetMonthly(40000.0 * 12)
        assertTrue("Net should be positive", net > 0)
        assertTrue("Net should be less than gross", net < 40000.0)
    }

    @Test
    fun `effective rate is between 0 and 1`() {
        for (income in listOf(10000.0, 30000.0, 60000.0, 100000.0)) {
            val rate = TaxConfig.Netherlands.calculateEffectiveRate(income)
            assertTrue("Rate $rate should be >= 0", rate >= 0.0)
            assertTrue("Rate $rate should be <= 1", rate <= 1.0)
        }
    }

    @Test
    fun `custom flat tax works`() {
        val tax = TaxConfig.Custom.calculateTax(100000.0)
        assertEquals(25000.0, tax, 1.0)
    }
}

class SalaryProfileTest {

    @Test
    fun `effective hourly wage includes premiums`() {
        val base = SalaryProfile(hourlyWage = 20.0, overtimePercent = 50.0)
        assertTrue(base.effectiveHourlyWage > 20.0)
    }

    @Test
    fun `years until retirement calculated correctly`() {
        val young = SalaryProfile(age = 25)
        assertEquals(42, young.yearsUntilRetirement)

        val old = SalaryProfile(age = 66)
        assertEquals(1, old.yearsUntilRetirement)

        val retired = SalaryProfile(age = 70)
        assertEquals(0, retired.yearsUntilRetirement)
    }

    @Test
    fun `zero hourly wage does not crash`() {
        val profile = SalaryProfile(hourlyWage = 0.0, hoursPerWeek = 40.0)
        assertEquals(0.0, profile.netHourly, 0.01)
    }

    @Test
    fun `holiday allowance is excluded when toggle is off`() {
        val with = SalaryProfile(hourlyWage = 20.0, hoursPerWeek = 40.0, holidayAllowancePercent = 8.0, includeHolidayAllowance = true)
        val without = SalaryProfile(hourlyWage = 20.0, hoursPerWeek = 40.0, holidayAllowancePercent = 8.0, includeHolidayAllowance = false)
        assertTrue("With HA should be higher", with.totalAnnualGross > without.totalAnnualGross)
    }

    @Test
    fun `atv and verlof increase annual gross`() {
        val base = SalaryProfile(hourlyWage = 20.0, hoursPerWeek = 40.0)
        val withAtv = SalaryProfile(hourlyWage = 20.0, hoursPerWeek = 40.0, atvPercent = 5.0, includeAtv = true)
        val withVerlof = SalaryProfile(hourlyWage = 20.0, hoursPerWeek = 40.0, verlofPercent = 3.0, includeVerlof = true)
        assertTrue("ATV should increase", withAtv.totalAnnualGross > base.totalAnnualGross)
        assertTrue("Verlof should increase", withVerlof.totalAnnualGross > base.totalAnnualGross)
    }

    @Test
    fun `premiums excluded when toggles are off`() {
        val profile = SalaryProfile(
            hourlyWage = 20.0,
            hoursPerWeek = 40.0,
            overtimePercent = 50.0,
            weekendPercent = 25.0,
            nightPercent = 50.0,
            shiftPercent = 30.0,
            includeOvertime = false,
            includeWeekend = false,
            includeNight = false,
            includeShift = false
        )
        assertEquals("Effective wage should equal base", 20.0, profile.effectiveHourlyWage, 0.01)
    }
}

class InputValidatorTest {

    @Test
    fun `filterDecimal allows digits and dot`() {
        assertEquals("123.45", InputValidator.filterDecimal("123.45"))
    }

    @Test
    fun `filterDecimal removes letters`() {
        assertEquals("1234", InputValidator.filterDecimal("12a3b4"))
    }

    @Test
    fun `filterDecimal allows only one dot`() {
        assertEquals("12.34", InputValidator.filterDecimal("12.3.4"))
    }

    @Test
    fun `filterDecimal handles empty input`() {
        assertEquals("", InputValidator.filterDecimal(""))
    }

    @Test
    fun `filterDecimal handles dot only`() {
        assertEquals(".", InputValidator.filterDecimal("."))
    }

    @Test
    fun `filterDecimal handles leading dot`() {
        assertEquals(".5", InputValidator.filterDecimal(".5"))
    }

    @Test
    fun `filterInteger removes all non-digits`() {
        assertEquals("1234", InputValidator.filterInteger("12a3.4b"))
    }

    @Test
    fun `filterInteger handles empty input`() {
        assertEquals("", InputValidator.filterInteger(""))
    }

    @Test
    fun `filterDecimalWithSign allows leading minus`() {
        assertEquals("-12.5", InputValidator.filterDecimalWithSign("-12.5"))
    }

    @Test
    fun `filterDecimalWithSign removes mid-string minus`() {
        assertEquals("125", InputValidator.filterDecimalWithSign("1-25"))
    }

    @Test
    fun `filterDecimalWithSign handles multiple dots`() {
        assertEquals("-1.25", InputValidator.filterDecimalWithSign("-1.2.5"))
    }
}

class GoalScenarioTest {

    @Test
    fun `calculateGoalScenarios returns 3 scenarios`() {
        val scenarios = Calculators.calculateGoalScenarios(100000.0, 20)
        assertEquals(3, scenarios.size)
    }

    @Test
    fun `calculateGoalScenarios higher return needs less monthly`() {
        val scenarios = Calculators.calculateGoalScenarios(100000.0, 20)
        assertTrue("Niet gunstig needs most", scenarios[0].monthlyContribution >= scenarios[1].monthlyContribution)
        assertTrue("Redelijk needs most", scenarios[1].monthlyContribution >= scenarios[2].monthlyContribution)
    }

    @Test
    fun `calculateGoalScenarios zero target returns empty`() {
        val scenarios = Calculators.calculateGoalScenarios(0.0, 20)
        assertTrue(scenarios.isEmpty())
    }

    @Test
    fun `calculateGoalScenarios zero years returns empty`() {
        val scenarios = Calculators.calculateGoalScenarios(100000.0, 0)
        assertTrue(scenarios.isEmpty())
    }

    @Test
    fun `calculatePortfolioProjection returns 3 scenarios`() {
        val scenarios = Calculators.calculatePortfolioProjection(10000.0, 500.0, 10)
        assertEquals(3, scenarios.size)
    }

    @Test
    fun `calculatePortfolioProjection future value increases with return`() {
        val scenarios = Calculators.calculatePortfolioProjection(10000.0, 500.0, 10)
        assertTrue(scenarios[0].futureValue <= scenarios[1].futureValue)
        assertTrue(scenarios[1].futureValue <= scenarios[2].futureValue)
    }

    @Test
    fun `calculatePortfolioProjection zero years returns empty`() {
        val scenarios = Calculators.calculatePortfolioProjection(10000.0, 500.0, 0)
        assertTrue(scenarios.isEmpty())
    }
}

class FormatCurrencyTest {

    @Test
    fun `formatCurrency returns non-empty string`() {
        val result = Calculators.formatCurrency(1234.56, Currency.EUR)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatCurrency with no decimals`() {
        val result = Calculators.formatCurrency(1234.56, Currency.EUR, showDecimals = false)
        assertFalse(result.contains(",56"))
    }

    @Test
    fun `formatCompact thousands`() {
        val result = Calculators.formatCompact(5000.0, Currency.EUR)
        assertTrue(result.contains("K"))
    }

    @Test
    fun `formatCompact millions`() {
        val result = Calculators.formatCompact(2_500_000.0, Currency.EUR)
        assertTrue(result.contains("M"))
    }

    @Test
    fun `formatCompact small amount`() {
        val result = Calculators.formatCompact(50.0, Currency.EUR)
        assertFalse(result.contains("K"))
        assertFalse(result.contains("M"))
    }
}

class CurrencyTest {

    @Test
    fun `fromCode EUR`() {
        assertEquals(Currency.EUR, Currency.fromCode("EUR"))
    }

    @Test
    fun `fromCode unknown returns EUR`() {
        assertEquals(Currency.EUR, Currency.fromCode("XYZ"))
    }

    @Test
    fun `all currencies have symbol`() {
        Currency.all.forEach { currency ->
            assertTrue("${currency.code} should have symbol", currency.symbol.isNotEmpty())
        }
    }

    @Test
    fun `all currencies have name`() {
        Currency.all.forEach { currency ->
            assertTrue("${currency.code} should have name", currency.name.isNotEmpty())
        }
    }
}

class EdgeCaseTest {

    @Test
    fun `work time with zero wage returns placeholder`() {
        val profile = SalaryProfile(hourlyWage = 0.0, hoursPerWeek = 40.0)
        val result = Calculators.calculateWorkTime(100.0, profile)
        assertEquals(0.0, result.hours, 0.01)
    }

    @Test
    fun `work time with zero hours per week returns placeholder`() {
        val profile = SalaryProfile(hourlyWage = 20.0, hoursPerWeek = 0.0)
        val result = Calculators.calculateWorkTime(100.0, profile)
        assertEquals(0.0, result.hours, 0.01)
    }

    @Test
    fun `savings with zero initial and zero monthly`() {
        val result = Calculators.calculateSavings(0.0, 0.0, 5.0, 10)
        assertEquals(0.0, result.finalAmount, 0.01)
    }

    @Test
    fun `investment with zero contribution`() {
        val result = Calculators.calculateInvestment(10000.0, 0.0, 7.0, 10)
        assertTrue(result.finalAmount > 10000.0)
        assertEquals(10000.0, result.totalContributed, 0.01)
    }

    @Test
    fun `months to goal already reached`() {
        val months = Calculators.calculateMonthsToGoal(100.0, 5.0, 50.0, 100.0)
        assertNotNull(months)
        assertEquals(0, months)
    }

    @Test
    fun `purchase comparisons with zero price`() {
        val comparisons = Calculators.getPurchaseComparisons(0.0)
        assertTrue(comparisons.isEmpty())
    }
}
