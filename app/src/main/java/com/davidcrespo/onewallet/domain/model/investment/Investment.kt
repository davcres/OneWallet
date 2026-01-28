package com.davidcrespo.onewallet.domain.model.investment

data class Investment(
    val symbol: String,
    val name: String,
    val quantity: Double,
    val price: Double,
    val previousPrice: Double,
    val currency: Currency,
    val type: InvestmentType,
    val year: Int,
    val month: Int
) {

    fun setDate(month: Int, year: Int): Investment {
        return this.copy(month = month, year = year)
    }
}
