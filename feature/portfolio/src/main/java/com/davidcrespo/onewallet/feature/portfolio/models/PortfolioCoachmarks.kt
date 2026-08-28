package com.davidcrespo.onewallet.feature.portfolio.models

import androidx.annotation.StringRes
import com.davidcrespo.onewallet.core.R

enum class PortfolioCoachmarks(@StringRes val tooltip: Int, val tab: PortfolioTabs) {
    POSITIONS_TAB(R.string.tooltip_positions, PortfolioTabs.POSITIONS),
    TOTAL_BALANCE(R.string.tooltip_total_balance, PortfolioTabs.POSITIONS),
    PORTFOLIO_LIST(R.string.tooltip_portfolio_list, PortfolioTabs.POSITIONS),
    ALLOCATION_TAB(R.string.tooltip_allocation, PortfolioTabs.ALLOCATION),
    ALLOCATION_GRAPH(R.string.tooltip_allocation_graph, PortfolioTabs.ALLOCATION),
    PRICES_TAB(R.string.tooltip_prices, PortfolioTabs.PRICES),
    PRICES_LIST(R.string.tooltip_prices_list, PortfolioTabs.PRICES),
    HISTORY_TAB(R.string.tooltip_history, PortfolioTabs.HISTORY),
    //HISTORY_LIST(R.string.tooltip_history_list, PortfolioTabs.HISTORY),
    EDIT_INVESTMENT(R.string.tooltip_edit_investment, PortfolioTabs.POSITIONS),
    DELETE_INVESTMENT(R.string.tooltip_delete_investment, PortfolioTabs.POSITIONS),
    ADD_INVESTMENT(R.string.tooltip_add_investment, PortfolioTabs.POSITIONS),
}
