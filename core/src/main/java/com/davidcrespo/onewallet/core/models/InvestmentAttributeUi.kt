package com.davidcrespo.onewallet.core.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.domain.model.investment.InvestmentAttribute
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

val InvestmentAttribute.color: Color
    get() = when (this) {
        is InvestmentType -> this.color
        is InvestmentCategory -> this.color
        else -> Color.Gray
    }

val InvestmentAttribute.icon: ImageVector
    get() = when (this) {
        is InvestmentType -> this.icon
        is InvestmentCategory -> this.icon
        else -> Icons.Outlined.Category
    }

@get:StringRes
val InvestmentAttribute.nameRes: Int?
    get() = when (this) {
        is InvestmentType -> this.titleRes
        is InvestmentCategory -> this.nameRes
        else -> null
    }
