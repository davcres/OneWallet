package com.davidcrespo.onewallet.data.remote.etf.extraEtf

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto

class ExtraEtfDataSource(private val extraEtfApiClient: ExtraEtfApiClient) {

    suspend fun getEtfPrice(symbol: String): InvestmentDto? {
        return extraEtfApiClient.getEtfPrice(symbol)
    }
}