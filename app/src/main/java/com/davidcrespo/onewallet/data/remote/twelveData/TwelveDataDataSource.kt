package com.davidcrespo.onewallet.data.remote.twelveData

import com.davidcrespo.onewallet.data.remote.twelveData.models.PriceResponse

class TwelveDataDataSource(private val twelveDataApiClient: TwelveDataApiClient) {

    suspend fun getPrice(symbol: String): PriceResponse {
        return twelveDataApiClient.getPrice(symbol)
    }
}
