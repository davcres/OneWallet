package com.davidcrespo.onewallet.feature.widget.utils

import com.davidcrespo.onewallet.core.extensions.signPrefix
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.core.models.CurrencyView

fun formatPrice(
    value: Double,
    currency: CurrencyView,
    showSign: Boolean
): String {
    val sign = value.signPrefix(showSign)

    return when (currency.code) {
        EUR -> "%s%.2f %s".format(sign, value, currency.symbol)
        USD -> "%s %s%.2f".format(currency.symbol, sign, value)
        else -> "%s %s%.2f".format(currency.symbol, sign, value)
    }
}
