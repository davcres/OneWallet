package com.davidcrespo.onewallet.core.extensions

fun String.normalizeDouble(): Double? =
    replace(",", ".").toDoubleOrNull()