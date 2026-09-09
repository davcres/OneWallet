package com.davidcrespo.onewallet.core.models

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
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.category_consumer
import com.davidcrespo.onewallet.core.generated.resources.category_crypto
import com.davidcrespo.onewallet.core.generated.resources.category_energy
import com.davidcrespo.onewallet.core.generated.resources.category_finance
import com.davidcrespo.onewallet.core.generated.resources.category_healthcare
import com.davidcrespo.onewallet.core.generated.resources.category_industrial
import com.davidcrespo.onewallet.core.generated.resources.category_other
import com.davidcrespo.onewallet.core.generated.resources.category_raw_materials
import com.davidcrespo.onewallet.core.generated.resources.category_real_estate
import com.davidcrespo.onewallet.core.generated.resources.category_tech
import com.davidcrespo.onewallet.core.generated.resources.category_telecom
import com.davidcrespo.onewallet.core.generated.resources.category_utilities
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import org.jetbrains.compose.resources.StringResource
import kotlin.math.absoluteValue

val InvestmentCategory.nameResource: StringResource?
    get() = when (this) {
        InvestmentCategory.Tech -> Res.string.category_tech
        InvestmentCategory.RealEstate -> Res.string.category_real_estate
        InvestmentCategory.Healthcare -> Res.string.category_healthcare
        InvestmentCategory.Energy -> Res.string.category_energy
        InvestmentCategory.Finance -> Res.string.category_finance
        InvestmentCategory.Consumer -> Res.string.category_consumer
        InvestmentCategory.Industrial -> Res.string.category_industrial
        InvestmentCategory.Telecom -> Res.string.category_telecom
        InvestmentCategory.RawMaterials -> Res.string.category_raw_materials
        InvestmentCategory.Utilities -> Res.string.category_utilities
        InvestmentCategory.Crypto -> Res.string.category_crypto
        InvestmentCategory.Other -> Res.string.category_other
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
