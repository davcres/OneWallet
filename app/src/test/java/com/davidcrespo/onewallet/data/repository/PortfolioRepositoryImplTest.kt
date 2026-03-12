package com.davidcrespo.onewallet.data.repository

import app.cash.turbine.test
import com.davidcrespo.onewallet.data.local.database.portfolio.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.CurrencyEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toEntity
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import com.davidcrespo.onewallet.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioRepositoryImplTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val dispatcherProvider = TestDispatcherProvider(mainDispatcherExtension.testDispatcher)

    private val dao = mockk<PortfolioDao>(relaxed = true)
    private lateinit var repository: PortfolioRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = PortfolioRepositoryImpl(dao, dispatcherProvider)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cuando pide los items del portfolio, los mapea de entidad a dominio correctamente`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val entity = InvestmentEntity(
            symbol = "AAPL",
            name = "Apple Inc",
            quantity = 10.0,
            price = 150.0,
            previousPrice = 145.0,
            currency = CurrencyEntity("EUR"),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 3
        )
        every { dao.getLatestPortfolio() } returns flowOf(listOf(entity))

        // When & Then
        repository.getPortfolioItems().test {
            val domainList = awaitItem()
            assertEquals(1, domainList.size)
            assertEquals("AAPL", domainList[0].symbol)
            assertEquals(150.0, domainList[0].price, 0.0)
            assertEquals(EUR, domainList[0].currency.code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cuando añade un item, mapea de dominio a entidad antes de llamar al DAO`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val domainInvestment = Investment(
            symbol = "BTC",
            name = "Bitcoin",
            quantity = 0.5,
            price = 60000.0,
            previousPrice = 59000.0,
            currency = Currency(EUR),
            type = InvestmentType.CRYPTO,
            year = 2026,
            month = 3
        )

        // When
        repository.addOrUpdateItem(domainInvestment)

        // Then
        coVerify(exactly = 1) { dao.insertOrUpdate(domainInvestment.toEntity()) }
    }

    @Test
    fun `cuando borra un item, llama al DAO con los parametros correctos`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val domainInvestment = Investment(
            symbol = "MSFT",
            name = "Microsoft",
            quantity = 5.0,
            price = 300.0,
            previousPrice = 0.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 3
        )

        // When
        repository.removeItem(domainInvestment, 2026, 3)

        // Then
        coVerify(exactly = 1) { dao.deleteInvestment("MSFT", 2026, 3) }
    }

    @Test
    fun `cuando actualiza el portfolio del mes, mapea la lista completa al DAO`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val investment = Investment(
            symbol = "AMZN",
            name = "Amazon",
            quantity = 2.0,
            price = 130.0,
            previousPrice = 0.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 3
        )
        val list = listOf(investment)

        // When
        repository.updateMonthPortfolio(2026, 3, list)

        // Then
        coVerify(exactly = 1) { dao.updateMonthPortfolio(2026, 3, match { it.size == 1 && it[0].symbol == "AMZN" }) }
    }

    @Test
    fun `cuando borra el portfolio del mes, delega la llamada al DAO`() = runTest(mainDispatcherExtension.testDispatcher) {
        // When
        repository.deleteMonthPortfolio(2026, 3)

        // Then
        coVerify(exactly = 1) { dao.deleteMonthPortfolio(2026, 3) }
    }

    @Test
    fun `cuando obtiene el historial mensual, mapea la lista de entidades a dominio`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val entity = InvestmentEntity(
            symbol = "TSLA",
            name = "Tesla",
            quantity = 5.0,
            price = 200.0,
            previousPrice = 190.0,
            currency = CurrencyEntity("EUR"),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 1
        )
        coEvery { dao.getMonthsPortfolio() } returns listOf(entity)

        // When
        val result = repository.getMonthsPortfolio()

        // Then
        assertEquals(1, result.size)
        assertEquals("TSLA", result[0].symbol)
        assertEquals(200.0, result[0].price, 0.0)
    }
}
