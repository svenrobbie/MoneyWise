package com.moneywise.data

import kotlinx.serialization.Serializable

@Serializable
data class SalaryProfile(
    val hourlyWage: Double = 16.00,
    val hoursPerWeek: Double = 40.0,
    val age: Int = 30,
    val holidayAllowancePercent: Double = 8.0,
    val endOfYearBonusPercent: Double = 0.0,
    val overtimePercent: Double = 0.0,
    val weekendPercent: Double = 0.0,
    val nightPercent: Double = 0.0,
    val shiftPercent: Double = 0.0,
    val taxConfigName: String = "Nederland (2025)",
    val currencyCode: String = "EUR"
) {
    val taxConfig: TaxConfig
        get() = TaxConfig.all.find { it.name == taxConfigName } ?: TaxConfig.Netherlands

    val currency: Currency
        get() = Currency.fromCode(currencyCode)

    val effectiveHourlyWage: Double
        get() {
            val base = hourlyWage
            val avgPremium = (overtimePercent + weekendPercent + nightPercent + shiftPercent) / 4.0
            return base * (1 + avgPremium / 100)
        }

    val weeklyGross: Double get() = effectiveHourlyWage * hoursPerWeek
    val annualGross: Double get() = weeklyGross * 52
    val annualWithHolidayAllowance: Double get() = annualGross * (1 + holidayAllowancePercent / 100)
    val annualWithBonus: Double get() = annualWithHolidayAllowance * (1 + endOfYearBonusPercent / 100)
    val totalAnnualGross: Double get() = annualWithBonus

    val netMonthly: Double get() = taxConfig.calculateNetMonthly(totalAnnualGross)
    val netAnnual: Double get() = netMonthly * 12
    val netHourly: Double get() = if (hoursPerWeek * 52 > 0) netAnnual / (hoursPerWeek * 52) else 0.0
    val effectiveTaxRate: Double get() = taxConfig.calculateEffectiveRate(totalAnnualGross)
    val monthlyGross: Double get() = totalAnnualGross / 12

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
