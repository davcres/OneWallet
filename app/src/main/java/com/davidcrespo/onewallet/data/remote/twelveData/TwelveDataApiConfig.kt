package com.davidcrespo.onewallet.data.remote.twelveData

object TwelveDataApiConfig {
    const val BASE_URL = "https://api.twelvedata.com"

    object GetPrice {
        const val PATH = "price"
        const val SYMBOL = "symbol"
        const val API_KEY = "apikey"
    }
}