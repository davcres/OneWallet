package com.davidcrespo.onewallet.domain.model.investment

import androidx.compose.ui.graphics.Color

enum class InvestmentType(val color: Color) {
    STOCK(Color(0xFF3B82F6)),
    CRYPTO(Color(0xFFF59E0B)),
    FUND(Color(0xFF8B5CF6)),
    ETF(Color(0xFF10B981)),
    CASH(Color(0xFF64748B)),
    OTHER(Color(0xFFEC4899)),
}

private val MARKET_TYPES = setOf(
    InvestmentType.STOCK,
    InvestmentType.CRYPTO,
    InvestmentType.FUND,
    InvestmentType.ETF,
)

private val MANUAL_TYPES = setOf(
    InvestmentType.CASH,
    InvestmentType.OTHER,
)

private val ISIN_TYPES = setOf(
    InvestmentType.FUND,
    InvestmentType.ETF,
)

fun InvestmentType.isMarket(): Boolean = this in MARKET_TYPES
fun InvestmentType.isManual(): Boolean = this in MANUAL_TYPES
fun InvestmentType.hasIsin(): Boolean = this in ISIN_TYPES
