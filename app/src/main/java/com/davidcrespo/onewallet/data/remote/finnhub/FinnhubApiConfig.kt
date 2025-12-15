package com.davidcrespo.onewallet.data.remote.finnhub

object FinnhubApiConfig {
    const val BASE_URL = "https://finnhub.io/api/v1"

    object GetSymbols {
        const val PATH = "stock/symbol"
        const val EXCHANGE = "exchange"
        const val TOKEN = "token"
    }

    object GetCryptoSymbols {
        const val PATH = "crypto/symbol"
        const val EXCHANGE = "exchange"
        const val TOKEN = "token"
    }

    object GetQuote {
        const val PATH = "quote"
        const val SYMBOL = "symbol"
        const val TOKEN = "token"
    }
}