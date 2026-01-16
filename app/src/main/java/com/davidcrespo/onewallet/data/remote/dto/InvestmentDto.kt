package com.davidcrespo.onewallet.data.remote.dto

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

data class InvestmentDto(
    val symbol: String,
    val name: String,
    val quantity: Double,
    val price: Double,
    val previousPrice: Double,
    val currency: Currency,
    val type: InvestmentType,
    val year: Int,
    val month: Int
)

fun InvestmentDto.toDomain(): Investment = Investment(
    symbol = symbol,
    name = name,
    quantity = quantity,
    price = price,
    previousPrice = previousPrice,
    currency = currency,
    type = type,
    year = year,
    month = month
)

fun InvestmentDto.toEntity(): InvestmentEntity = InvestmentEntity(
    symbol = symbol,
    name = name,
    quantity = quantity,
    price = price,
    previousPrice = previousPrice,
    currency = currency,
    type = type,
    year = year,
    month = month
)
