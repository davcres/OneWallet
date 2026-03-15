package com.davidcrespo.onewallet.presentation.portfolio.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.R

enum class PortfolioTabs(@StringRes val title: Int, @StringRes val description: Int, val icon: ImageVector) {
    POSITIONS(R.string.tab_positions, R.string.tab_positions_title, Icons.Default.AccountBalanceWallet),
    ALLOCATION(R.string.tab_allocation, R.string.tab_allocation_title, Icons.Default.PieChart),
    PRICES(R.string.tab_prices, R.string.tab_prices_title, Icons.Default.AttachMoney),
    HISTORY(R.string.tab_history, R.string.history_monthly_title, Icons.Filled.AutoGraph)
}