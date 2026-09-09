package com.davidcrespo.onewallet.core.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Euro
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.USD

data class CurrencyView(
    val code: String,
    val symbol: String,
    val icon: ImageVector
) {
    companion object {
        private val currencies = mapOf(
            USD to CurrencyView(USD, "$", Icons.Filled.AttachMoney),
            EUR to CurrencyView(EUR, "€", Icons.Filled.Euro),
            //GBP to CurrencyView(GBP, "£", Icons.Filled.CurrencyPound),
        )

        fun get(code: String) : CurrencyView {
            return currencies[code] ?: CurrencyView(code, code, Icons.Filled.CurrencyExchange)
        }
    }
}

fun Currency.toUI() = CurrencyView.get(code)

fun CurrencyView.toDomain() = Currency(code = code)
