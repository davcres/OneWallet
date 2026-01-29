package com.davidcrespo.onewallet.presentation.portfolio.models

import androidx.annotation.StringRes
import com.davidcrespo.onewallet.R

enum class PortfolioTab(@StringRes val title: Int) {
    ALLOCATION(R.string.tab_allocation),
    PORTFOLIO(R.string.tab_portfolio),
    PRICES(R.string.tab_prices)
}