package com.davidcrespo.onewallet.domain.repository

interface PriceAlertNotificationRepository {
    suspend fun wasNotifiedToday(symbol: String): Boolean
    suspend fun markNotifiedToday(symbol: String)
}
