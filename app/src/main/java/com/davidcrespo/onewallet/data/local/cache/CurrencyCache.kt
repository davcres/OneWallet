package com.davidcrespo.onewallet.data.local.cache

import com.davidcrespo.onewallet.domain.model.investment.Currency

interface CurrencyCache {

    suspend fun getCachedRateIfValid(symbol: String, validCacheHours: Long): Double?
    suspend fun setCachedRate(symbol: String, price: Double)

    fun getSelectedCurrency(): Currency
    fun setSelectedCurrency(currency: Currency)
}
