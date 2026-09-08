package com.davidcrespo.onewallet.core.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.asset_bank
import com.davidcrespo.onewallet.core.generated.resources.asset_bank_subtitle
import com.davidcrespo.onewallet.core.generated.resources.asset_crypto
import com.davidcrespo.onewallet.core.generated.resources.asset_crypto_subtitle
import com.davidcrespo.onewallet.core.generated.resources.asset_etf
import com.davidcrespo.onewallet.core.generated.resources.asset_etf_subtitle
import com.davidcrespo.onewallet.core.generated.resources.asset_fund
import com.davidcrespo.onewallet.core.generated.resources.asset_fund_subtitle
import com.davidcrespo.onewallet.core.generated.resources.asset_other
import com.davidcrespo.onewallet.core.generated.resources.asset_other_subtitle
import com.davidcrespo.onewallet.core.generated.resources.asset_stock
import com.davidcrespo.onewallet.core.generated.resources.asset_stock_subtitle
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import org.jetbrains.compose.resources.StringResource

val InvestmentType.titleResource: StringResource
    get() = when (this) {
        InvestmentType.STOCK -> Res.string.asset_stock
        InvestmentType.CRYPTO -> Res.string.asset_crypto
        InvestmentType.FUND -> Res.string.asset_fund
        InvestmentType.ETF -> Res.string.asset_etf
        InvestmentType.BANK -> Res.string.asset_bank
        InvestmentType.OTHER -> Res.string.asset_other
    }

val InvestmentType.subtitleResource: StringResource
    get() = when (this) {
        InvestmentType.STOCK -> Res.string.asset_stock_subtitle
        InvestmentType.CRYPTO -> Res.string.asset_crypto_subtitle
        InvestmentType.FUND -> Res.string.asset_fund_subtitle
        InvestmentType.ETF -> Res.string.asset_etf_subtitle
        InvestmentType.BANK -> Res.string.asset_bank_subtitle
        InvestmentType.OTHER -> Res.string.asset_other_subtitle
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
