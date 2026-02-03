package com.davidcrespo.onewallet.presentation.portfolio.allocation.models

import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList

data class ItemsByTypeView(
    val type: InvestmentType,
    val items: ImmutableList<InvestmentView>,
    val totalValue: Double
)