package com.davidcrespo.onewallet.core.models

import androidx.annotation.StringRes
import com.davidcrespo.onewallet.domain.model.investment.InvestmentAttribute
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

@get:StringRes
val InvestmentAttribute.nameRes: Int?
    get() = when (this) {
        is InvestmentType -> this.titleRes
        is InvestmentCategory -> this.nameRes
        else -> null
    }
