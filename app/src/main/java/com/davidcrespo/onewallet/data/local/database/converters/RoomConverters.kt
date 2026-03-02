package com.davidcrespo.onewallet.data.local.database.converters

import androidx.room.TypeConverter
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

class RoomConverters {

    @TypeConverter
    fun investmentTypeToString(value: InvestmentType): String = value.name

    @TypeConverter
    fun stringToInvestmentType(value: String): InvestmentType = InvestmentType.valueOf(value)

    @TypeConverter
    fun currencyToString(value: Currency): String = value.name

    @TypeConverter
    fun stringToCurrency(value: String): Currency = Currency.from(value)
}