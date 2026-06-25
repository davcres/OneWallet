package com.davidcrespo.onewallet.domain.model.investment

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.R

enum class InvestmentType(
    override val id: String,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    override val color: Color,
    override val icon: ImageVector
) : InvestmentAttribute {
    STOCK(
        "stock",
        R.string.asset_stock,
        R.string.asset_stock_subtitle,
        Color(0xFF2563EB),
        Icons.Outlined.StackedLineChart
    ),
    CRYPTO(
        "crypto",
        R.string.asset_crypto,
        R.string.asset_crypto_subtitle,
        Color(0xFFF59E0B),
        Icons.Outlined.CurrencyBitcoin
    ),
    FUND(
        "fund",
        R.string.asset_fund,
        R.string.asset_fund_subtitle,
        Color(0xFF7C3AED),
        Icons.Outlined.AccountBalance
    ),
    ETF(
        "etf",
        R.string.asset_etf,
        R.string.asset_etf_subtitle,
        Color(0xFF10B981),
        Icons.Outlined.QueryStats
    ),
    BANK(
        "bank",
        R.string.asset_bank,
        R.string.asset_bank_subtitle,
        Color(0xFF64748B),
        Icons.Outlined.Savings
    ),
    OTHER(
        "other",
        R.string.asset_other,
        R.string.asset_other_subtitle,
        Color(0xFFEC4899),
        Icons.Outlined.Category
    );

    override val nameRes: Int get() = titleRes
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
