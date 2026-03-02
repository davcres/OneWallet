package com.davidcrespo.onewallet.domain.model.investment

import androidx.annotation.DrawableRes
import com.davidcrespo.onewallet.R

enum class GlobalMarketRegion(
    val displayName: String,
    @DrawableRes val flagRes: Int
) {
    BRAZIL("Brazil", R.drawable.flag_br),
    CANADA("Canada", R.drawable.flag_ca),
    CHINA("China", R.drawable.flag_cn),
    FRANCE("France", R.drawable.flag_fr),
    GERMANY("Germany", R.drawable.flag_de),
    HONG_KONG("Hong Kong", R.drawable.flag_hk),
    INDIA("India", R.drawable.flag_in),
    JAPAN("Japan", R.drawable.flag_jp),
    MEXICO("Mexico", R.drawable.flag_mx),
    NETHERLANDS("Netherlands", R.drawable.flag_nl),
    PORTUGAL("Portugal", R.drawable.flag_pt),
    SOUTH_AFRICA("South Africa", R.drawable.flag_za),
    SPAIN("Spain", R.drawable.flag_es),
    UNITED_KINGDOM("United Kingdom", R.drawable.flag_gb),
    UNITED_STATES("United States", R.drawable.flag_us),
    GLOBAL("Global", R.drawable.ic_globe);

    companion object {
        private val aliasMap = mapOf(
            // 🇧🇷 Brazil
            "Brazil" to BRAZIL,
            "Sao Paolo" to BRAZIL,
            "Brazil/Sao Paolo" to BRAZIL,

            // 🇨🇦 Canada
            "Toronto" to CANADA,
            "Toronto Venture" to CANADA,
            "Toronto Ventures" to CANADA,
            "Canada" to CANADA,

            // 🇨🇳 China
            "China" to CHINA,
            "Mainland China" to CHINA,
            "Shanghai" to CHINA,
            "Shenzhen" to CHINA,

            // 🇫🇷 France
            "France" to FRANCE,
            "Paris" to FRANCE,

            // 🇩🇪 Germany
            "Germany" to GERMANY,
            "Frankfurt" to GERMANY,
            "Berlin" to GERMANY,
            "Munich" to GERMANY,
            "XETRA" to GERMANY,

            // 🇭🇰 Hong Kong
            "Hong Kong" to HONG_KONG,

            // 🇮🇳 India
            "India" to INDIA,
            "BSE" to INDIA,
            "NSE" to INDIA,
            "India/Bombay" to INDIA,

            // 🇯🇵 Japan
            "Japan" to JAPAN,
            "Tokyo" to JAPAN,

            // 🇲🇽 Mexico
            "Mexico" to MEXICO,

            // 🇳🇱 Netherlands
            "Netherlands" to NETHERLANDS,
            "Amsterdam" to NETHERLANDS,

            // 🇵🇹 Portugal
            "Portugal" to PORTUGAL,
            "Lisbon" to PORTUGAL,

            // 🇿🇦 South Africa
            "South Africa" to SOUTH_AFRICA,
            "Johannesburg" to SOUTH_AFRICA,

            // 🇪🇸 Spain
            "Spain" to SPAIN,
            "Madrid" to SPAIN,
            "Barcelona" to SPAIN,

            // 🇬🇧 United Kingdom
            "United Kingdom" to UNITED_KINGDOM,
            "London" to UNITED_KINGDOM,

            // 🇺🇸 US
            "AMEX" to UNITED_STATES,
            "NASDAQ" to UNITED_STATES,
            "NYSE" to UNITED_STATES,
            "BATS" to UNITED_STATES,
            "United States" to UNITED_STATES,

            // 🌍 Global
            "Global" to GLOBAL
        )


        fun from(value: String?): GlobalMarketRegion {
            val normalized = value?.trim()?.lowercase()
            return aliasMap.entries
                .firstOrNull { it.key.lowercase() == normalized }
                ?.value
                ?: GLOBAL
        }
    }
}
