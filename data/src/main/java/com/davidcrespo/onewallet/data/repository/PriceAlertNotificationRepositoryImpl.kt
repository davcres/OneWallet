package com.davidcrespo.onewallet.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.davidcrespo.onewallet.domain.repository.PriceAlertNotificationRepository
import java.time.LocalDate

class PriceAlertNotificationRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : PriceAlertNotificationRepository {

    override suspend fun wasNotifiedToday(symbol: String): Boolean {
        val today = LocalDate.now().toString()
        val key = "alert_last_date_$symbol"
        return sharedPreferences.getString(key, null) == today
    }

    override suspend fun markNotifiedToday(symbol: String) {
        val today = LocalDate.now().toString()
        val key = "alert_last_date_$symbol"
        sharedPreferences.edit {
            putString(key, today)
        }
    }
}
