package com.davidcrespo.onewallet.domain.model.investment

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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.R
import kotlin.math.absoluteValue

/**
 * Represents the categorization for an investment.
 *
 * 1. **Identity Decoupling**: Uses explicit [id]s instead of [javaClass.simpleName] to remain
 *    resilient against ProGuard/R8 obfuscation.
 * 2. **Performance**: Uses a [Map] for O(1) lookups instead of linear [List.find].
 */
@Immutable
sealed class InvestmentCategory(
    override val id: String,
    @StringRes override val nameRes: Int? = null,
    override val color: Color,
    override val icon: ImageVector
) : InvestmentAttribute {
    data object Tech : InvestmentCategory("tech", R.string.category_tech, Color(0xFF3B82F6), Icons.Outlined.Computer)
    data object RealEstate : InvestmentCategory("real_estate", R.string.category_real_estate, Color(0xFF10B981), Icons.Outlined.HomeWork)
    data object Healthcare : InvestmentCategory("healthcare", R.string.category_healthcare, Color(0xFFEF4444), Icons.Outlined.Biotech)
    data object Energy : InvestmentCategory("energy", R.string.category_energy, Color(0xFFF59E0B), Icons.Outlined.ElectricBolt)
    data object Finance : InvestmentCategory("finance", R.string.category_finance, Color(0xFF6366F1), Icons.Outlined.Storefront)
    data object Consumer : InvestmentCategory("consumer", R.string.category_consumer, Color(0xFFEC4899), Icons.Outlined.LocalDining)
    data object Industrial : InvestmentCategory("industrial", R.string.category_industrial, Color(0xFF8B5CF6), Icons.Outlined.Factory)
    data object Telecom : InvestmentCategory("telecom", R.string.category_telecom, Color(0xFF06B6D4), Icons.Outlined.Smartphone)
    data object RawMaterials : InvestmentCategory("raw_materials", R.string.category_raw_materials, Color(0xFF84CC16), Icons.Outlined.Forest)
    data object Utilities : InvestmentCategory("utilities", R.string.category_utilities, Color(0xFF14B8A6), Icons.Outlined.Propane)
    data object Crypto : InvestmentCategory("crypto", R.string.category_crypto, Color(0xFFF97316), Icons.Outlined.CurrencyBitcoin)
    data object Other : InvestmentCategory("other", R.string.category_other, Color(0xFF94A3B8), Icons.Outlined.Category)

    /**
     * A user-defined category.
     * 
     * @property customName The display name of the category.
     */
    data class Custom(
        val customName: String
    ) : InvestmentCategory(
        id = customName,
        nameRes = null,
        color = generateColorFromName(customName),
        icon = Icons.Outlined.Category
    )

    companion object {
        /**
         * List of all system-defined categories available for selection.
         */
        val ALL_PREDEFINED: List<InvestmentCategory> by lazy {
            listOf(
                Tech, RealEstate, Healthcare, Energy, Finance, Consumer,
                Industrial, Telecom, RawMaterials, Utilities, Crypto, Other
            )
        }

        private val BY_ID: Map<String, InvestmentCategory> by lazy {
            ALL_PREDEFINED.associateBy { it.id }
        }

        /**
         * Deterministic palette used for generating stable colors for custom categories.
         */
        private val PALETTE: List<Color> by lazy {
            ALL_PREDEFINED.map { it.color }
        }

        /**
         * Resolves an [InvestmentCategory] from a raw string (ID or Name).
         *
         * @param nameOrId The identifier stored in the database. 
         */
        fun fromName(nameOrId: String?): InvestmentCategory {
            if (nameOrId.isNullOrBlank()) return Other

            val normalized = nameOrId.trim()
            val lower = normalized.lowercase()
            
            // First check by ID
            return BY_ID[lower] ?: Custom(customName = normalized)
        }

        /**
         * Generates a stable color derived from the category name.
         */
        private fun generateColorFromName(name: String): Color {
            val hash = name.hashCode().absoluteValue
            val palette = PALETTE
            if (palette.isEmpty()) return Color.Gray
            return palette[hash % palette.size]
        }
    }
}
