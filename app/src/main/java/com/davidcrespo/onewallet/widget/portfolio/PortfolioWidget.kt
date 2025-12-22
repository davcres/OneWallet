package com.davidcrespo.onewallet.widget.portfolio

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.davidcrespo.onewallet.MainActivity
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.widget.WidgetsRefreshWorker
import kotlin.math.roundToInt

class PortfolioWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState<Preferences>()
            val balance = state[PortfolioPrefsKeys.balance] ?: 0.0
            val items = stringToPortfolio(state[PortfolioPrefsKeys.items].orEmpty())

            PortfolioWidgetContent(
                balance = balance,
                items = items.sortedByDescending { it.quantity * it.price }
            )
        }
    }

    @Composable
    fun PortfolioWidgetContent(
        balance: Double,
        items: List<Investment>
    ) {

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity<MainActivity>())
                .padding(16.dp)
        ) {
            if (items.isEmpty()) {
                Row(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TotalBalance(balance)

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
                        TotalBalance(balance)

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Reload()
                    }
                    if (items.isNotEmpty()) {
                        ItemsList(items)
                    }
                }
            }
        }
    }

    @Composable
    fun TotalBalance(balance: Double) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Balance Total",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )

            Text(
                text = if (balance >= 100000)
                    "${balance.roundToInt()}€"
                else
                    "${String.format("%.2f", balance)} €",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
        }
    }

    @Composable
    fun ItemsList(items: List<Investment>) {
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
                ItemRow(items[it])
            }
        }
    }

    @Composable
    fun Reload(modifier: GlanceModifier = GlanceModifier) {
        Box(
            modifier = modifier.clickable(actionRunCallback<GetPortfolioCallback>())
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_reload),
                contentDescription = "Reload"
            )
        }
    }

    @Composable
    fun ItemRow(item: Investment) {
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
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                modifier = GlanceModifier.defaultWeight(),
                maxLines = 1
            )
            Text(
                text = "${String.format("%.2f", item.quantity * item.price)} €",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
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

class GetPortfolioCallback() : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        WidgetsRefreshWorker.enqueueNow(context)
    }
}

object PortfolioPrefsKeys {
    val balance = doublePreferencesKey("balance")
    val items = stringSetPreferencesKey("items")
}