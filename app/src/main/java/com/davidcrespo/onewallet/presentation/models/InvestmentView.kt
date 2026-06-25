package com.davidcrespo.onewallet.presentation.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.DataSource
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import java.util.Locale
import kotlin.math.abs

import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory

@Immutable
data class InvestmentView(
    val symbol: String,
    val name: String,
    val quantity: Double,
    val displayPrice: Double,
    val displayPreviousPrice: Double,
    val originalPrice: Double,
    val originalPreviousPrice: Double,
    val originalCurrency: CurrencyView,
    val changePercent: Double,
    val type: InvestmentType,
    val month: Int,
    val year: Int,
    val preferredApi: DataSource? = null,
    val alertThreshold: Double? = null,
    val category: InvestmentCategory = InvestmentCategory.Other
) {
    fun getIconRes() =
        when (type) {
            InvestmentType.STOCK -> R.drawable.ic_stacked_line_chart
            InvestmentType.CRYPTO -> R.drawable.ic_currency_bitcoin
            InvestmentType.FUND -> R.drawable.ic_account_balance
            InvestmentType.ETF -> R.drawable.ic_query_stats
            InvestmentType.BANK -> R.drawable.ic_savings
            InvestmentType.OTHER -> R.drawable.ic_category
        }

    override fun toString(): String {
        return "$symbol|$name|$quantity|$displayPrice|$displayPreviousPrice|$originalPrice|$originalPreviousPrice|${originalCurrency.code}|$changePercent|$type|$year|$month|${preferredApi?.name}|$alertThreshold|${category.id}"
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
        originalCurrency = currency.toUI(),
        changePercent = changePercent,
        type = type,
        month = month,
        year = year,
        preferredApi = preferredApi,
        alertThreshold = alertThreshold,
        category = category
    )
}

fun InvestmentView.toDomain(): Investment {
    return Investment(
        symbol = symbol,
        name = name,
        quantity = quantity,
        price = originalPrice,
        previousPrice = originalPreviousPrice,
        currency = originalCurrency.toDomain(),
        type = type,
        year = year,
        month = month,
        preferredApi = preferredApi,
        alertThreshold = alertThreshold,
        category = category
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
        originalCurrency = CurrencyView.get(parts[7]),
        changePercent = parts[8].toDoubleOrNull() ?: 0.0,
        type = InvestmentType.valueOf(parts[9]),
        year = parts[10].toIntOrNull() ?: 0,
        month = parts[11].toIntOrNull() ?: 0,
        preferredApi = parts.getOrNull(12)?.takeIf { it != "null" }?.let { runCatching { DataSource.valueOf(it) }.getOrNull() },
        alertThreshold = parts.getOrNull(13)?.takeIf { it != "null" }?.toDoubleOrNull(),
        category = InvestmentCategory.fromName(parts.getOrNull(14)?.takeIf { it != "null" })
    )
}

@Composable
fun InvestmentView.contentDescription(
    totalValue: Double,
    currency: CurrencyView
): String {
    val title = this.name.ifEmpty { this.symbol }
    val formattedTotalValue = formatAccessibilityNumber(totalValue)

    return buildString {
        append(title)
        append(", ")
        append(formattedTotalValue)
        append(" ")
        append(currency.symbol)

        if (this@contentDescription.type.isMarket()) {
            append(", ")
            append(changePercentDescription(this@contentDescription))
            append(" ")
            append(priceDifferenceDescription(this@contentDescription, currency))
        }
    }
}

@Composable
private fun changePercentDescription(
    item: InvestmentView
): String {
    val formattedChangePercent = formatAccessibilityNumber(abs(item.changePercent))

    return when {
        item.changePercent > 0.0 -> stringResource(
            R.string.accessibility_percent_increased_by,
            formattedChangePercent
        )

        item.changePercent < 0.0 -> stringResource(
            R.string.accessibility_percent_decreased_by,
            formattedChangePercent
        )

        else -> stringResource(
            R.string.accessibility_unchanged
        )
    }
}

@Composable
private fun priceDifferenceDescription(
    item: InvestmentView,
    currency: CurrencyView
): String {
    val priceDifference = item.displayPrice - item.displayPreviousPrice
    val formattedDifference = formatAccessibilityNumber(abs(priceDifference))

    return when {
        priceDifference > 0.0 -> stringResource(
            R.string.accessibility_price_increased_by,
            formattedDifference,
            currency.symbol
        )

        priceDifference < 0.0 -> stringResource(
            R.string.accessibility_price_decreased_by,
            formattedDifference,
            currency.symbol
        )
        else -> ""
    }
}

private fun formatAccessibilityNumber(value: Double): String {
    return "%.2f".format(Locale.getDefault(), value)
}
