package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.davidcrespo.onewallet.util.TestClock
import kotlinx.datetime.Instant

class SeedInitialPortfolioUseCaseTest {

    private val getInvestmentPriceUseCase = mockk<GetInvestmentPriceUseCase>()
    private val addInvestmentToPortfolioUseCase = mockk<AddInvestmentToPortfolioUseCase>(relaxed = true)
    private val clock = TestClock(Instant.parse("2026-04-21T12:00:00Z"))
    private val expectedYear = 2026
    private val expectedMonth = 4
    private lateinit var seedInitialPortfolioUseCase: SeedInitialPortfolioUseCase

    @BeforeEach
    fun setUp() {
        seedInitialPortfolioUseCase = SeedInitialPortfolioUseCase(
            getInvestmentPriceUseCase,
            addInvestmentToPortfolioUseCase,
            clock
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    private fun createStubInvestment(symbol: String, type: InvestmentType) = Investment(
        symbol = symbol,
        name = if (symbol == "GOOGL") "Alphabet Inc." else symbol,
        quantity = 0.0,
        price = 100.0,
        previousPrice = 90.0,
        currency = Currency(EUR),
        type = type,
        year = 0,
        month = 0
    )

    @Test
    fun `cuando se invoca el caso de uso, se añaden todos los activos con la fecha y cantidades correctas`() = runTest {
        // GIVEN
        val currency = Currency(EUR)
        val year = expectedYear
        val month = expectedMonth

        val btcBase = createStubInvestment("BTCEUR", InvestmentType.CRYPTO)
        val googlBase = createStubInvestment("GOOGL", InvestmentType.STOCK)

        coEvery { getInvestmentPriceUseCase(symbol = "BTCEUR", type = InvestmentType.CRYPTO, any(), any(), any(), any()) } returns Result.success(btcBase)
        coEvery { getInvestmentPriceUseCase(symbol = "GOOGL", type = InvestmentType.STOCK, any(), any(), any(), any()) } returns Result.success(googlBase)

        // WHEN
        seedInitialPortfolioUseCase.invoke(currency)

        // THEN
        coVerify { 
            addInvestmentToPortfolioUseCase.invoke(withArg<List<Investment>> { investments ->
                assertEquals(3, investments.size)
                
                // Verificar Bitcoin
                val btc = investments.find { it.symbol == "BTCEUR" }!!
                assertEquals(0.01, btc.quantity)
                assertEquals(year, btc.year)
                assertEquals(month, btc.month)

                // Verificar Google
                val googl = investments.find { it.symbol == "GOOGL" }!!
                assertEquals(2.0, googl.quantity)
                assertEquals(year, googl.year)
                assertEquals(month, googl.month)

                // Verificar Cuenta Bancaria (Manual)
                val bank = investments.find { it.type == InvestmentType.BANK }!!
                assertEquals("Cuenta Remunerada", bank.name)
                assertEquals(500.0, bank.quantity)
                assertEquals(year, bank.year)
                assertEquals(month, bank.month)
            })
        }
    }

    @Test
    fun `si falla un activo de mercado, el resto de activos se añaden correctamente`() = runTest {
        // GIVEN
        val currency = Currency(EUR)
        val googlBase = createStubInvestment("GOOGL", InvestmentType.STOCK)

        coEvery { getInvestmentPriceUseCase(symbol = "BTCEUR", type = InvestmentType.CRYPTO, any(), any(), any(), any()) } returns Result.failure(Exception("Network Error"))
        coEvery { getInvestmentPriceUseCase(symbol = "GOOGL", type = InvestmentType.STOCK, any(), any(), any(), any()) } returns Result.success(googlBase)

        // WHEN
        seedInitialPortfolioUseCase.invoke(currency)

        // THEN
        coVerify { 
            addInvestmentToPortfolioUseCase.invoke(withArg<List<Investment>> { investments ->
                // Deberían estar Google (éxito) y Cuenta Bancaria (siempre se añade)
                assertEquals(2, investments.size)
                assertTrue(investments.any { it.symbol == "GOOGL" })
                assertTrue(investments.any { it.type == InvestmentType.BANK })
                
                // Verificar que ambos tienen la fecha correcta
                investments.forEach {
                    assertEquals(expectedYear, it.year)
                    assertEquals(expectedMonth, it.month)
                }
            })
        }
    }
}
