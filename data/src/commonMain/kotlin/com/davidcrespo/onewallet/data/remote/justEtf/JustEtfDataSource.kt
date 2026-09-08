package com.davidcrespo.onewallet.data.remote.justEtf

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.justEtf.models.toInvestDto

class JustEtfDataSource(private val justEtfApiClient: JustEtfApiClient) {

    suspend fun getEtfDetail(symbol: String, currency: CurrencyDto): InvestmentDto {
        return justEtfApiClient.getEtfDetail(symbol, currency.code).toInvestDto(currency)
    }

    suspend fun getEtfPrice(symbol: String, currency: CurrencyDto): InvestmentDto {
        return justEtfApiClient.getEtfPrice(symbol, currency.code).toInvestDto(symbol, currency)
    }
}