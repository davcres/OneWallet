package com.davidcrespo.onewallet.presentation.portfolio.allocation.mapper

import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.allocation.models.ItemsByTypeView

fun ItemsByTypeView.toAllocationInvestmentView(
    currency: CurrencyView,
    displayName: String
): InvestmentView {
    val totalPreviousValue = items.sumOf { it.quantity * it.displayPreviousPrice }

    val changePercent = if (totalPreviousValue != 0.0) {
        ((totalValue - totalPreviousValue) / totalPreviousValue) * 100.0
    } else {
        0.0
    }

    return InvestmentView(
        symbol = displayName,
        name = "",
        quantity = 1.0,
        displayPrice = totalValue,
        displayPreviousPrice = totalPreviousValue,
        originalPrice = totalValue,
        originalPreviousPrice = totalPreviousValue,
        originalCurrency = currency,
        changePercent = changePercent,
        type = type,
        month = 0,
        year = 0
    )
}
