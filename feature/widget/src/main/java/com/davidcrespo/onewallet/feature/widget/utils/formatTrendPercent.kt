package com.davidcrespo.onewallet.feature.widget.utils

import com.davidcrespo.onewallet.core.extensions.signPrefix

fun formatTrendPercent(
    pct: Double,
    showSign: Boolean
): String {
    val sign = pct.signPrefix(showSign)
    return "%s%.2f %%".format(sign, pct)
}