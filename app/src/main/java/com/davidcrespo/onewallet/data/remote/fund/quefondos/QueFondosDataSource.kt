package com.davidcrespo.onewallet.data.remote.fund.quefondos

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto

class QueFondosDataSource(private val queFondosApiClient: QueFondosApiClient) {

    suspend fun getFundPrice(symbol: String): InvestmentDto? {
        return queFondosApiClient.getFundPrice(symbol)
    }
}