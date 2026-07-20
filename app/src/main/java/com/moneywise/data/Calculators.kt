package com.moneywise.data

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

data class WorkTimeResult(
    val hours: Double,
    val minutes: Int,
    val grossHours: Double,
    val grossMinutes: Int,
    val humanReadable: String,
    val humanReadableGross: String,
    val workdays: Double
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

data class PurchaseComparison(
    val emoji: String,
    val label: String,
    val count: Int
)

data class GoalScenario(
    val label: String,
    val annualReturn: Double,
    val monthlyContribution: Double,
    val totalContributed: Double,
    val totalGain: Double,
    val futureValue: Double = 0.0
)

object Calculators {

    fun calculateWorkTime(purchasePrice: Double, profile: SalaryProfile): WorkTimeResult {
        if (profile.hourlyWage <= 0 || profile.netHourly <= 0 || profile.hoursPerWeek <= 0) {
            return WorkTimeResult(0.0, 0, 0.0, 0, "Vul je uurloon en werkweek in", "Vul je uurloon en werkweek in", 0.0)
        }

        val grossHours = purchasePrice / profile.hourlyWage
        val netHours = purchasePrice / profile.netHourly

        val grossH = grossHours.toInt()
        val grossM = ((grossHours - grossH) * 60).toInt()
        val netH = netHours.toInt()
        val netM = ((netHours - netH) * 60).toInt()

        val hoursPerDay = profile.hoursPerWeek / 5
        val workdays = netHours / hoursPerDay

        val humanReadable = when {
            netHours < 1 -> "${netM} minuten"
            netHours < 24 -> "$netH uur en $netM minuten"
            else -> "${String.format("%.1f", workdays)} werkdagen"
        }

        val humanReadableGross = when {
            grossHours < 1 -> "${grossM} minuten"
            grossHours < 24 -> "$grossH uur en $grossM minuten"
            else -> "${String.format("%.1f", grossHours / hoursPerDay)} werkdagen"
        }

        return WorkTimeResult(
            hours = netHours,
            minutes = netM,
            grossHours = grossHours,
            grossMinutes = grossM,
            humanReadable = humanReadable,
            humanReadableGross = humanReadableGross,
            workdays = workdays
        )
    }

    fun getPurchaseComparisons(price: Double): List<PurchaseComparison> {
        val items = listOf(
            PurchaseComparison("\u2615", "kopjes koffie", (price / 3.50).toInt()),
            PurchaseComparison("\ud83e\udd6a", "lunches", (price / 12.0).toInt()),
            PurchaseComparison("\ud83c\udfac", "bioscoopbezoeken", (price / 12.50).toInt()),
            PurchaseComparison("\ud83d\udeb2", "fietstanks benzine", (price / 20.0).toInt()),
            PurchaseComparison("\ud83c\udf55", "pizza\u2019s", (price / 10.0).toInt()),
        )
        return items.filter { it.count > 0 }
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

    fun calculateMonthsToGoal(
        monthlyContribution: Double,
        annualRate: Double,
        targetAmount: Double,
        initialAmount: Double = 0.0
    ): Int? {
        if (monthlyContribution <= 0 || targetAmount <= initialAmount) return null
        val monthlyRate = annualRate / 100 / 12
        var balance = initialAmount
        var months = 0
        while (balance < targetAmount && months < 1200) {
            balance += monthlyContribution
            balance *= (1 + monthlyRate)
            months++
        }
        return if (balance >= targetAmount) months else null
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

    fun calculateGoalScenarios(
        targetAmount: Double,
        years: Int,
        initialAmount: Double = 0.0
    ): List<GoalScenario> {
        if (targetAmount <= 0 || years <= 0) return emptyList()
        val months = years * 12
        val scenarios = listOf(
            GoalScenario("Niet gunstig", 3.0, 0.0, 0.0, 0.0),
            GoalScenario("Redelijk", 7.0, 0.0, 0.0, 0.0),
            GoalScenario("Fantastisch", 10.0, 0.0, 0.0, 0.0)
        )
        return scenarios.map { scenario ->
            val monthlyRate = scenario.annualReturn / 100 / 12
            val monthly = if (monthlyRate > 0) {
                val factor = ((1 + monthlyRate).pow(months) - 1) / monthlyRate
                val futureValueInitial = initialAmount * (1 + monthlyRate).pow(months)
                val remaining = targetAmount - futureValueInitial
                if (remaining > 0) remaining / factor else 0.0
            } else {
                val remaining = targetAmount - initialAmount
                if (remaining > 0) remaining / months else 0.0
            }
            val totalContributed = initialAmount + monthly * months
            val totalGain = targetAmount - totalContributed
            scenario.copy(
                monthlyContribution = monthly,
                totalContributed = totalContributed,
                totalGain = totalGain
            )
        }
    }

    fun calculatePortfolioProjection(
        currentBalance: Double,
        monthlyContribution: Double,
        years: Int
    ): List<GoalScenario> {
        if (years <= 0) return emptyList()
        val months = years * 12
        val scenarios = listOf(
            GoalScenario("Niet gunstig", 3.0, monthlyContribution, 0.0, 0.0),
            GoalScenario("Redelijk", 7.0, monthlyContribution, 0.0, 0.0),
            GoalScenario("Fantastisch", 10.0, monthlyContribution, 0.0, 0.0)
        )
        return scenarios.map { scenario ->
            val monthlyRate = scenario.annualReturn / 100 / 12
            var balance = currentBalance
            repeat(months) {
                balance += monthlyContribution
                balance *= (1 + monthlyRate)
            }
            val totalContributed = currentBalance + monthlyContribution * months
            val totalGain = balance - totalContributed
            scenario.copy(
                totalContributed = totalContributed,
                totalGain = totalGain,
                futureValue = balance
            )
        }
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
