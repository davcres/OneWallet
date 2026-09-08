package com.davidcrespo.onewallet.data.local.database.converters

import androidx.room.TypeConverter
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.CurrencyEntity
import com.davidcrespo.onewallet.domain.model.investment.DataSource
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

class RoomConverters {

    @TypeConverter
    fun investmentTypeToString(value: InvestmentType): String = value.name

    @TypeConverter
    fun stringToInvestmentType(value: String): InvestmentType = InvestmentType.valueOf(value)

    @TypeConverter
    fun currencyToString(value: CurrencyEntity): String = value.code

    @TypeConverter
    fun stringToCurrency(value: String): CurrencyEntity = CurrencyEntity(value)

    @TypeConverter
    fun dataSourceToString(value: DataSource?): String? = value?.name

    @TypeConverter
    fun stringToDataSource(value: String?): DataSource? = value?.let { runCatching { DataSource.valueOf(it) }.getOrNull() }
}