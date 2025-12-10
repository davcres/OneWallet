package com.davidcrespo.onewallet.data.remote.finnhub

object FinnhubApiConfig {
    const val BASE_URL = "https://finnhub.io/api/v1"

    object GetQuote {
        const val PATH = "quote"
        const val SYMBOL = "symbol"
        const val TOKEN = "token"
    }
}