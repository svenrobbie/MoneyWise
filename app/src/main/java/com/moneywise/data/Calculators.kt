package com.moneywise.data

import java.text.NumberFormat
import java.util.Locale

data class WorkTimeResult(
    val hours: Double,
    val minutes: Int,
    val grossHours: Double,
    val grossMinutes: Int,
    val humanReadable: String,
    val humanReadableGross: String
)

data class SavingsYear(
    val year: Int,
    val totalBalance: Double,
    val contributed: Double,
    val interestEarned: Double
)

data class SavingsResult(
    val initialAmount: Double,
    val monthlyContribution: Double,
    val annualRate: Double,
    val years: Int,
    val timeline: List<SavingsYear>
) {
    val finalAmount: Double get() = timeline.lastOrNull()?.totalBalance ?: initialAmount
    val totalContributed: Double get() = initialAmount + (monthlyContribution * years * 12)
    val totalInterest: Double get() = finalAmount - totalContributed
}

data class InvestmentYear(
    val year: Int,
    val nominalBalance: Double,
    val realBalance: Double,
    val contributed: Double
)

data class InvestmentResult(
    val initialAmount: Double,
    val monthlyContribution: Double,
    val annualReturn: Double,
    val years: Int,
    val inflationRate: Double,
    val timeline: List<InvestmentYear>
) {
    val finalAmount: Double get() = timeline.lastOrNull()?.nominalBalance ?: initialAmount
    val finalRealAmount: Double get() = timeline.lastOrNull()?.realBalance ?: initialAmount
    val totalContributed: Double get() = initialAmount + (monthlyContribution * years * 12)
    val totalGain: Double get() = finalAmount - totalContributed
    val totalRealGain: Double get() = finalRealAmount - totalContributed
}

object Calculators {

    fun calculateWorkTime(purchasePrice: Double, profile: SalaryProfile): WorkTimeResult {
        val grossHours = purchasePrice / profile.hourlyWage
        val netHours = purchasePrice / profile.netHourly

        val grossH = grossHours.toInt()
        val grossM = ((grossHours - grossH) * 60).toInt()
        val netH = netHours.toInt()
        val netM = ((netHours - netH) * 60).toInt()

        val hoursPerDay = profile.hoursPerWeek / 5

        val humanReadable = when {
            netHours < 1 -> "${netM} minuten"
            netHours < 24 -> "$netH uur en $netM minuten"
            else -> "${(netHours / hoursPerDay).toString().take(4)} werkdagen"
        }

        val humanReadableGross = when {
            grossHours < 1 -> "${grossM} minuten"
            grossHours < 24 -> "$grossH uur en $grossM minuten"
            else -> "${(grossHours / hoursPerDay).toString().take(4)} werkdagen"
        }

        return WorkTimeResult(
            hours = netHours,
            minutes = netM,
            grossHours = grossHours,
            grossMinutes = grossM,
            humanReadable = humanReadable,
            humanReadableGross = humanReadableGross
        )
    }

    fun calculateSavings(
        initialAmount: Double,
        monthlyContribution: Double,
        annualRate: Double,
        years: Int
    ): SavingsResult {
        val monthlyRate = annualRate / 100 / 12
        val timeline = mutableListOf<SavingsYear>()
        var balance = initialAmount
        var totalContributed = initialAmount

        for (year in 1..years) {
            repeat(12) {
                balance += monthlyContribution
                balance *= (1 + monthlyRate)
                totalContributed += monthlyContribution
            }
            timeline.add(SavingsYear(
                year = year,
                totalBalance = balance,
                contributed = totalContributed,
                interestEarned = balance - totalContributed
            ))
        }

        return SavingsResult(
            initialAmount = initialAmount,
            monthlyContribution = monthlyContribution,
            annualRate = annualRate,
            years = years,
            timeline = timeline
        )
    }

    fun calculateInvestment(
        initialAmount: Double,
        monthlyContribution: Double,
        annualReturn: Double,
        years: Int,
        inflationRate: Double = 2.5
    ): InvestmentResult {
        val monthlyReturn = annualReturn / 100 / 12
        val monthlyInflation = inflationRate / 100 / 12
        val timeline = mutableListOf<InvestmentYear>()
        var nominalBalance = initialAmount
        var realBalance = initialAmount
        var totalContributed = initialAmount

        for (year in 1..years) {
            repeat(12) {
                nominalBalance += monthlyContribution
                nominalBalance *= (1 + monthlyReturn)
                realBalance += monthlyContribution
                realBalance *= (1 + monthlyReturn - monthlyInflation)
                totalContributed += monthlyContribution
            }
            timeline.add(InvestmentYear(
                year = year,
                nominalBalance = nominalBalance,
                realBalance = realBalance,
                contributed = totalContributed
            ))
        }

        return InvestmentResult(
            initialAmount = initialAmount,
            monthlyContribution = monthlyContribution,
            annualReturn = annualReturn,
            years = years,
            inflationRate = inflationRate,
            timeline = timeline
        )
    }

    fun formatCurrency(amount: Double, currency: Currency, showDecimals: Boolean = true): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = java.util.Currency.getInstance(currency.code)
        if (!showDecimals) format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun formatCompact(amount: Double, currency: Currency): String {
        return when {
            amount >= 1_000_000 -> "${currency.symbol}${String.format("%.1f", amount / 1_000_000)}M"
            amount >= 1_000 -> "${currency.symbol}${String.format("%.1f", amount / 1_000)}K"
            else -> formatCurrency(amount, currency)
        }
    }
}
