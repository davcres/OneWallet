package com.davidcrespo.onewallet.core.models

import androidx.annotation.DrawableRes
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

@DrawableRes
fun InvestmentView.getIconRes(): Int = when (type) {
    InvestmentType.STOCK -> R.drawable.ic_stacked_line_chart
    InvestmentType.CRYPTO -> R.drawable.ic_currency_bitcoin
    InvestmentType.FUND -> R.drawable.ic_account_balance
    InvestmentType.ETF -> R.drawable.ic_query_stats
    InvestmentType.BANK -> R.drawable.ic_savings
    InvestmentType.OTHER -> R.drawable.ic_category
}
