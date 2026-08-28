package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.rate.Rate
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetCurrencyRateUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private lateinit var useCase: GetCurrencyRateUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetCurrencyRateUseCase(repository)
    }

    @Test
    fun `cuando el repositorio devuelve una tasa, el UseCase retorna el valor de esa tasa`() = runTest {
        // Given
        val from = "EUR"
        val to = "USD"
        val mockRate = Rate("EURUSD", 1.1)
        coEvery { repository.getRate(from, to) } returns Result.success(mockRate)

        // When
        val result = useCase(from, to)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1.1, result.getOrNull()!!, 0.0)
    }

    @Test
    fun `cuando el repositorio falla, el UseCase retorna el mismo fallo`() = runTest {
        // Given
        val from = "EUR"
        val to = "USD"
        val exception = Exception("API Error")
        coEvery { repository.getRate(from, to) } returns Result.failure(exception)

        // When
        val result = useCase(from, to)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
