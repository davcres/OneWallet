package com.davidcrespo.onewallet.data.local.database.portfolio.entities

import androidx.room.Entity
import com.davidcrespo.onewallet.domain.model.investment.DataSource
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "monthly_portfolio_table", primaryKeys = ["year", "month", "symbol"])
data class InvestmentEntity(
    val symbol: String,
    val name: String,
    val quantity: Double,
    val price: Double,
    val previousPrice: Double? = null,
    val currency: CurrencyEntity,
    val type: InvestmentType,
    val year: Int,
    val month: Int,
    val preferredApi: DataSource? = null,
    val alertThreshold: Double? = null,
    val category: String = InvestmentCategory.Other.id
) {
    override fun toString(): String {
        return "$symbol|$name|$quantity|$price|$previousPrice|${currency.code}|$type|$year|$month|${preferredApi?.name}|$alertThreshold|$category"
    }
}

fun Investment.toEntity(): InvestmentEntity = InvestmentEntity(
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
    alertThreshold = alertThreshold,
    category = category.id
)

fun InvestmentEntity.toDomain(): Investment = Investment(
    symbol = symbol,
    name = name,
    quantity = quantity,
    price = price,
    previousPrice = previousPrice ?: 0.0,
    currency = currency.toDomain(),
    type = type,
    year = year,
    month = month,
    preferredApi = preferredApi,
    alertThreshold = alertThreshold,
    category = InvestmentCategory.fromName(category)
)

fun String.toInvestmentEntity(): InvestmentEntity {
    val parts = this.split("|")
    return InvestmentEntity(
        symbol = parts[0],
        name = parts[1],
        quantity = parts[2].toDoubleOrNull() ?: 0.0,
        price = parts[3].toDoubleOrNull() ?: 0.0,
        previousPrice = parts[4].toDoubleOrNull(),
        currency = CurrencyEntity(parts[5]),
        type = InvestmentType.valueOf(parts[6]),
        year = parts[7].toIntOrNull() ?: 0,
        month = parts[8].toIntOrNull() ?: 0,
        preferredApi = parts.getOrNull(9)?.takeIf { it != "null" }?.let { runCatching { DataSource.valueOf(it) }.getOrNull() },
        alertThreshold = parts.getOrNull(10)?.takeIf { it != "null" }?.toDoubleOrNull(),
        category = parts.getOrNull(11)?.takeIf { it != "null" } ?: InvestmentCategory.Other.id
    )
}
