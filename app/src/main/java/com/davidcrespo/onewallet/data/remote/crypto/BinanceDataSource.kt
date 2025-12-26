package com.davidcrespo.onewallet.data.remote.crypto

import com.davidcrespo.onewallet.data.remote.crypto.models.MarketCryptoResponse
import com.davidcrespo.onewallet.data.remote.crypto.models.toInvestDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto

class BinanceDataSource(private val binanceApiClient: BinanceApiClient) {

    suspend fun getCryptoSymbols(): List<MarketCryptoResponse> {
        return binanceApiClient.getCryptoSymbols()
    }

    suspend fun getCryptoPrice(symbol: String): InvestmentDto {
        return binanceApiClient.getCryptoPrice(symbol).toInvestDto()
    }
}