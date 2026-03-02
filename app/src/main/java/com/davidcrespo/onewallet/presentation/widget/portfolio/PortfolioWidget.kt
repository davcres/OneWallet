package com.davidcrespo.onewallet.presentation.widget.portfolio

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
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
import com.davidcrespo.onewallet.MainActivity
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.models.toInvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.CurrencyConverter
import com.davidcrespo.onewallet.presentation.widget.designsystem.composables.OWInvestmentWidget
import com.davidcrespo.onewallet.presentation.widget.designsystem.composables.Reload
import com.davidcrespo.onewallet.presentation.widget.designsystem.composables.TotalBalance
import com.davidcrespo.onewallet.presentation.widget.designsystem.theme.WidgetColors
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class PortfolioWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState<Preferences>()
            val items = stringToPortfolio(state[PortfolioPrefsKeys.items].orEmpty())
            val currency = Currency.from(state[PortfolioPrefsKeys.currency] ?: Currency.EUR.name)
            val usdEurRate = state[PortfolioPrefsKeys.usdEurRate] ?: 1.0

            val currencyConverter = CurrencyConverter()
            val itemsConverted = items.map {
                currencyConverter.convert(it, currency, usdEurRate)
            }

            GlanceTheme(colors = WidgetColors) {
                PortfolioWidgetContent(
                    balance = itemsConverted.sumOf { it.quantity * it.displayPrice },
                    items = itemsConverted.toImmutableList(),
                    currency = currency
                )
            }
        }
    }

    @Composable
    fun PortfolioWidgetContent(
        balance: Double,
        items: ImmutableList<InvestmentView>,
        currency: Currency
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.widget_gradient_background))
                .clickable(actionStartActivity<MainActivity>())
                .padding(16.dp)
        ) {
            if (items.isEmpty()) {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TotalBalance(balance, currency)

                    Spacer(modifier = GlanceModifier.height(16.dp))

                    Reload()
                }
            } else {
                Column(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TotalBalance(balance, currency)

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Reload()
                    }

                    Spacer(modifier = GlanceModifier.height(12.dp))

                    ItemsList(
                        items = items,
                        currency = currency,
                        onClick = actionStartActivity<MainActivity>()
                    )
                }
            }
        }
    }

    @Composable
    private fun ItemsList(
        items: ImmutableList<InvestmentView>,
        currency: Currency,
        onClick: Action
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
                        section = SectionType.PORTFOLIO
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
}

object PortfolioPrefsKeys {
    val items = stringSetPreferencesKey("items")
    val currency = stringPreferencesKey("currency")
    val usdEurRate = doublePreferencesKey("usdEurRate")
}

@Preview
@Composable
private fun PortfolioWidgetPreview() {
    OneWalletTheme {
        PortfolioWidget().PortfolioWidgetContent(
            balance = 100000.0,
            items = persistentListOf(),
            currency = Currency.EUR
        )
    }
}
