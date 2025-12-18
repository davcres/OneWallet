package com.davidcrespo.onewallet.data.remote.twelveData

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.twelveData.models.RateResponse
import com.davidcrespo.onewallet.data.remote.twelveData.models.toInvestDto

class TwelveDataDataSource(private val twelveDataApiClient: TwelveDataApiClient) {

    suspend fun getCryptoPrice(symbol: String): InvestmentDto {
        return twelveDataApiClient.getCryptoPrice(symbol).toInvestDto(symbol)
    }

    suspend fun getUsdEur(): RateResponse {
        return twelveDataApiClient.getUsdEur()
    }
}
