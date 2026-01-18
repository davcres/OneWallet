package com.davidcrespo.onewallet.data.remote.stock

object FinnhubApiConfig {
    const val BASE_URL = "https://finnhub.io"

    object GetSymbols {
        const val PATH = "api/v1/stock/symbol"
        const val EXCHANGE = "exchange"
        const val TOKEN = "token"
    }

    object GetQuote {
        const val PATH = "api/v1/quote"
        const val SYMBOL = "symbol"
    }
}