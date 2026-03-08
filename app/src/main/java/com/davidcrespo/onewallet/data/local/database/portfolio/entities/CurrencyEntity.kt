package com.davidcrespo.onewallet.data.local.database.portfolio.entities

import com.davidcrespo.onewallet.domain.model.investment.Currency
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyEntity(val code: String)

fun Currency.toEntity() = CurrencyEntity(code)

fun CurrencyEntity.toDomain() = Currency(code)