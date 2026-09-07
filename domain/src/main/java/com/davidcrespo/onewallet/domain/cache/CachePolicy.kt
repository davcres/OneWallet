package com.davidcrespo.onewallet.domain.cache

data class CachePolicy(
    val stockHours: Long,
    val cryptoHours: Long,
    val fundHours: Long,
    val etfHours: Long,
    val marketHours: Long,
    val rateHours: Long
)
