package com.davidcrespo.onewallet.presentation.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.presentation.models.toUI
import com.davidcrespo.onewallet.presentation.widget.portfolio.PortfolioPrefsKeys
import com.davidcrespo.onewallet.presentation.widget.portfolio.PortfolioWidget
import com.davidcrespo.onewallet.presentation.widget.stocks.StocksPrefsKeys
import com.davidcrespo.onewallet.presentation.widget.stocks.StocksWidget
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class WidgetsRefreshWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    val getPortfolioItemsUseCase: GetPortfolioItemsUseCase by inject()
    val financialRepository: FinancialRepository by inject()
    val getCurrencyRateUseCase: GetCurrencyRateUseCase by inject()

    override suspend fun doWork(): Result = runCatching {

        // 1) Foto única del portfolio
        val portfolioData = getPortfolioItemsUseCase().first()

        // 2) Datos para PortfolioWidget
        val portfolioItemsSet = portfolioData
            .map {
                it.toUI().toString()
            }
            .toSet()

        // 3) Datos para StocksWidget
        val stocksSet = portfolioData
            .filter { it.type.isMarket() }
            .map {
                it.toUI().toString()
            }
            .toSet()

        val selectedCurrency = financialRepository.getSelectedCurrency()

        val rates = portfolioData
            .map { item ->
                val from = item.currency.code
                val to = selectedCurrency.code
                val rate = getCurrencyRateUseCase(from, to).getOrDefault(1.0)

                "$from/$to|$rate"
            }
            .toSet()

        // 4) Actualiza todos los PortfolioWidget
        val manager = GlanceAppWidgetManager(applicationContext)

        manager.getGlanceIds(PortfolioWidget::class.java).forEach { glanceId ->
            updateAppWidgetState(applicationContext, glanceId) { prefs: MutablePreferences ->
                prefs[PortfolioPrefsKeys.stocks] = portfolioItemsSet
                prefs[PortfolioPrefsKeys.selectedCurrency] = selectedCurrency.code
                prefs[PortfolioPrefsKeys.rates] = rates
            }
            PortfolioWidget().update(applicationContext, glanceId)
        }

        // 5) Actualiza todos los StocksWidget
        manager.getGlanceIds(StocksWidget::class.java).forEach { glanceId ->
            updateAppWidgetState(applicationContext, glanceId) { prefs: MutablePreferences ->
                prefs[StocksPrefsKeys.stocks] = stocksSet
                prefs[StocksPrefsKeys.selectedCurrency] = selectedCurrency.code
                prefs[StocksPrefsKeys.rates] = rates
            }
            StocksWidget().update(applicationContext, glanceId)
        }

        Result.success()
    }.getOrElse { e ->
        e.printStackTrace()
        Result.retry() // o failure()
    }

    companion object {
        private const val PERIODIC_WIDGET_TAG = "widgets_periodic_refresh"
        private const val MANUAL_WIDGET_TAG = "widgets_manual_refresh"

        fun enqueuePeriodic(context: Context) {
            val req = PeriodicWorkRequestBuilder<WidgetsRefreshWorker>(
                1, TimeUnit.HOURS
            )
                .addTag(PERIODIC_WIDGET_TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WIDGET_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                req
            )
        }

        fun enqueueNow(context: Context) {
            val req = OneTimeWorkRequestBuilder<WidgetsRefreshWorker>()
                .addTag(MANUAL_WIDGET_TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                MANUAL_WIDGET_TAG,
                ExistingWorkPolicy.REPLACE,
                req
            )
        }
    }
}
