package com.davidcrespo.onewallet.domain.model.investment

enum class InvestmentType(
    override val id: String
) : InvestmentAttribute {
    STOCK("stock"),
    CRYPTO("crypto"),
    FUND("fund"),
    ETF("etf"),
    BANK("bank"),
    OTHER("other");
}

private val MARKET_TYPES = setOf(
    InvestmentType.STOCK,
    InvestmentType.CRYPTO,
    InvestmentType.FUND,
    InvestmentType.ETF,
)

private val MANUAL_TYPES = setOf(
    InvestmentType.BANK,
    InvestmentType.OTHER,
)

private val ISIN_TYPES = setOf(
    InvestmentType.FUND,
    InvestmentType.ETF,
)

fun InvestmentType.isMarket(): Boolean = this in MARKET_TYPES
fun InvestmentType.isManual(): Boolean = this in MANUAL_TYPES
fun InvestmentType.hasIsin(): Boolean = this in ISIN_TYPES
