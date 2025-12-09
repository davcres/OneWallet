package com.davidcrespo.onewallet.data.remote

import com.davidcrespo.onewallet.data.models.PriceResponse

class FinancialDataSource(private val apiClient: ApiClient) {

    suspend fun getPrice(symbol: String): PriceResponse {
        return apiClient.getPrice(symbol)
    }
}
