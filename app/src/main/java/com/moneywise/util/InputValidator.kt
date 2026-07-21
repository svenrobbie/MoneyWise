package com.moneywise.util

object InputValidator {
    fun filterDecimal(input: String): String {
        val filtered = input.filter { it.isDigit() || it == '.' }
        val firstDot = filtered.indexOf('.')
        return if (firstDot >= 0) {
            filtered.substring(0, firstDot + 1) + filtered.substring(firstDot + 1).filter { it != '.' }
        } else {
            filtered
        }
    }

    fun filterInteger(input: String): String {
        return input.filter { it.isDigit() }
    }

    fun filterDecimalWithSign(input: String): String {
        val filtered = input.filter { it.isDigit() || it == '.' || (it == '-' && input.indexOf(it) == 0) }
        val firstDot = filtered.indexOf('.')
        return if (firstDot >= 0) {
            filtered.substring(0, firstDot + 1) + filtered.substring(firstDot + 1).filter { it != '.' }
        } else {
            filtered
        }
    }
}
