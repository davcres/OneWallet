package com.davidcrespo.onewallet.core.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.round(decimals: Int = 2): Double {
    val bigDecimal = BigDecimal(this)
    val rounded = bigDecimal.setScale(2, RoundingMode.HALF_UP)
    return rounded.toDouble()
}

fun Double.signPrefix(showSign: Boolean): String {
    if (!showSign) return ""
    return when {
        this > 0.0 -> "+"
        this < 0.0 -> ""
        else -> "±"
    }
}
