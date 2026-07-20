package com.moneywise.data

import kotlinx.serialization.Serializable

@Serializable
data class SalaryProfile(
    val hourlyWage: Double = 16.00,
    val hoursPerWeek: Double = 40.0,
    val age: Int = 30,
    val holidayAllowancePercent: Double = 8.0,
    val atvPercent: Double = 0.0,
    val verlofPercent: Double = 0.0,
    val endOfYearBonusPercent: Double = 0.0,
    val overtimePercent: Double = 0.0,
    val weekendPercent: Double = 0.0,
    val nightPercent: Double = 0.0,
    val shiftPercent: Double = 0.0,
    val includeHolidayAllowance: Boolean = true,
    val includeAtv: Boolean = true,
    val includeVerlof: Boolean = true,
    val includeOvertime: Boolean = true,
    val includeWeekend: Boolean = true,
    val includeNight: Boolean = true,
    val includeShift: Boolean = true,
    val deductionName1: String = "",
    val deductionPercent1: Double = 0.0,
    val deductionEnabled1: Boolean = true,
    val deductionName2: String = "",
    val deductionPercent2: Double = 0.0,
    val deductionEnabled2: Boolean = true,
    val deductionName3: String = "",
    val deductionPercent3: Double = 0.0,
    val deductionEnabled3: Boolean = true,
    val extraName1: String = "",
    val extraAmount1: Double = 0.0,
    val extraEnabled1: Boolean = true,
    val extraName2: String = "",
    val extraAmount2: Double = 0.0,
    val extraEnabled2: Boolean = true,
    val extraName3: String = "",
    val extraAmount3: Double = 0.0,
    val extraEnabled3: Boolean = true,
    val taxConfigName: String = "Nederland (2025)",
    val currencyCode: String = "EUR",
    val paymentFrequency: String = "monthly"
) {
    val is4Weekly: Boolean get() = paymentFrequency == "4weekly"
    val paymentPeriodsPerYear: Int get() = if (is4Weekly) 13 else 12
    val paymentPeriodLabel: String get() = if (is4Weekly) "per 4 weken" else "per maand"
    val taxConfig: TaxConfig
        get() = TaxConfig.all.find { it.name == taxConfigName } ?: TaxConfig.Netherlands

    val currency: Currency
        get() = Currency.fromCode(currencyCode)

    val effectiveHourlyWage: Double
        get() {
            var totalPercent = 0.0
            var count = 0
            if (includeOvertime && overtimePercent > 0) { totalPercent += overtimePercent; count++ }
            if (includeWeekend && weekendPercent > 0) { totalPercent += weekendPercent; count++ }
            if (includeNight && nightPercent > 0) { totalPercent += nightPercent; count++ }
            if (includeShift && shiftPercent > 0) { totalPercent += shiftPercent; count++ }
            val avgPremium = if (count > 0) totalPercent / count else 0.0
            return hourlyWage * (1 + avgPremium / 100)
        }

    val weeklyGross: Double get() = effectiveHourlyWage * hoursPerWeek
    val annualGross: Double get() = weeklyGross * 52

    val annualWithHolidayAllowance: Double
        get() = if (includeHolidayAllowance)
            annualGross * (1 + holidayAllowancePercent / 100)
        else annualGross

    val annualWithAtv: Double
        get() = if (includeAtv)
            annualWithHolidayAllowance * (1 + atvPercent / 100)
        else annualWithHolidayAllowance

    val annualWithVerlof: Double
        get() = if (includeVerlof)
            annualWithAtv * (1 + verlofPercent / 100)
        else annualWithAtv

    val annualWithBonus: Double
        get() = annualWithVerlof * (1 + endOfYearBonusPercent / 100)

    val totalAnnualGross: Double get() = annualWithBonus

    val totalDeductionPercent: Double
        get() {
            var total = 0.0
            if (deductionEnabled1) total += deductionPercent1
            if (deductionEnabled2) total += deductionPercent2
            if (deductionEnabled3) total += deductionPercent3
            return total
        }

    val annualDeductions: Double get() = totalAnnualGross * totalDeductionPercent / 100

    val annualExtras: Double
        get() {
            var total = 0.0
            if (extraEnabled1) total += extraAmount1
            if (extraEnabled2) total += extraAmount2
            if (extraEnabled3) total += extraAmount3
            return total
        }

    val taxableIncome: Double get() = (totalAnnualGross - annualDeductions).coerceAtLeast(0.0)

    val netMonthly: Double get() = taxConfig.calculateNetMonthly(taxableIncome, annualExtras)
    val netPerPeriod: Double get() = netMonthly * 12 / paymentPeriodsPerYear
    val netAnnual: Double get() = netMonthly * 12
    val netHourly: Double get() = if (hoursPerWeek * 52 > 0) netAnnual / (hoursPerWeek * 52) else 0.0
    val effectiveTaxRate: Double
        get() {
            if (totalAnnualGross <= 0) return 0.0
            val totalDeductions = taxConfig.calculateTax(taxableIncome) + (taxableIncome * taxConfig.socialSecurityRate)
            val effectiveRate = (totalDeductions - annualExtras) / totalAnnualGross
            return effectiveRate.coerceIn(0.0, 1.0)
        }
    val monthlyGross: Double get() = totalAnnualGross / 12
    val grossPerPeriod: Double get() = totalAnnualGross / paymentPeriodsPerYear

    val yearsUntilRetirement: Int
        get() {
            val retirementAge = when {
                taxConfig.country == "NL" -> 67
                taxConfig.country == "BE" -> 67
                taxConfig.country == "DE" -> 67
                taxConfig.country == "UK" -> 67
                taxConfig.country == "SE" -> 65
                else -> 67
            }
            return (retirementAge - age).coerceAtLeast(0)
        }

    val retirementAge: Int
        get() = when (taxConfig.country) {
            "SE" -> 65
            else -> 67
        }
}
