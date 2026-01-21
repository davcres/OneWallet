package com.davidcrespo.onewallet.data.remote.etf.justEtf

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.etf.justEtf.models.toInvestDto
import com.davidcrespo.onewallet.domain.model.investment.Currency

class JustEtfDataSource(private val justEtfApiClient: JustEtfApiClient) {

    suspend fun getEtfDetail(symbol: String, currency: Currency): InvestmentDto {
        return justEtfApiClient.getEtfDetail(symbol, currency.name).toInvestDto(currency)
    }

    suspend fun getEtfPrice(symbol: String, currency: Currency): InvestmentDto {
        return justEtfApiClient.getEtfPrice(symbol, currency.name).toInvestDto(symbol, currency)
    }
}