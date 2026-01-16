package com.davidcrespo.onewallet.domain.model.investment

enum class InvestmentType {
    STOCK,
    CRYPTO,
    FUND,
    CASH
}

private val MARKET_TYPES = setOf(
    InvestmentType.STOCK,
    InvestmentType.CRYPTO,
    InvestmentType.FUND
)

private val MANUAL_TYPES = setOf(
    InvestmentType.CASH
)

fun InvestmentType.isMarket(): Boolean = this in MARKET_TYPES
fun InvestmentType.isManual(): Boolean = this in MANUAL_TYPES
