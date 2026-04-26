package com.davidcrespo.onewallet.presentation.portfolio.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.core.NotificationHelper
import com.davidcrespo.onewallet.domain.logging.Telemetry
import com.davidcrespo.onewallet.domain.repository.PriceAlertNotificationRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RefreshPortfolioPricesUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class PriceAlertWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val refreshPortfolioPricesUseCase: RefreshPortfolioPricesUseCase by inject()
    private val getPortfolioItemsUseCase: GetPortfolioItemsUseCase by inject()
    private val telemetry: Telemetry by inject()
    private val alertNotificationRepository: PriceAlertNotificationRepository by inject()

    override suspend fun doWork(): Result = runCatching {
        telemetry.log("alerts")

        // 0) Check if it has notifications configured
        if (!canSendNotifications()) {
            telemetry.log("no notifications")
            return Result.success()
        }

        // 1) Check if it has alerts configured
        val currentItems = getPortfolioItemsUseCase().first()
        val hasAlertsConfigured = currentItems.any { it.alertThreshold != null }
        if (!hasAlertsConfigured) {
            telemetry.log("no alerts")
            return Result.success()
        }

        // 2) Refresca precios y obtiene cambios
        val updatedItemsWithChange = refreshPortfolioPricesUseCase()

        // 3) Comprueba alertas y notifica
        updatedItemsWithChange.forEach { (item, changePercent) ->
            item.alertThreshold?.let { threshold ->
                val shouldNotify = abs(changePercent) >= threshold
                telemetry.log("Symbol: ${item.symbol}\nchangePercent: $changePercent\nthreshold: $threshold\nshouldNotify: $shouldNotify")
                
                if (shouldNotify && !alertNotificationRepository.wasNotifiedToday(item.symbol)) {
                    telemetry.log("Notify ${item.symbol}")
                    NotificationHelper.showPriceAlertNotification(
                        applicationContext,
                        item.symbol,
                        item.name,
                        changePercent
                    )
                    alertNotificationRepository.markNotifiedToday(item.symbol)
                } else {
                    telemetry.log("Already notified today for ${item.symbol}")
                }
            }
        }

        Result.success()
    }.getOrElse { e ->
        telemetry.log("Error $e")
        e.printStackTrace()
        Result.retry()
    }

    private fun canSendNotifications(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val PERIODIC_ALERTS_TAG = "price_alerts_periodic_refresh"

        fun enqueuePeriodic(context: Context) {
            val req = PeriodicWorkRequestBuilder<PriceAlertWorker>(
                1, TimeUnit.HOURS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .addTag(PERIODIC_ALERTS_TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_ALERTS_TAG,
                if (BuildConfig.DEBUG) {
                    ExistingPeriodicWorkPolicy.UPDATE
                } else {
                    ExistingPeriodicWorkPolicy.KEEP
                },
                req
            )
        }
    }
}
