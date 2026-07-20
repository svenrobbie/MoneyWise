package com.moneywise.data

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val code: String,
    val symbol: String,
    val name: String,
    val decimalPlaces: Int = 2
) {
    companion object {
        val EUR = Currency("EUR", "\u20ac", "Euro")
        val USD = Currency("USD", "$", "US Dollar")
        val GBP = Currency("GBP", "\u00a3", "British Pound")
        val SEK = Currency("SEK", "kr", "Swedish Krona", 0)
        val NOK = Currency("NOK", "kr", "Norwegian Krone", 0)
        val DKK = Currency("DKK", "kr", "Danish Krone")
        val CHF = Currency("CHF", "CHF", "Swiss Franc")
        val PLN = Currency("PLN", "z\u0142", "Polish Zloty")
        val TRY = Currency("TRY", "\u20ba", "Turkish Lira", 0)
        val BRL = Currency("BRL", "R$", "Brazilian Real")
        val INR = Currency("INR", "\u20b9", "Indian Rupee", 0)
        val JPY = Currency("JPY", "\u00a5", "Japanese Yen", 0)

        val all = listOf(EUR, USD, GBP, SEK, NOK, DKK, CHF, PLN, TRY, BRL, INR, JPY)

        fun fromCode(code: String): Currency =
            all.find { it.code == code } ?: EUR
    }
}
