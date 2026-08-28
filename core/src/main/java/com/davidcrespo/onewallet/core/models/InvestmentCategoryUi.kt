package com.davidcrespo.onewallet.core.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Factory
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.Propane
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.core.R
import kotlin.math.absoluteValue

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

val InvestmentCategory.color: Color
    get() = when (this) {
        InvestmentCategory.Tech -> Color(0xFF3B82F6)
        InvestmentCategory.RealEstate -> Color(0xFF10B981)
        InvestmentCategory.Healthcare -> Color(0xFFEF4444)
        InvestmentCategory.Energy -> Color(0xFFF59E0B)
        InvestmentCategory.Finance -> Color(0xFF6366F1)
        InvestmentCategory.Consumer -> Color(0xFFEC4899)
        InvestmentCategory.Industrial -> Color(0xFF8B5CF6)
        InvestmentCategory.Telecom -> Color(0xFF06B6D4)
        InvestmentCategory.RawMaterials -> Color(0xFF84CC16)
        InvestmentCategory.Utilities -> Color(0xFF14B8A6)
        InvestmentCategory.Crypto -> Color(0xFFF97316)
        InvestmentCategory.Other -> Color(0xFF94A3B8)
        is InvestmentCategory.Custom -> generateColorFromName(customName)
    }

val InvestmentCategory.icon: ImageVector
    get() = when (this) {
        InvestmentCategory.Tech -> Icons.Outlined.Computer
        InvestmentCategory.RealEstate -> Icons.Outlined.HomeWork
        InvestmentCategory.Healthcare -> Icons.Outlined.Biotech
        InvestmentCategory.Energy -> Icons.Outlined.ElectricBolt
        InvestmentCategory.Finance -> Icons.Outlined.Storefront
        InvestmentCategory.Consumer -> Icons.Outlined.LocalDining
        InvestmentCategory.Industrial -> Icons.Outlined.Factory
        InvestmentCategory.Telecom -> Icons.Outlined.Smartphone
        InvestmentCategory.RawMaterials -> Icons.Outlined.Forest
        InvestmentCategory.Utilities -> Icons.Outlined.Propane
        InvestmentCategory.Crypto -> Icons.Outlined.CurrencyBitcoin
        InvestmentCategory.Other -> Icons.Outlined.Category
        is InvestmentCategory.Custom -> Icons.Outlined.Category
    }

private val PREDEFINED_PALETTE: List<Color> = listOf(
    Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFEF4444), Color(0xFFF59E0B),
    Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF06B6D4),
    Color(0xFF84CC16), Color(0xFF14B8A6), Color(0xFFF97316), Color(0xFF94A3B8)
)

private fun generateColorFromName(name: String): Color {
    val hash = name.hashCode().absoluteValue
    return PREDEFINED_PALETTE[hash % PREDEFINED_PALETTE.size]
}
