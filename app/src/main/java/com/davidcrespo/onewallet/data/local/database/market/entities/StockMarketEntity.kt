package com.davidcrespo.onewallet.data.local.database.market.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidcrespo.onewallet.data.remote.finnhub.models.MarketStockResponse
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "stock_market_table")
data class StockMarketEntity(
    @PrimaryKey val symbol: String,
    val description: String?,
    val currency: Currency,
    val figi: String?,
    val type: String?
)

fun StockMarketEntity.toDomain(): MarketAsset =
    MarketAsset(
        symbol = symbol,
        price = 0.0,
        currency = currency,
        type = InvestmentType.STOCK,
        description = description,
        figi = figi,
        stockType = type
    )

fun MarketStockResponse.toStockEntity(): StockMarketEntity? {
    return if (currency.isNotEmpty()) {
        StockMarketEntity(
            symbol = symbol,
            currency = Currency.USD,
            type = type,
            description = description,
            figi = figi,
        )
    } else {
        null
    }
}
