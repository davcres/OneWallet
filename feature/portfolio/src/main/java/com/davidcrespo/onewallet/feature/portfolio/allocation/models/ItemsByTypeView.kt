package com.davidcrespo.onewallet.feature.portfolio.allocation.models

import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.core.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList

data class ItemsByTypeView(
    val type: InvestmentType,
    val items: ImmutableList<InvestmentView>,
    val totalValue: Double
)