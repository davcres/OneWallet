package com.davidcrespo.onewallet.domain.util

import com.davidcrespo.onewallet.domain.model.PortfolioItem
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun calculateTotalDcaInvested(item: PortfolioItem): Double {
    val start = item.dcaStartDate ?: return item.dcaInitialInvestment
    val now = System.currentTimeMillis()
    
    if (start > now) return item.dcaInitialInvestment

    val frequencyMillis = when (item.dcaFrequency) {
        "Diario" -> TimeUnit.DAYS.toMillis(1)
        "Semanal" -> TimeUnit.DAYS.toMillis(7)
        "Mensual" -> TimeUnit.DAYS.toMillis(30) // Aprox
        else -> TimeUnit.DAYS.toMillis(30)
    }

    val durationMillis = now - start
    // Count how many full periods have passed starting from 0 (initial date payment)?
    // Usually DCA executes on start date + periods.
    // Let's assume payment on start date is counted if we treat initialInvestment separately or as part of it?
    // User has "Inversión Inicial" field. So regular payments usually start after 1 period? Or on start date?
    // Let's assume regular payments start on Start Date.
    // Periods = (duration / frequency) + 1 (for the first payment on start date)
    // If duration is small, at least 1 payment if start <= now.
    
    val periods = (durationMillis / frequencyMillis).toLong() + 1
    
    return item.dcaInitialInvestment + (periods * item.dcaAmount)
}
