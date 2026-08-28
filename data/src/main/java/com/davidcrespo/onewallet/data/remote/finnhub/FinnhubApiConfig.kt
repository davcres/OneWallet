package com.davidcrespo.onewallet.data.remote.finnhub

object FinnhubApiConfig {
    const val BASE_URL = "https://finnhub.io"
    const val TOKEN = "token"

    object GetSymbols {
        const val PATH = "api/v1/stock/symbol"
        const val EXCHANGE = "exchange"
    }

    object GetQuote {
        const val PATH = "api/v1/quote"
        const val SYMBOL = "symbol"
    }
}