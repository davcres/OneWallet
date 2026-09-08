package com.davidcrespo.onewallet.feature.portfolio.allocation.models

import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.core.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList

data class ItemsByCategoryView(
    val category: InvestmentCategory,
    val items: ImmutableList<InvestmentView>,
    val totalValue: Double
)
