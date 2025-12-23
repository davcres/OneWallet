package com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.ui.graphics.vector.ImageVector

enum class AssetType(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
) {
    Stock("Acciones", "Stocks & Equity", Icons.AutoMirrored.Outlined.ShowChart),
    Crypto("Criptomonedas", "Coins & Tokens", Icons.Outlined.CurrencyBitcoin),
    Fund("Fondo / ETF", "Index & Mutuals", Icons.Outlined.PieChartOutline),
    Bank("Banco", "Deposits & Cash", Icons.Outlined.AccountBalance),
}