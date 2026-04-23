package com.davidcrespo.onewallet.domain.model.investment

enum class DataSource(val value: String) {
    FINNHUB("Finnhub"),
    YAHOO_FINANCE("Yahoo Finance"),
    ALPHA_VANTAGE("Alpha Vantage"),
    MARKETSTACK("Marketstack"),
    INVESTING_COM("Investing.com"),
    QUE_FONDOS("QueFondos.com"),
    JUST_ETF_DETAIL("JustETF.com (detail)"),
    JUST_ETF_PRICE("JustETF.com (price)"),
    EXTRA_ETF("ExtraETF.com"),
    BINANCE("Binance");

    companion object {
        fun fromValue(value: String?): DataSource? {
            return entries.find { it.value == value }
        }
    }
}
