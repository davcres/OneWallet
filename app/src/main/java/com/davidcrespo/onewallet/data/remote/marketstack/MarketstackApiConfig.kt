package com.davidcrespo.onewallet.data.remote.marketstack

object MarketstackApiConfig {
    const val BASE_URL = "https://api.marketstack.com"
    const val TOKEN = "access_key"

    object GetSymbolsByQuery {
        const val PATH = "v2/tickerslist"
        const val QUERY = "search"
    }

    object GetQuote {
        const val PATH = "v2/eod"
        const val SYMBOL = "symbols"
        const val LIMIT = "limit"
        const val SORT = "sort"
    }
}