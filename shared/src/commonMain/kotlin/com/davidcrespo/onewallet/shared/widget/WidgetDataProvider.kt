package com.davidcrespo.onewallet.shared.widget

import com.davidcrespo.onewallet.core.models.toUI
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.CurrencyConverter
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val PERCENT_MULTIPLIER = 100.0
private const val ROUNDING_FACTOR = 100.0

data class WidgetAssetItem(
    val name: String,
    val symbol: String,
    val quantity: Double,
    val value: Double,
    val formattedValue: String,
    val trendPercent: Double,
    val isPositive: Boolean
)

data class WidgetDataSnapshot(
    val totalBalance: Double,
    val formattedBalance: String,
    val currencySymbol: String,
    val items: List<WidgetAssetItem>
)

class WidgetDataProvider : KoinComponent {
    private val getPortfolioItemsUseCase: GetPortfolioItemsUseCase by inject()
    private val financialRepository: FinancialRepository by inject()
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase by inject()
    private val currencyConverter = CurrencyConverter()

    suspend fun getSnapshot(): WidgetDataSnapshot {
        val portfolioData = getPortfolioItemsUseCase().first()
        val selectedCurrency = financialRepository.getSelectedCurrency()
        val currencySymbol = selectedCurrency.toUI().symbol

        val convertedItems = portfolioData.map { item ->
            val rate = getCurrencyRateUseCase(item.currency.code, selectedCurrency.code).getOrDefault(1.0)
            val currentVal = currencyConverter.convert(
                item.price,
                item.currency.code,
                selectedCurrency.code,
                rate
            ) * item.quantity
            val prevVal = currencyConverter.convert(
                item.previousPrice,
                item.currency.code,
                selectedCurrency.code,
                rate
            ) * item.quantity

            val trend = if (prevVal > 0) ((currentVal - prevVal) / prevVal) * PERCENT_MULTIPLIER else 0.0

            WidgetAssetItem(
                name = item.name,
                symbol = item.symbol,
                quantity = item.quantity,
                value = currentVal,
                formattedValue = formatAmount(currentVal, currencySymbol),
                trendPercent = trend,
                isPositive = trend >= 0
            )
        }.sortedByDescending { it.value }

        val totalBalance = convertedItems.sumOf { it.value }
        return WidgetDataSnapshot(
            totalBalance = totalBalance,
            formattedBalance = formatAmount(totalBalance, currencySymbol),
            currencySymbol = currencySymbol,
            items = convertedItems
        )
    }

    private fun formatAmount(amount: Double, symbol: String): String {
        val rounded = ((amount * ROUNDING_FACTOR).toLong()) / ROUNDING_FACTOR
        return "$symbol $rounded"
    }
}
