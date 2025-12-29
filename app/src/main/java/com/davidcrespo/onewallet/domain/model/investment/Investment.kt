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

    fun setDate(month: Int, year: Int): Investment {
        return this.copy(month = month, year = year)
    }
}

fun String.toInvestment(): Investment {
    val parts = this.split("|")
    return Investment(
        symbol = parts[0],
        quantity = parts[1].toDoubleOrNull() ?: 0.0,
        price = parts[2].toDoubleOrNull() ?: 0.0,
        previousPrice = parts[3].toDoubleOrNull() ?: 0.0,
        currency = Currency.valueOf(parts[4]),
        type = InvestmentType.valueOf(parts[5]),
        year = parts[6].toIntOrNull() ?: 0,
        month = parts[7].toIntOrNull() ?: 0,
    )
}

fun Investment.toPreference(): String {
    return "$symbol|$quantity|$price|$previousPrice|$currency|$type|$year|$month"
}
