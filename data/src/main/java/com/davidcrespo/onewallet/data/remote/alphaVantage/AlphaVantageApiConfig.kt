package com.davidcrespo.onewallet.data.remote.alphaVantage

object AlphaVantageApiConfig {
    const val BASE_URL = "https://www.alphavantage.co"
    const val TOKEN = "apikey"

    object GetSymbolsByQuery {
        const val PATH = "query"
        const val FUNCTION = "function"
        const val SYMBOL_SEARCH = "SYMBOL_SEARCH"
        const val KEYWORDS = "keywords"
    }

    object GetQuote {
        const val PATH = "query"
        const val FUNCTION = "function"
        const val GLOBAL_QUOTE = "GLOBAL_QUOTE"
        const val SYMBOL = "symbol"
    }
}