package com.davidcrespo.onewallet.domain.extensions

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
