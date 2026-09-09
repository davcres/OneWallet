package com.davidcrespo.onewallet.core.models

import androidx.annotation.StringRes
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory

@get:StringRes
val InvestmentCategory.nameRes: Int?
    get() = when (this) {
        InvestmentCategory.Tech -> R.string.category_tech
        InvestmentCategory.RealEstate -> R.string.category_real_estate
        InvestmentCategory.Healthcare -> R.string.category_healthcare
        InvestmentCategory.Energy -> R.string.category_energy
        InvestmentCategory.Finance -> R.string.category_finance
        InvestmentCategory.Consumer -> R.string.category_consumer
        InvestmentCategory.Industrial -> R.string.category_industrial
        InvestmentCategory.Telecom -> R.string.category_telecom
        InvestmentCategory.RawMaterials -> R.string.category_raw_materials
        InvestmentCategory.Utilities -> R.string.category_utilities
        InvestmentCategory.Crypto -> R.string.category_crypto
        InvestmentCategory.Other -> R.string.category_other
        is InvestmentCategory.Custom -> null
    }
