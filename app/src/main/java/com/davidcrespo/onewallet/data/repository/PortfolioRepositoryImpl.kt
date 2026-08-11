package com.davidcrespo.onewallet.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.davidcrespo.onewallet.data.local.database.portfolio.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toDomain
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toEntity
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate

class PortfolioRepositoryImpl(
    private val dao: PortfolioDao,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: DispatcherProvider
) : PortfolioRepository {

    /**
     * Room runs queries off the main thread, therefore, we don't need to manage anything.
     * BUT mapping could be done on Main thread if we don't change the dispatcher with flowOn.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPortfolioItems(): Flow<List<Investment>> {
        val now = LocalDate.now()
        val currentYear = now.year
        val currentMonth = now.monthValue
        val initKey = "portfolio_initialized_${currentYear}_${currentMonth}"

        return dao.getLatestPortfolio().flatMapLatest { latestEntities ->
            val isInitialized = sharedPreferences.getBoolean(initKey, false)

            if (!isInitialized) {
                val latestYear = latestEntities.firstOrNull()?.year
                val latestMonth = latestEntities.firstOrNull()?.month

                if (latestEntities.isNotEmpty() && (latestYear != currentYear || latestMonth != currentMonth)) {
                    val newEntities = latestEntities.map { it.copy(year = currentYear, month = currentMonth) }
                    dao.insertPortfolio(newEntities)
                }
                sharedPreferences.edit { putBoolean(initKey, true) }
            }

            dao.getPortfolio(currentYear, currentMonth).map { entities ->
                entities.map { it.toDomain() }
            }
        }.flowOn(dispatcher.io)
    }

    override suspend fun addOrUpdateItem(investment: Investment) =
        withContext(dispatcher.io) {
            dao.insertOrUpdate(investment.toEntity())
        }

    override suspend fun addOrUpdateItems(investments: List<Investment>) =
        withContext(dispatcher.io) {
            dao.insertPortfolio(investments.map { it.toEntity() })
        }

    override suspend fun removeItem(investment: Investment, year: Int, month: Int) =
        withContext(dispatcher.io) {
            dao.deleteInvestment(investment.symbol, year, month)
        }

    override suspend fun updateMonthPortfolio(
        year: Int,
        month: Int,
        investments: List<Investment>
    ) = withContext(dispatcher.io) {
            dao.updateMonthPortfolio(
                year,
                month,
                investments.map(Investment::toEntity)
            )
        }

    override suspend fun deleteMonthPortfolio(year: Int, month: Int) =
        withContext(dispatcher.io) {
            dao.deleteMonthPortfolio(year, month)
        }

    override suspend fun getMonthsPortfolio(): List<Investment> =
        withContext(dispatcher.io) {
            dao.getMonthsPortfolio().map { it.toDomain() }
        }
}