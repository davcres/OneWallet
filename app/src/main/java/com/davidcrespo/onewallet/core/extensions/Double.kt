package com.davidcrespo.onewallet.core.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.round(decimals: Int = 2): Double {
    return BigDecimal.valueOf(this).setScale(decimals, RoundingMode.HALF_UP).toDouble()
}

fun Double.toSpanishCsvFormat(): String {
    val symbols = DecimalFormatSymbols(Locale.forLanguageTag("es-ES"))
    // Use up to 8 decimal places for quantity/price, which is usually enough even for crypto
    // and avoids the Excel "long number" issues.
    val df = DecimalFormat("#.########", symbols)
    return df.format(this)
}

fun Double.signPrefix(showSign: Boolean): String {
    if (!showSign) return ""
    return when {
        this > 0.0 -> "+"
        this < 0.0 -> ""
        else -> "±"
    }
}
