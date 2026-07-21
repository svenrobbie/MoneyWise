package com.moneywise.ui.components

fun blurValue(value: String, isBlurred: Boolean): String {
    return if (isBlurred) "••••" else value
}
