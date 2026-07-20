package com.moneywise.data

import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class TaxBracket(
    val from: Double,
    val to: Double = Double.MAX_VALUE,
    val rate: Double
)

@Serializable
data class TaxConfig(
    val name: String,
    val country: String,
    val brackets: List<TaxBracket>,
    val socialSecurityRate: Double = 0.0,
    val generalTaxCredit: Double = 0.0,
    val employmentTaxCredit: Double = 0.0
) {
    fun calculateTax(annualIncome: Double): Double {
        var tax = 0.0
        for (bracket in brackets) {
            if (annualIncome <= bracket.from) break
            val taxableInBracket = if (annualIncome > bracket.to)
                bracket.to - bracket.from
            else
                annualIncome - bracket.from
            tax += taxableInBracket * bracket.rate
        }
        return tax - generalTaxCredit - employmentTaxCredit
    }

    fun calculateEffectiveRate(annualIncome: Double): Double {
        if (annualIncome <= 0) return 0.0
        return calculateTax(annualIncome) / annualIncome
    }

    fun calculateNetMonthly(annualIncome: Double): Double {
        val annualTax = calculateTax(annualIncome)
        val socialSecurity = annualIncome * socialSecurityRate
        return (annualIncome - annualTax - socialSecurity) / 12
    }

    companion object {
        val Netherlands = TaxConfig(
            name = "Nederland (2025)",
            country = "NL",
            brackets = listOf(
                TaxBracket(from = 0.0, to = 75518.0, rate = 0.3693),
                TaxBracket(from = 75518.0, rate = 0.4950)
            ),
            generalTaxCredit = 3362.0,
            employmentTaxCredit = 5532.0
        )

        val Belgium = TaxConfig(
            name = "Belgi\u00eb (2025)",
            country = "BE",
            brackets = listOf(
                TaxBracket(from = 0.0, to = 15820.0, rate = 0.25),
                TaxBracket(from = 15820.0, to = 27920.0, rate = 0.40),
                TaxBracket(from = 27920.0, to = 48320.0, rate = 0.45),
                TaxBracket(from = 48320.0, rate = 0.50)
            ),
            socialSecurityRate = 0.1307
        )

        val Germany = TaxConfig(
            name = "Duitsland (2025)",
            country = "DE",
            brackets = listOf(
                TaxBracket(from = 0.0, to = 12096.0, rate = 0.0),
                TaxBracket(from = 12096.0, to = 17005.0, rate = 0.14),
                TaxBracket(from = 17005.0, to = 66760.0, rate = 0.24),
                TaxBracket(from = 66760.0, to = 277825.0, rate = 0.42),
                TaxBracket(from = 277825.0, rate = 0.45)
            ),
            socialSecurityRate = 0.203
        )

        val UK = TaxConfig(
            name = "Verenigd Koninkrijk (2025)",
            country = "UK",
            brackets = listOf(
                TaxBracket(from = 0.0, to = 12570.0, rate = 0.0),
                TaxBracket(from = 12570.0, to = 50270.0, rate = 0.20),
                TaxBracket(from = 50270.0, to = 125140.0, rate = 0.40),
                TaxBracket(from = 125140.0, rate = 0.45)
            ),
            socialSecurityRate = 0.08
        )

        val Sweden = TaxConfig(
            name = "Zweden (2025)",
            country = "SE",
            brackets = listOf(
                TaxBracket(from = 0.0, to = 598500.0, rate = 0.30),
                TaxBracket(from = 598500.0, rate = 0.52)
            ),
            socialSecurityRate = 0.07
        )

        val Custom = TaxConfig(
            name = "Aangepast",
            country = "XX",
            brackets = listOf(TaxBracket(from = 0.0, rate = 0.25))
        )

        val all = listOf(Netherlands, Belgium, Germany, UK, Sweden, Custom)
    }
}
