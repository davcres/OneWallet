package com.davidcrespo.onewallet.presentation.portfolio.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.R

enum class PortfolioTabs(@StringRes val title: Int, val icon: ImageVector) {
    POSITIONS(R.string.tab_positions, Icons.Default.AccountBalanceWallet),
    ALLOCATION(R.string.tab_allocation, Icons.Default.PieChart),
    PRICES(R.string.tab_prices, Icons.Default.AttachMoney),
    HISTORICAL(R.string.tab_historical, Icons.Filled.AutoGraph)
}