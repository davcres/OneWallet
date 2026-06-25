package com.davidcrespo.onewallet.domain.model.onboarding

import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.MarketType

data class SeedAsset(
        val symbol: String,
        val name: String,
        val type: InvestmentType,
        val category: InvestmentCategory,
        val initialQuantity: Double,
        val marketType: MarketType? = null
    )