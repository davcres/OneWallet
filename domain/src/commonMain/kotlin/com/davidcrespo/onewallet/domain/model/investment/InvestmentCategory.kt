package com.davidcrespo.onewallet.domain.model.investment

/**
 * Represents the categorization for an investment.
 *
 * 1. **Identity Decoupling**: Uses explicit [id]s instead of [javaClass.simpleName] to remain
 *    resilient against ProGuard/R8 obfuscation.
 * 2. **Performance**: Uses a [Map] for O(1) lookups instead of linear [List.find].
 */
sealed class InvestmentCategory(
    override val id: String
) : InvestmentAttribute {
    data object Tech : InvestmentCategory("tech")
    data object RealEstate : InvestmentCategory("real_estate")
    data object Healthcare : InvestmentCategory("healthcare")
    data object Energy : InvestmentCategory("energy")
    data object Finance : InvestmentCategory("finance")
    data object Consumer : InvestmentCategory("consumer")
    data object Industrial : InvestmentCategory("industrial")
    data object Telecom : InvestmentCategory("telecom")
    data object RawMaterials : InvestmentCategory("raw_materials")
    data object Utilities : InvestmentCategory("utilities")
    data object Crypto : InvestmentCategory("crypto")
    data object Other : InvestmentCategory("other")

    /**
     * A user-defined category.
     * 
     * @property customName The display name of the category.
     */
    data class Custom(
        val customName: String
    ) : InvestmentCategory(id = customName)

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
    }
}
