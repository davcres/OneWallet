package com.davidcrespo.onewallet.domain.model.investment

// https://github.com/stevdza-san/CountryPicker-KMP/tree/main/library/src/commonMain/composeResources/drawable
enum class GlobalMarketRegion(
    val displayName: String
) {
    ARGENTINA("Argentina"),
    AUSTRIA("Austria"),
    BELGIUM("Belgium"),
    BRAZIL("Brazil"),
    CANADA("Canada"),
    CHILE("Chile"),
    CHINA("China"),
    COLOMBIA("Colombia"),
    FRANCE("France"),
    GERMANY("Germany"),
    GREECE("Greece"),
    HONG_KONG("Hong Kong"),
    INDIA("India"),
    ISRAEL("Israel"),
    ITALY("Italy"),
    JAPAN("Japan"),
    MEXICO("Mexico"),
    NETHERLANDS("Netherlands"),
    PERU("Peru"),
    PORTUGAL("Portugal"),
    SOUTH_AFRICA("South Africa"),
    SPAIN("Spain"),
    SWITZERLAND("Switzerland"),
    UAE("UAE"),
    UNITED_KINGDOM("United Kingdom"),
    UNITED_STATES("United States"),
    GLOBAL("Global");

    companion object {
        private val aliasMap = mapOf(
            // 🇦🇷 Argentina
            "Argentina" to ARGENTINA,
            "BUE" to ARGENTINA,
            "Buenos Aires" to ARGENTINA,

            // 🇦🇹Austria
            "Austria" to ARGENTINA,
            "VIE" to ARGENTINA,
            "Viena" to ARGENTINA,

            // 🇧🇪 Belgium
            "Belgium" to BELGIUM,
            "BRU" to BELGIUM,
            "Brussels" to BELGIUM,

            // 🇧🇷 Brazil
            "Brazil" to BRAZIL,
            "Brazil/Sao Paolo" to BRAZIL,
            "SAO" to BRAZIL,
            "Sao Paolo" to BRAZIL,

            // 🇨🇦 Canada
            "Canada" to CANADA,
            "Toronto" to CANADA,
            "CNQ" to CANADA,
            "NEO" to CANADA,
            "V" to CANADA,
            "Toronto" to CANADA,
            "Toronto Venture" to CANADA,
            "Toronto Ventures" to CANADA,

            // 🇨🇱 Chile
            "Chile" to CHILE,
            "Santiago" to CHILE,
            "SGO" to CHILE,


            // 🇨🇳 China
            "China" to CHINA,
            "Mainland China" to CHINA,
            "Shanghai" to CHINA,
            "Shenzhen" to CHINA,
            "SHH" to CHINA,
            "SHZ" to CHINA,

            // 🇨🇴 Colombia
            "Colombia" to COLOMBIA,
            "Bogota" to COLOMBIA,
            "BVC" to COLOMBIA,

            // 🇦🇪 UAE
            "EAU" to UAE,
            "Dubai" to UAE,
            "UAE" to UAE,
            "DFM" to UAE,


            // 🇫🇷 France
            "France" to FRANCE,
            "AMS" to FRANCE,
            "PAR" to FRANCE,

            // 🇩🇪 Germany
            "Germany" to GERMANY,
            "Berlin" to GERMANY,
            "Frankfurt" to GERMANY,
            "Munich" to GERMANY,
            "FRA" to GERMANY,
            "XET" to GERMANY,
            "MUN" to GERMANY,
            "STU" to GERMANY,
            "HAM" to GERMANY,
            "BER" to GERMANY,
            "DUS" to GERMANY,
            "XETRA" to GERMANY,

            // 🇬🇷 Greece
            "Greece" to GREECE,
            "Athens" to GREECE,
            "ATH" to GREECE,

            // 🇭🇰 Hong Kong
            "Hong Kong" to HONG_KONG,
            "HKG" to HONG_KONG,

            // 🇮🇳 India
            "India" to INDIA,
            "BSE" to INDIA,
            "NSE" to INDIA,
            "India/Bombay" to INDIA,
            "NYSEArca" to INDIA,

            // 🇮🇱 Israel
            "Israel" to ISRAEL,
            "Tel Aviv" to ISRAEL,

            // 🇮🇹 Italy
            "Italy" to ITALY,
            "MIL" to ITALY,
            "Milan" to ITALY,


            // 🇯🇵 Japan
            "Japan" to JAPAN,
            "Tokyo" to JAPAN,
            "TSE" to JAPAN,

            // 🇲🇽 Mexico
            "MEX" to MEXICO,
            "Mexico" to MEXICO,

            // 🇳🇱 Netherlands
            "Netherlands" to NETHERLANDS,
            "AMS" to NETHERLANDS,
            "Amsterdam" to NETHERLANDS,

            // 🇵🇪 Perú
            "Peru" to PERU,
            "Lima" to PERU,
            "LIM" to PERU,

            // 🇵🇹 Portugal
            "Portugal" to PORTUGAL,
            "LIS" to PORTUGAL,
            "Lisbon" to PORTUGAL,

            // 🇿🇦 South Africa
            "South Africa" to SOUTH_AFRICA,
            "Johannesburg" to SOUTH_AFRICA,
            "JNB" to SOUTH_AFRICA,

            // 🇪🇸 Spain
            "Spain" to SPAIN,
            "Madrid" to SPAIN,
            "MCE" to SPAIN,
            "XMCE" to SPAIN,
            "Barcelona" to SPAIN,

            // 🇨🇭Switzerland
            "Switzerland" to SWITZERLAND,
            "SWX" to SWITZERLAND,
            "Zurich" to SWITZERLAND,

            // 🇬🇧 United Kingdom
            "United Kingdom" to UNITED_KINGDOM,
            "AQE" to UNITED_KINGDOM,
            "LON" to UNITED_KINGDOM,
            "London" to UNITED_KINGDOM,
            "LSE" to UNITED_KINGDOM,

            // 🇺🇸 US
            "United States" to UNITED_STATES,
            "AMEX" to UNITED_STATES,
            "ARCA" to UNITED_STATES,
            "ASE" to UNITED_STATES,
            "BATS" to UNITED_STATES,
            "BATS Trading" to UNITED_STATES,
            "CBT" to UNITED_STATES,
            "CMX" to UNITED_STATES,
            "Chicago Board of Trade" to UNITED_STATES,
            "Chicago Mercantile Exchange" to UNITED_STATES,
            "CME" to UNITED_STATES,
            "DJI" to UNITED_STATES,
            "ICE Futures" to UNITED_STATES,
            "IEX" to UNITED_STATES,
            "MSCI" to UNITED_STATES,
            "NASDAQ" to UNITED_STATES,
            "New York Commodity Exchange" to UNITED_STATES,
            "NCM" to UNITED_STATES,
            "NIM" to UNITED_STATES,
            "NGM" to UNITED_STATES,
            "NMS" to UNITED_STATES,
            "NYB" to UNITED_STATES,
            "NYM" to UNITED_STATES,
            "NY Mercantile" to UNITED_STATES,
            "NYQ" to UNITED_STATES,
            "XNAS" to UNITED_STATES,
            "NYSE" to UNITED_STATES,
            "XNYS" to UNITED_STATES,
            "NYSE American" to UNITED_STATES,
            "NYSEArca" to UNITED_STATES,
            "OTCM" to UNITED_STATES,
            "OQB" to UNITED_STATES,
            "OQX" to UNITED_STATES,
            "PNK" to UNITED_STATES,
            "SNP" to UNITED_STATES,

            // 🌍 Global
            "Global" to GLOBAL
        )

        fun from(value: String?): GlobalMarketRegion {
            val normalized = value?.trim() ?: return GLOBAL
            
            // Try match by name first (case-insensitive)
            entries.firstOrNull { it.name.equals(normalized, ignoreCase = true) }?.let { return it }

            val normalizedLower = normalized.lowercase()
            return aliasMap.entries
                .firstOrNull { it.key.lowercase() == normalizedLower }
                ?.value
                ?: GLOBAL
        }
    }
}
