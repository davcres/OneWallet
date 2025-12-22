package com.davidcrespo.onewallet.data.local.database.market.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidcrespo.onewallet.data.remote.finnhub.models.MarketCryptoResponse
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "crypto_market_table")
data class CryptoMarketEntity(
    @PrimaryKey val displaySymbol: String,
)

fun CryptoMarketEntity.toDomain() = MarketAsset(
    symbol = displaySymbol.replace("USDC", "USD"),
    currency = if (displaySymbol.endsWith("EUR")) Currency.EUR else Currency.USD,
    type = InvestmentType.CRYPTO,
    description = null,
    figi = null,
    stockType = null
)

fun MarketCryptoResponse.toCryptoEntity(): CryptoMarketEntity =
    CryptoMarketEntity(
        displaySymbol = displaySymbol
    )
