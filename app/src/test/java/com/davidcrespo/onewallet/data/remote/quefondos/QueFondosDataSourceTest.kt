package com.davidcrespo.onewallet.data.remote.quefondos

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class QueFondosDataSourceTest {

    private val apiClient = mockk<QueFondosApiClient>()
    private val dataSource = QueFondosDataSource(apiClient)

    @Test
    fun `getFundPrice devuelve el InvestmentDto del API client`() = runTest {
        // Given
        val symbol = "ES0123456789"
        val type = InvestmentType.FUND
        val mockDto = InvestmentDto(
            symbol = symbol,
            name = "My Fund",
            quantity = 0.0,
            price = 50.0,
            previousPrice = 49.5,
            currency = CurrencyDto(EUR),
            type = type,
            year = 0,
            month = 0
        )
        coEvery { apiClient.getFundPrice(symbol, type) } returns mockDto

        // When
        val result = dataSource.getFundPrice(symbol, type)

        // Then
        assertEquals(mockDto, result)
    }

    @Test
    fun `getFundPrice devuelve null si el API client falla`() = runTest {
        // Given
        val symbol = "INVALID"
        val type = InvestmentType.FUND
        coEvery { apiClient.getFundPrice(symbol, type) } returns null

        // When
        val result = dataSource.getFundPrice(symbol, type)

        // Then
        assertNull(result)
    }
}
