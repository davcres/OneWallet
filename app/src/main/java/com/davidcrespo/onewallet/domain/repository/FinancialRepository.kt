package com.davidcrespo.onewallet.domain.repository

import com.davidcrespo.onewallet.domain.model.Price

interface FinancialRepository {
    suspend fun getPrice(): Result<Price>
}
