package com.davidcrespo.onewallet.data.remote.fund.investing

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto

class InvestingDataSource(private val investingApiClient: InvestingApiClient) {

    suspend fun getFundPrice(symbol: String): InvestmentDto? {
        return investingApiClient.getFundPrice(symbol)
    }
}