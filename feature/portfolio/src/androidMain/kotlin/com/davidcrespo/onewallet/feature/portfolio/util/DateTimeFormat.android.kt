package com.davidcrespo.onewallet.feature.portfolio.util

import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

actual fun getMonthDisplayName(month: Int): String {
    return Month.of(month)
        .getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}
