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

fun String.toInvestment(): Investment {
    val parts = this.split("|")
    return Investment(
        symbol = parts[0],
        name = parts[1],
        quantity = parts[2].toDoubleOrNull() ?: 0.0,
        price = parts[3].toDoubleOrNull() ?: 0.0,
        previousPrice = parts[4].toDoubleOrNull() ?: 0.0,
        currency = Currency.valueOf(parts[5]),
        type = InvestmentType.valueOf(parts[6]),
        year = parts[7].toIntOrNull() ?: 0,
        month = parts[8].toIntOrNull() ?: 0,
    )
}

fun Investment.toPreference(): String {
    return "$symbol|$name|$quantity|$price|$previousPrice|$currency|$type|$year|$month"
}
