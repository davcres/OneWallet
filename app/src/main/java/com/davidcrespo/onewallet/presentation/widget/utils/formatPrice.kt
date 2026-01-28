package com.davidcrespo.onewallet.presentation.widget.utils

import com.davidcrespo.onewallet.core.extensions.signPrefix
import com.davidcrespo.onewallet.domain.model.investment.Currency

fun formatPrice(
    value: Double,
    currency: Currency,
    showSign: Boolean
): String {
    val sign = value.signPrefix(showSign)

    return when (currency) {
        Currency.EUR -> "%s%.2f %s".format(sign, value, currency.symbol)
        Currency.USD -> "%s %s%.2f".format(currency.symbol, sign, value)
    }
}
