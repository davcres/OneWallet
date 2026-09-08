package com.davidcrespo.onewallet.domain.model.market

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

data class MarketAsset(
    val symbol: String,
    val price: Double,
    val currency: Currency,
    val type: InvestmentType,
    val description: String?,
    val figi: String? = null,
    val region: GlobalMarketRegion? = null,
    val stockType: String?
)

fun MarketAsset.toInvestment(type: InvestmentType, year: Int, month: Int) = Investment(
    symbol = symbol,
    name = description.orEmpty(),
    quantity = 0.0,
    price = price,
    previousPrice = 0.0,
    currency = currency,
    type = type,
    year = year,
    month = month
)
