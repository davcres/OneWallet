package com.davidcrespo.onewallet.data.remote.quefondos

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

class QueFondosDataSource(private val queFondosApiClient: QueFondosApiClient) {

    suspend fun getFundPrice(symbol: String, type: InvestmentType): InvestmentDto? {
        return queFondosApiClient.getFundPrice(symbol, type)
    }
}