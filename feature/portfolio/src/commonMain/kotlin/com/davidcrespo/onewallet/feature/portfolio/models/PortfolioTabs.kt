package com.davidcrespo.onewallet.feature.portfolio.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.history_monthly_title
import com.davidcrespo.onewallet.core.generated.resources.tab_allocation
import com.davidcrespo.onewallet.core.generated.resources.tab_allocation_title
import com.davidcrespo.onewallet.core.generated.resources.tab_history
import com.davidcrespo.onewallet.core.generated.resources.tab_positions
import com.davidcrespo.onewallet.core.generated.resources.tab_positions_title
import com.davidcrespo.onewallet.core.generated.resources.tab_prices
import com.davidcrespo.onewallet.core.generated.resources.tab_prices_title
import org.jetbrains.compose.resources.StringResource

enum class PortfolioTabs(
    val title: StringResource,
    val description: StringResource,
    val icon: ImageVector
) {
    POSITIONS(Res.string.tab_positions, Res.string.tab_positions_title, Icons.Default.AccountBalanceWallet),
    ALLOCATION(Res.string.tab_allocation, Res.string.tab_allocation_title, Icons.Default.PieChart),
    PRICES(Res.string.tab_prices, Res.string.tab_prices_title, Icons.Default.AttachMoney),
    HISTORY(Res.string.tab_history, Res.string.history_monthly_title, Icons.Filled.AutoGraph)
}