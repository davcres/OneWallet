package com.davidcrespo.onewallet.presentation.portfolio

class CurrencyConverter {

    fun convert(
        amount: Double,
        from: String,
        to: String,
        rate: Double
    ): Double {
        if (from == to) return amount
        return amount * rate
    }
}