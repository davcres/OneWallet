package com.davidcrespo.onewallet.data.remote.crypto

object BinanceApiConfig {
    const val BASE_URL = "https://api.binance.com"

    object GetCryptoSymbols {
        const val PATH = "api/v3/ticker/price"
    }

    object GetCryptoPrice {
        const val PATH = "api/v3/ticker/24hr"
        const val SYMBOL = "symbol"
    }
}