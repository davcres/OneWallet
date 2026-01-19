package com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.R

enum class AssetType(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val icon: ImageVector,
) {
    Stock(R.string.asset_stock, R.string.asset_stock_subtitle, Icons.Outlined.StackedLineChart),
    Crypto(R.string.asset_crypto, R.string.asset_crypto_subtitle, Icons.Outlined.CurrencyBitcoin),
    Fund(R.string.asset_fund, R.string.asset_fund_subtitle, Icons.Outlined.PieChartOutline),
    ETF(R.string.asset_etf, R.string.asset_etf_subtitle, Icons.Outlined.PieChartOutline),
    Bank(R.string.asset_bank, R.string.asset_bank_subtitle, Icons.Outlined.AccountBalance),
}