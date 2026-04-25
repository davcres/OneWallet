package com.davidcrespo.onewallet.data.remote.dto

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity
import com.davidcrespo.onewallet.domain.model.investment.DataSource
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

data class InvestmentDto(
    val symbol: String,
    val name: String,
    val quantity: Double,
    val price: Double,
    val previousPrice: Double,
    val currency: CurrencyDto,
    val type: InvestmentType,
    val year: Int,
    val month: Int,
    val preferredApi: DataSource? = null,
    val alertThreshold: Double? = null
) {
    fun isValidName(): Boolean =
        name.isNotBlank()

    fun isValidPrice(): Boolean =
        price > 0.0
}

fun InvestmentDto.toDomain(): Investment = Investment(
    symbol = symbol,
    name = name,
    quantity = quantity,
    price = price,
    previousPrice = previousPrice,
    currency = currency.toDomain(),
    type = type,
    year = year,
    month = month,
    preferredApi = preferredApi,
    alertThreshold = alertThreshold
)

fun InvestmentDto.toEntity(): InvestmentEntity = InvestmentEntity(
    symbol = symbol,
    name = name,
    quantity = quantity,
    price = price,
    previousPrice = previousPrice,
    currency = currency.toEntity(),
    type = type,
    year = year,
    month = month,
    preferredApi = preferredApi,
    alertThreshold = alertThreshold
)
