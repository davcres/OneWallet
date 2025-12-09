package com.davidcrespo.onewallet.data.datasource

import com.davidcrespo.onewallet.data.models.PriceResponse
import com.davidcrespo.onewallet.data.remote.ApiClient

class FinancialDataSource {

    suspend fun getPrice(symbol: String): PriceResponse {
        return ApiClient.getPrice(symbol)
    }
}