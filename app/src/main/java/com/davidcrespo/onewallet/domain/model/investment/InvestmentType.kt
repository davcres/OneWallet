package com.davidcrespo.onewallet.domain.model.investment

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.R

enum class InvestmentType(
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val color: Color,
    val icon: ImageVector
) {
    STOCK(R.string.asset_stock, R.string.asset_stock_subtitle, Color(0xFF3B82F6), Icons.Outlined.StackedLineChart),
    CRYPTO(R.string.asset_crypto, R.string.asset_crypto_subtitle, Color(0xFFF59E0B), Icons.Outlined.CurrencyBitcoin),
    FUND(R.string.asset_fund, R.string.asset_fund_subtitle, Color(0xFF8B5CF6), Icons.Outlined.PieChartOutline),
    ETF(R.string.asset_etf, R.string.asset_etf_subtitle, Color(0xFF10B981), Icons.Outlined.PieChartOutline),
    BANK(R.string.asset_bank, R.string.asset_bank_subtitle, Color(0xFF64748B), Icons.Outlined.AccountBalance),
    OTHER(R.string.asset_other, R.string.asset_other_subtitle, Color(0xFFEC4899), Icons.Outlined.Payments),
}

private val MARKET_TYPES = setOf(
    InvestmentType.STOCK,
    InvestmentType.CRYPTO,
    InvestmentType.FUND,
    InvestmentType.ETF,
)

private val MANUAL_TYPES = setOf(
    InvestmentType.BANK,
    InvestmentType.OTHER,
)

private val ISIN_TYPES = setOf(
    InvestmentType.FUND,
    InvestmentType.ETF,
)

fun InvestmentType.isMarket(): Boolean = this in MARKET_TYPES
fun InvestmentType.isManual(): Boolean = this in MANUAL_TYPES
fun InvestmentType.hasIsin(): Boolean = this in ISIN_TYPES
