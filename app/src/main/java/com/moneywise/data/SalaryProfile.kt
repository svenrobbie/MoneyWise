package com.moneywise.data

import kotlinx.serialization.Serializable

@Serializable
data class SalaryProfile(
    val hourlyWage: Double = 16.00,
    val hoursPerWeek: Double = 40.0,
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

    val weeklyGross: Double get() = hourlyWage * hoursPerWeek
    val annualGross: Double get() = weeklyGross * 52
    val annualWithHolidayAllowance: Double get() = annualGross * (1 + holidayAllowancePercent / 100)
    val annualWithBonus: Double get() = annualWithHolidayAllowance * (1 + endOfYearBonusPercent / 100)
    val totalAnnualGross: Double get() = annualWithBonus

    val netMonthly: Double get() = taxConfig.calculateNetMonthly(totalAnnualGross)
    val netAnnual: Double get() = netMonthly * 12
    val netHourly: Double get() = netAnnual / (hoursPerWeek * 52)
    val effectiveTaxRate: Double get() = taxConfig.calculateEffectiveRate(totalAnnualGross)
    val monthlyGross: Double get() = totalAnnualGross / 12
}
