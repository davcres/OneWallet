package com.davidcrespo.onewallet.core.extensions

import kotlin.math.round

private const val BASE_DECIMAL_MULTIPLIER = 10.0
private const val MAX_CSV_DECIMALS = 8

fun Double.round(decimals: Int = 2): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= BASE_DECIMAL_MULTIPLIER }
    return round(this * multiplier) / multiplier
}

fun Double.toSpanishCsvFormat(): String {
    val str = this.toString()
    val parts = str.split('.')
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1].take(MAX_CSV_DECIMALS).trimEnd('0') else ""
    return if (decimalPart.isEmpty()) integerPart else "$integerPart,$decimalPart"
}

fun Double.signPrefix(showSign: Boolean): String {
    if (!showSign) return ""
    return when {
        this > 0.0 -> "+"
        this < 0.0 -> ""
        else -> "±"
    }
}

expect fun Double.formatTwoDecimals(): String
