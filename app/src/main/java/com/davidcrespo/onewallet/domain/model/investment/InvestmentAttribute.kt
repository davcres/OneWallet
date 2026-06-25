package com.davidcrespo.onewallet.domain.model.investment

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Common interface for investment classifications like Type and Category.
 */
interface InvestmentAttribute {
    val id: String
    @get:StringRes val nameRes: Int?
    val color: Color
    val icon: ImageVector
}
