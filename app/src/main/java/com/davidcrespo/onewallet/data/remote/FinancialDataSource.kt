package com.davidcrespo.onewallet.data.remote

import com.davidcrespo.onewallet.data.models.PriceResponse


class FinancialDataSource {

    suspend fun getPrice(): PriceResponse {
        return ApiClient.getPrice()
    }
}
