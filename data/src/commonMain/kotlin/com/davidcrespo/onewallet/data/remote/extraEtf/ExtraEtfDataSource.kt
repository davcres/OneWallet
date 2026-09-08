package com.davidcrespo.onewallet.data.remote.extraEtf

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.extraEtf.models.toInvestDto

class ExtraEtfDataSource(private val extraEtfApiClient: ExtraEtfApiClient) {

    suspend fun getEtfPrice(symbol: String): InvestmentDto {
        return extraEtfApiClient.getEtfPrice(symbol).toInvestDto()
    }
}