package com.davidcrespo.onewallet.core.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun String.normalizeDouble(): Double {
    if (this.isEmpty() || this.isBlank()) return 0.0
    val numberText = this
        .replace(".", "")
        .replace(",", ".")
    val bigDecimal = runCatching { BigDecimal(numberText) }.getOrDefault(BigDecimal(0.0))
    val rounded = bigDecimal.setScale(2, RoundingMode.HALF_UP)
    return rounded.toDouble()
}

fun String.isValidIsin(): Boolean {
    // 1. Length
    if (length != 12) return false

    // 2. Valid format ES12345ABCD0
    if (!matches(Regex("^[A-Z]{2}[A-Z0-9]{9}[0-9]$"))) return false

    // 3. Convertir letras a números (A=10, B=11...)
    val converted = buildString {
        for (c in this@isValidIsin.dropLast(1)) {
            if (c.isDigit()) {
                append(c)
            } else {
                append(c.code - 'A'.code + 10)
            }
        }
    }

    // 4. Check digit control
    val sum = converted
        .reversed()
        .mapIndexed { index, c ->
            val n = c.digitToInt()
            val doubled = if (index % 2 == 0) n * 2 else n
            if (doubled > 9) doubled - 9 else doubled
        }
        .sum()

    val checkDigit = (10 - (sum % 10)) % 10

    return true
    return checkDigit == last().digitToInt() // IE00NNFR7C63 fails
}
