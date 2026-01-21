package com.davidcrespo.onewallet.data.remote.justEtf

object JustEtfApiConfig {
    const val BASE_URL = "https://www.justetf.com"

    object GetEtf {
        const val PATH_PREFIX = "api/etfs"
        const val PATH_SUFIX = "quote"
        const val CURRENCY = "currency"
        const val LOCALE = "locale"

    }

    object GetEtfDetail {
        const val PATH = "api/etfs/cards"
        const val ISIN = "isin"
        const val CURRENCY = "currency"
        const val LOCALE = "locale"
    }
}