package com.davidcrespo.onewallet.data.remote.util

fun String.normalizeDouble(): Double {
    val raw = trim()
        .replace("\u00A0", "")
        .replace(" ", "")

    if (raw.isEmpty()) return 0.0

    val lastDot = raw.lastIndexOf('.')
    val lastComma = raw.lastIndexOf(',')

    val normalized = when {
        lastDot >= 0 && lastComma >= 0 -> {
            if (lastComma > lastDot) {
                raw.replace(".", "").replace(",", ".")
            } else {
                raw.replace(",", "")
            }
        }
        lastComma >= 0 -> raw.replace(",", ".")
        else -> raw
    }

    return normalized.toDoubleOrNull() ?: 0.0
}
