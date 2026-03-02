package com.davidcrespo.onewallet.data.local.database.portfolio.entities

import androidx.room.Entity
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
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
    val currency: Currency,
    val type: InvestmentType,
    val year: Int,
    val month: Int
) {
    override fun toString(): String {
        return "$symbol|$name|$quantity|$price|$previousPrice|$currency|$type|$year|$month"
    }
}

fun Investment.toEntity(): InvestmentEntity = InvestmentEntity(
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

fun InvestmentEntity.toDomain(): Investment = Investment(
    symbol = symbol,
    name = name,
    quantity = quantity,
    price = price,
    previousPrice = previousPrice ?: 0.0,
    currency = currency,
    type = type,
    year = year,
    month = month
)

fun String.toInvestmentEntity(): InvestmentEntity {
    val parts = this.split("|")
    return InvestmentEntity(
        symbol = parts[0],
        name = parts[1],
        quantity = parts[2].toDoubleOrNull() ?: 0.0,
        price = parts[3].toDoubleOrNull() ?: 0.0,
        previousPrice = parts[4].toDoubleOrNull() ?: 0.0,
        currency = Currency.from(parts[5]),
        type = InvestmentType.valueOf(parts[6]),
        year = parts[7].toIntOrNull() ?: 0,
        month = parts[8].toIntOrNull() ?: 0,
    )
}
