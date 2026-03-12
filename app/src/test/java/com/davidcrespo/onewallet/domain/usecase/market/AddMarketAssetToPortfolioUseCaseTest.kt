package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AddMarketAssetToPortfolioUseCaseTest {

    private val repository = mockk<PortfolioRepository>(relaxed = true)
    private lateinit var useCase: AddMarketAssetToPortfolioUseCase

    private val fixedDate = LocalDate.of(2026, 3, 10)

    @BeforeEach
    fun setUp() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns fixedDate
        useCase = AddMarketAssetToPortfolioUseCase(repository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cuando es stock (isCrypto false), guarda el asset con tipo STOCK y fecha actual`() = runTest {
        // Given
        val marketAsset = MarketAsset(
            symbol = "AAPL",
            price = 150.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            description = "Apple",
            stockType = "Common Stock"
        )
        
        // When
        useCase(marketAsset, isCrypto = false)

        // Then
        coVerify {
            repository.addOrUpdateItem(match { investment ->
                investment.symbol == "AAPL" &&
                investment.type == InvestmentType.STOCK &&
                investment.year == 2026 &&
                investment.month == 3
            })
        }
    }

    @Test
    fun `cuando es crypto (isCrypto true), guarda el asset con tipo CRYPTO y fecha actual`() = runTest {
        // Given
        val marketAsset = MarketAsset(
            symbol = "BTC",
            price = 60000.0,
            currency = Currency(EUR),
            type = InvestmentType.CRYPTO,
            description = "Bitcoin",
            stockType = "Crypto"
        )
        
        // When
        useCase(marketAsset, isCrypto = true)

        // Then
        coVerify {
            repository.addOrUpdateItem(match { investment ->
                investment.symbol == "BTC" &&
                investment.type == InvestmentType.CRYPTO &&
                investment.year == 2026 &&
                investment.month == 3
            })
        }
    }
}
