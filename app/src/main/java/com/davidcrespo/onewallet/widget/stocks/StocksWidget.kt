package com.davidcrespo.onewallet.widget.stocks

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.davidcrespo.onewallet.MainActivity
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.data.local.database.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.entities.toDomain
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StocksWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState<Preferences>()
            val stocks = stringToPortfolio(state[StocksPrefsKeys.stocks].orEmpty())

            StocksWidgetContent(
                stocks = stocks
            )
        }
    }

    @Composable
    fun StocksWidgetContent(
        stocks: List<Investment>
    ) {

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xCC1C1C1E))
                .clickable(actionStartActivity<MainActivity>())
                .cornerRadius(20.dp)
                .padding(12.dp)
        ) {
            if (stocks.isEmpty()) {
                Row(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Title()

                    Spacer(modifier = GlanceModifier.defaultWeight())

                    Reload()
                }
            } else {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        Title()

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Reload()
                    }
                    if (stocks.isNotEmpty()) {
                        StockList(stocks)
                    }
                }
            }
        }
    }

    @Composable
    fun Title() {
        Text(
            text = "Precios de Activos",
            style = TextStyle(
                color = GlanceTheme.colors.surface,
                fontSize = 14.sp
            )
        )
    }

    @Composable
    fun StockList(items: List<Investment>) {
        LazyColumn(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Spacer(modifier = GlanceModifier.height(12.dp))

                // Divider
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GlanceTheme.colors.outline)
                ) {}

                Spacer(modifier = GlanceModifier.height(12.dp))
            }

            items(items.size) {
                StockRow(items[it])
            }
        }
    }

    @Composable
    fun Reload(modifier: GlanceModifier = GlanceModifier) {
        Box(
            modifier = modifier
                .clickable(actionRunCallback<GetPortfolioCallback>())
                .size(16.dp)
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_reload),
                contentDescription = "Reload",
                colorFilter = ColorFilter.tint(GlanceTheme.colors.surface)
            )
        }
    }

    @Composable
    fun StockRow(item: Investment) {
        Row(
            modifier = GlanceModifier
                .clickable(actionStartActivity<MainActivity>())
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.symbol,
                style = TextStyle(
                    color = GlanceTheme.colors.surface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                modifier = GlanceModifier.defaultWeight(),
                maxLines = 1
            )
            Text(
                text = "${String.format("%.2f", item.price)} €",
                style = TextStyle(
                    color = GlanceTheme.colors.surface,
                    fontSize = 14.sp
                ),
                maxLines = 1
            )
        }
    }


    private fun stringToPortfolio(items: Set<String>): List<Investment> {
        return items.map { item ->
            val parts = item.split("|")
            Investment(
                symbol = parts[0],
                quantity = parts[1].toDoubleOrNull() ?: 0.0,
                price = parts[2].toDoubleOrNull() ?: 0.0,
                previousPrice = parts[3].toDoubleOrNull() ?: 0.0,
                currency = Currency.valueOf(parts[4]),
                type = InvestmentType.valueOf(parts[5]),
                year = parts[6].toIntOrNull() ?: 0,
                month = parts[7].toIntOrNull() ?: 0,
            )
        }
    }
}

class GetPortfolioCallback() : ActionCallback, KoinComponent {

    private val portfolioDao: PortfolioDao by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        runCatching {
            portfolioDao.getLatestPortfolio().map { it.map { it.toDomain() } }.collect { portfolioData ->

                updateAppWidgetState(context, glanceId) { prefs: MutablePreferences ->
                    prefs[StocksPrefsKeys.stocks] = portfolioData.map {
                        "${it.symbol}|${it.quantity}|${it.price}|${it.previousPrice}|${it.currency}|${it.type}|${it.year}|${it.month}"
                    }.toSet()
                }

                StocksWidget().update(context, glanceId)
            }
        }.onFailure {
            it.printStackTrace()
        }
    }
}

object StocksPrefsKeys {
    val stocks = stringSetPreferencesKey("stocks")
}