package com.davidcrespo.onewallet.data.remote.twelveData

import com.davidcrespo.onewallet.data.remote.twelveData.models.RateResponse

class TwelveDataDataSource(private val twelveDataApiClient: TwelveDataApiClient) {

    suspend fun getUsdEur(): RateResponse {
        return twelveDataApiClient.getUsdEur()
    }
}
