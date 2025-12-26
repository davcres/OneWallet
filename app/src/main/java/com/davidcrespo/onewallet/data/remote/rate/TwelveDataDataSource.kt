package com.davidcrespo.onewallet.data.remote.rate

import com.davidcrespo.onewallet.data.remote.rate.models.RateResponse

class TwelveDataDataSource(private val twelveDataApiClient: TwelveDataApiClient) {

    suspend fun getUsdEur(): RateResponse {
        return twelveDataApiClient.getUsdEur()
    }
}
