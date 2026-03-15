package com.davidcrespo.onewallet.presentation.widget.stocks

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.davidcrespo.onewallet.MainActivity
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.models.toInvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.CurrencyConverter
import com.davidcrespo.onewallet.presentation.widget.designsystem.composables.OWInvestmentWidget
import com.davidcrespo.onewallet.presentation.widget.designsystem.composables.Reload
import com.davidcrespo.onewallet.presentation.widget.designsystem.theme.WidgetColors
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class StocksWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val context = LocalContext.current

            val isDarkTheme = true
                // (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

            val state = currentState<Preferences>()
            val stocks = stringToPortfolio(state[StocksPrefsKeys.stocks].orEmpty())
            val selectedCurrency = CurrencyView.get(state[StocksPrefsKeys.selectedCurrency] ?: EUR)
            val rates = stringToRates(state[StocksPrefsKeys.rates].orEmpty())
            val currencyConverter = CurrencyConverter()

            val stocksConverted = stocks
                .map { investment ->
                    val rate =
                        rates["${investment.originalCurrency.code}/${selectedCurrency.code}"] ?: 1.0
                    investment.copy(
                        displayPrice = currencyConverter.convert(
                            investment.originalPrice,
                            investment.originalCurrency.code,
                            selectedCurrency.code,
                            rate
                        ),
                        displayPreviousPrice = currencyConverter.convert(
                            investment.originalPreviousPrice,
                            investment.originalCurrency.code,
                            selectedCurrency.code,
                            rate
                        )
                    )
                }
                .sortedByDescending { it.displayPrice }

            GlanceTheme(colors = WidgetColors) {
                StocksWidgetContent(
                    isDarkTheme = isDarkTheme,
                    stocks = stocksConverted.toImmutableList(),
                    currency = selectedCurrency
                )
            }
        }
    }

    @Composable
    fun StocksWidgetContent(
        isDarkTheme: Boolean,
        stocks: ImmutableList<InvestmentView>,
        currency: CurrencyView
    ) {
        val background = if (isDarkTheme) {
            R.drawable.widget_gradient_background_dark
        } else {
            R.drawable.widget_gradient_background_light
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(background))
                .clickable(actionStartActivity<MainActivity>())
                .padding(16.dp)
        ) {
            if (stocks.isEmpty()) {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Title()

                    Spacer(modifier = GlanceModifier.height(16.dp))

                    Reload(isDarkTheme)
                }
            } else {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                ) {
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Title()

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Reload(isDarkTheme)
                    }

                    Spacer(modifier = GlanceModifier.height(12.dp))

                    StockList(
                        items = stocks,
                        currency = currency,
                        onClick = actionStartActivity<MainActivity>(),
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }

    @Composable
    fun Title() {
        Text(
            text = LocalContext.current.getString(R.string.asset_prices_title),
            style = TextStyle(
                color = GlanceTheme.colors.onSecondary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }

    @Composable
    private fun StockList(
        items: ImmutableList<InvestmentView>,
        currency: CurrencyView,
        onClick: Action,
        isDarkTheme: Boolean
    ) {
        LazyColumn(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            items(
                count = items.size
            ) {
                Column(
                    modifier = GlanceModifier.clickable(onClick)
                ) {
                    OWInvestmentWidget(
                        item = items[it],
                        currency = currency,
                        section = SectionType.PRICES,
                        isDarkTheme = isDarkTheme
                    )

                    Spacer(modifier = GlanceModifier.height(12.dp))
                }
            }
        }
    }

    private fun stringToPortfolio(items: Set<String>): List<InvestmentView> {
        return items.map { item ->
            item.toInvestmentView()
        }
    }

    private fun stringToRates(items: Set<String>): Map<String, Double> =
        items.associate { item ->
            val (pair, rate) = item.split('|')
            pair to (rate.toDoubleOrNull() ?: 1.0)
        }
}

object StocksPrefsKeys {
    val stocks = stringSetPreferencesKey("stocks")
    val selectedCurrency = stringPreferencesKey("currency")
    val rates = stringSetPreferencesKey("rates")
}

@Preview
@Composable
private fun StocksWidgetPreview() {
    OneWalletTheme {
        StocksWidget().StocksWidgetContent(
            stocks = persistentListOf(),
            currency = CurrencyView.get(EUR),
            isDarkTheme = true
        )
    }
}
