package com.davidcrespo.onewallet.data.remote.rate

object TwelveDataApiConfig {
    const val BASE_URL = "https://api.twelvedata.com"

    object GetRate {
        const val PATH = "currency_conversion"
        const val FROM_TO = "symbol"
        const val USD_EUR = "USD/EUR"
        const val AMOUNT = "amount"
        const val API_KEY = "apikey"

    }
}