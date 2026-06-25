package com.davidcrespo.onewallet.presentation.portfolio.allocation.models

import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList

data class ItemsByCategoryView(
    val category: InvestmentCategory,
    val items: ImmutableList<InvestmentView>,
    val totalValue: Double
)
