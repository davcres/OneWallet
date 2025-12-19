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
}