package com.davidcrespo.onewallet.feature.portfolio.util

import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

private const val MAX_MONTH_INDEX = 11

actual fun getMonthDisplayName(month: Int): String {
    val formatter = NSDateFormatter().apply {
        locale = NSLocale.currentLocale
    }
    @Suppress("UNCHECKED_CAST")
    val symbols = formatter.monthSymbols as? List<String>
    val index = (month - 1).coerceIn(0, MAX_MONTH_INDEX)
    val name = symbols?.getOrNull(index) ?: "Month $month"
    return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
