package com.davidcrespo.onewallet.domain.repository

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.rate.Rate

interface FinancialRepository {
    suspend fun getInvestmentPrice(symbol: String, type: InvestmentType, name: String = ""): Result<Investment>
    suspend fun getStocksSymbols(exchange: String): Result<List<MarketAsset>>
    suspend fun getCryptosSymbols(allowedCurrencies: Set<String>): Result<List<MarketAsset>>
    suspend fun getUsdEur(): Result<Rate>

    fun getSelectedCurrency(): Currency
    fun setSelectedCurrency(currency: Currency)
}