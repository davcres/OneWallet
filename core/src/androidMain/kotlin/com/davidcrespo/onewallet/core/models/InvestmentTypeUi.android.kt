package com.davidcrespo.onewallet.core.models

import androidx.annotation.StringRes
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

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
