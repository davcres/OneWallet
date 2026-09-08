package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.domain.repository.PriceAlertNotificationRepository
import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PriceAlertNotificationRepositoryImpl(
    private val settings: Settings,
    private val clock: Clock = Clock.System
) : PriceAlertNotificationRepository {

    override suspend fun wasNotifiedToday(symbol: String): Boolean {
        val today = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        val key = "alert_last_date_$symbol"
        return settings.getStringOrNull(key) == today
    }

    override suspend fun markNotifiedToday(symbol: String) {
        val today = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        val key = "alert_last_date_$symbol"
        settings.putString(key, today)
    }
}
