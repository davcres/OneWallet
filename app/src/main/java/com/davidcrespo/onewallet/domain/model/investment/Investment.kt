package com.davidcrespo.onewallet.domain.model.investment

data class Investment(
    val symbol: String,
    val quantity: Double,
    val price: Double,
    val previousPrice: Double,
    val currency: Currency,
    val type: InvestmentType,
    val year: Int,
    val month: Int
) {

    fun setNewPrice(newPrice: Double): Investment {
        return this.copy(price = newPrice)
    }

    fun setDate(month: Int, year: Int): Investment {
        return this.copy(month = month, year = year)
    }

    companion object {
        fun fromCache(symbol: String, price: Double, type: InvestmentType) = Investment(
            symbol = symbol,
            quantity = 0.0,
            price = price,
            previousPrice = 0.0,
            currency = Currency.USD,
            type = type,
            year = 0,
            month = 0
        )
    }
}