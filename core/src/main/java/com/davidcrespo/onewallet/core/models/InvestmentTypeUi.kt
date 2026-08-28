package com.davidcrespo.onewallet.core.models

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
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.core.R

@get:StringRes
val InvestmentType.titleRes: Int
    get() = when (this) {
        InvestmentType.STOCK -> R.string.asset_stock
        InvestmentType.CRYPTO -> R.string.asset_crypto
        InvestmentType.FUND -> R.string.asset_fund
        InvestmentType.ETF -> R.string.asset_etf
        InvestmentType.BANK -> R.string.asset_bank
        InvestmentType.OTHER -> R.string.asset_other
    }

@get:StringRes
val InvestmentType.subtitleRes: Int
    get() = when (this) {
        InvestmentType.STOCK -> R.string.asset_stock_subtitle
        InvestmentType.CRYPTO -> R.string.asset_crypto_subtitle
        InvestmentType.FUND -> R.string.asset_fund_subtitle
        InvestmentType.ETF -> R.string.asset_etf_subtitle
        InvestmentType.BANK -> R.string.asset_bank_subtitle
        InvestmentType.OTHER -> R.string.asset_other_subtitle
    }

val InvestmentType.color: Color
    get() = when (this) {
        InvestmentType.STOCK -> Color(0xFF2563EB)
        InvestmentType.CRYPTO -> Color(0xFFF59E0B)
        InvestmentType.FUND -> Color(0xFF7C3AED)
        InvestmentType.ETF -> Color(0xFF10B981)
        InvestmentType.BANK -> Color(0xFF64748B)
        InvestmentType.OTHER -> Color(0xFFEC4899)
    }

val InvestmentType.icon: ImageVector
    get() = when (this) {
        InvestmentType.STOCK -> Icons.Outlined.StackedLineChart
        InvestmentType.CRYPTO -> Icons.Outlined.CurrencyBitcoin
        InvestmentType.FUND -> Icons.Outlined.AccountBalance
        InvestmentType.ETF -> Icons.Outlined.QueryStats
        InvestmentType.BANK -> Icons.Outlined.Savings
        InvestmentType.OTHER -> Icons.Outlined.Category
    }
