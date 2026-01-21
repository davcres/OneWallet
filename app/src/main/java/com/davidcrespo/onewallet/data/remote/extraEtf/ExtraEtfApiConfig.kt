package com.davidcrespo.onewallet.data.remote.extraEtf

object ExtraEtfApiConfig {
    const val BASE_URL = "https://extraetf.com"

    object GetEtf {
        const val PATH = "api-v2/detail"
        const val ISIN = "isin"
        const val LOCALE = "extraetf_locale"
    }
}