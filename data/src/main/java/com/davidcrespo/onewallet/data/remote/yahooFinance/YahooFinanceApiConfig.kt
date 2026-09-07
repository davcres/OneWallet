package com.davidcrespo.onewallet.data.remote.yahooFinance

object YahooFinanceApiConfig {
    const val BASE_URL = "https://query1.finance.yahoo.com"

    object GetSymbolsByQuery {
        const val PATH = "v1/finance/search"
        const val QUERY = "q"
    }

    object GetQuote {
        const val PATH = "v8/finance/chart"
        const val INTERVAL = "interval"
        const val RANGE = "range"
    }
}