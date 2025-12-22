package com.davidcrespo.onewallet.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.widget.portfolio.PortfolioPrefsKeys
import com.davidcrespo.onewallet.widget.portfolio.PortfolioWidget
import com.davidcrespo.onewallet.widget.stocks.StocksPrefsKeys
import com.davidcrespo.onewallet.widget.stocks.StocksWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class WidgetsRefreshWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    val getPortfolioItemsUseCase : GetPortfolioItemsUseCase by inject()

    override suspend fun doWork(): Result = runCatching {
        withContext(Dispatchers.IO) {

            // 1) Foto única del portfolio
            val portfolioData = getPortfolioItemsUseCase().first()

            // 2) Datos para PortfolioWidget
            val balance = portfolioData.sumOf { it.quantity * it.price }
            val portfolioItemsSet = portfolioData
                .map {
                    "${it.symbol}|${it.quantity}|${it.price}|${it.previousPrice}|${it.currency}|${it.type}|${it.year}|${it.month}"
                }
                .toSet()

            // 3) Datos para StocksWidget
            val stocksSet = portfolioData
                .filter { it.type == InvestmentType.STOCK || it.type == InvestmentType.CRYPTO }
                .map {
                    "${it.symbol}|${it.quantity}|${it.price}|${it.previousPrice}|${it.currency}|${it.type}|${it.year}|${it.month}"
                }
                .toSet()

            val manager = GlanceAppWidgetManager(applicationContext)

            // 4) Actualiza todos los PortfolioWidget
            manager.getGlanceIds(PortfolioWidget::class.java).forEach { glanceId ->
                updateAppWidgetState(applicationContext, glanceId) { prefs: MutablePreferences ->
                    prefs[PortfolioPrefsKeys.balance] = balance
                    prefs[PortfolioPrefsKeys.items] = portfolioItemsSet
                }
                PortfolioWidget().update(applicationContext, glanceId)
            }

            // 5) Actualiza todos los StocksWidget
            manager.getGlanceIds(StocksWidget::class.java).forEach { glanceId ->
                updateAppWidgetState(applicationContext, glanceId) { prefs: MutablePreferences ->
                    prefs[StocksPrefsKeys.stocks] = stocksSet
                }
                StocksWidget().update(applicationContext, glanceId)
            }
        }

        Result.success()
    }.getOrElse { e ->
        e.printStackTrace()
        Result.retry() // o failure()
    }

    companion object {
        private const val WIDGET_TAG = "widgets_refresh"

        fun enqueue(context: Context) {
            val req = PeriodicWorkRequestBuilder<WidgetsRefreshWorker>(
                1, TimeUnit.HOURS
            )
                .addTag(WIDGET_TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WIDGET_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                req
            )
        }
    }
}