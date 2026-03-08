package com.davidcrespo.onewallet.data.remote.dto

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.CurrencyEntity
import com.davidcrespo.onewallet.domain.model.investment.Currency

data class CurrencyDto(val code: String)

fun CurrencyDto.toDomain() = Currency(code)

fun CurrencyDto.toEntity() = CurrencyEntity(code)

fun Currency.toDto() = CurrencyDto(code)
