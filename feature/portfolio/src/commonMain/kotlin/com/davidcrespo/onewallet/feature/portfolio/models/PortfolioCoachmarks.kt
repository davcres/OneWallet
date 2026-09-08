package com.davidcrespo.onewallet.feature.portfolio.models

import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.tooltip_add_investment
import com.davidcrespo.onewallet.core.generated.resources.tooltip_allocation
import com.davidcrespo.onewallet.core.generated.resources.tooltip_allocation_graph
import com.davidcrespo.onewallet.core.generated.resources.tooltip_delete_investment
import com.davidcrespo.onewallet.core.generated.resources.tooltip_edit_investment
import com.davidcrespo.onewallet.core.generated.resources.tooltip_history
import com.davidcrespo.onewallet.core.generated.resources.tooltip_portfolio_list
import com.davidcrespo.onewallet.core.generated.resources.tooltip_prices
import com.davidcrespo.onewallet.core.generated.resources.tooltip_prices_list
import com.davidcrespo.onewallet.core.generated.resources.tooltip_positions
import com.davidcrespo.onewallet.core.generated.resources.tooltip_total_balance
import org.jetbrains.compose.resources.StringResource

enum class PortfolioCoachmarks(val tooltip: StringResource, val tab: PortfolioTabs) {
    POSITIONS_TAB(Res.string.tooltip_positions, PortfolioTabs.POSITIONS),
    TOTAL_BALANCE(Res.string.tooltip_total_balance, PortfolioTabs.POSITIONS),
    PORTFOLIO_LIST(Res.string.tooltip_portfolio_list, PortfolioTabs.POSITIONS),
    ALLOCATION_TAB(Res.string.tooltip_allocation, PortfolioTabs.ALLOCATION),
    ALLOCATION_GRAPH(Res.string.tooltip_allocation_graph, PortfolioTabs.ALLOCATION),
    PRICES_TAB(Res.string.tooltip_prices, PortfolioTabs.PRICES),
    PRICES_LIST(Res.string.tooltip_prices_list, PortfolioTabs.PRICES),
    HISTORY_TAB(Res.string.tooltip_history, PortfolioTabs.HISTORY),
    //HISTORY_LIST(Res.string.tooltip_history_list, PortfolioTabs.HISTORY),
    EDIT_INVESTMENT(Res.string.tooltip_edit_investment, PortfolioTabs.POSITIONS),
    DELETE_INVESTMENT(Res.string.tooltip_delete_investment, PortfolioTabs.POSITIONS),
    ADD_INVESTMENT(Res.string.tooltip_add_investment, PortfolioTabs.POSITIONS),
}
