package com.davidcrespo.onewallet.data.remote.twelveData

import com.davidcrespo.onewallet.data.remote.twelveData.models.RateResponse

class TwelveDataDataSource(private val twelveDataApiClient: TwelveDataApiClient) {

    suspend fun getRate(from: String, to: String): RateResponse {
        return twelveDataApiClient.getRate(from, to)
    }
}
