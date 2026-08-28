package com.davidcrespo.onewallet.data.remote.binance

import com.davidcrespo.onewallet.data.remote.binance.models.MarketCryptoResponse
import com.davidcrespo.onewallet.data.remote.binance.models.toInvestDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto

class BinanceDataSource(private val binanceApiClient: BinanceApiClient) {

    suspend fun getCryptoSymbols(): List<MarketCryptoResponse> {
        return binanceApiClient.getCryptoSymbols()
    }

    suspend fun getCryptoPrice(symbol: String, name: String = ""): InvestmentDto {
        return binanceApiClient.getCryptoPrice(symbol).toInvestDto(name)
    }
}