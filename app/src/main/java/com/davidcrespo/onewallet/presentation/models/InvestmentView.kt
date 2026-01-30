package com.davidcrespo.onewallet.presentation.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

@Immutable
data class InvestmentView(
    val symbol: String,
    val name: String,
    val quantity: Double,
    val displayPrice: Double,
    val displayPreviousPrice: Double,
    val originalPrice: Double,
    val originalPreviousPrice: Double,
    val originalCurrency: Currency,
    val changePercent: Double,
    val type: InvestmentType,
    val month: Int,
    val year: Int
) {
    fun getIcon() =
        when (type) {
            InvestmentType.STOCK -> Icons.Outlined.StackedLineChart
            InvestmentType.CRYPTO -> Icons.Outlined.CurrencyBitcoin
            InvestmentType.FUND,
            InvestmentType.ETF -> Icons.Outlined.PieChartOutline
            InvestmentType.BANK -> Icons.Outlined.AccountBalance
            InvestmentType.OTHER -> Icons.Outlined.Payments
        }

    fun getIconRes() =
        when (type) {
            InvestmentType.STOCK -> R.drawable.ic_stacked_line_chart
            InvestmentType.CRYPTO -> R.drawable.ic_currency_bitcoin
            InvestmentType.FUND,
            InvestmentType.ETF -> R.drawable.ic_pie_chart
            InvestmentType.BANK -> R.drawable.ic_account_balance
            InvestmentType.OTHER -> R.drawable.ic_payments
        }
}

fun Investment.toUI(): InvestmentView {
    val changePercent = previousPrice
        .takeIf { it != 0.0 }
        ?.let { ((price - it) / it) * 100.0 } ?: 0.0

    return InvestmentView(
        symbol = symbol,
        name = name,
        quantity = quantity,
        displayPrice = price,
        displayPreviousPrice = previousPrice,
        originalPrice = price,
        originalPreviousPrice = previousPrice,
        originalCurrency = currency,
        changePercent = changePercent,
        type = type,
        month = month,
        year = year
    )
}

fun InvestmentView.toDomain(): Investment {
    return Investment(
        symbol = symbol,
        name = name,
        quantity = quantity,
        price = originalPrice,
        previousPrice = originalPreviousPrice,
        currency = originalCurrency,
        type = type,
        year = year,
        month = month
    )
}

fun String.toInvestmentView(): InvestmentView {
    val parts = this.split("|")
    return InvestmentView(
        symbol = parts[0],
        name = parts[1],
        quantity = parts[2].toDoubleOrNull() ?: 0.0,
        displayPrice = parts[3].toDoubleOrNull() ?: 0.0,
        displayPreviousPrice = parts[4].toDoubleOrNull() ?: 0.0,
        originalPrice = parts[5].toDoubleOrNull() ?: 0.0,
        originalPreviousPrice = parts[6].toDoubleOrNull() ?: 0.0,
        originalCurrency = Currency.valueOf(parts[7]),
        changePercent = parts[8].toDoubleOrNull() ?: 0.0,
        type = InvestmentType.valueOf(parts[9]),
        year = parts[10].toIntOrNull() ?: 0,
        month = parts[11].toIntOrNull() ?: 0,
    )
}

fun InvestmentView.toPreference(): String {
    return "$symbol|$name|$quantity|$displayPrice|$displayPreviousPrice|$originalPrice|$originalPreviousPrice|$originalCurrency|$changePercent|$type|$year|$month"
}
