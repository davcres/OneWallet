package com.davidcrespo.onewallet.data.remote.investing

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

class InvestingDataSourceTest {

    private val apiClient = mockk<InvestingApiClient>()
    private val dataSource = InvestingDataSource(apiClient)

    @Test
    fun `getFundPrice devuelve el InvestmentDto del API client`() = runTest {
        // Given
        val symbol = "MY_FUND"
        val mockDto = InvestmentDto(
            symbol = symbol,
            name = "My Investment Fund",
            quantity = 0.0,
            price = 120.0,
            previousPrice = 118.0,
            currency = CurrencyDto(EUR),
            type = InvestmentType.FUND
        )
        coEvery { apiClient.getFundPrice(symbol) } returns mockDto

        // When
        val result = dataSource.getFundPrice(symbol)

        // Then
        assertEquals(mockDto, result)
    }

    @Test
    fun `getFundPrice devuelve null si el API client falla`() = runTest {
        // Given
        val symbol = "INVALID"
        coEvery { apiClient.getFundPrice(symbol) } returns null

        // When
        val result = dataSource.getFundPrice(symbol)

        // Then
        assertNull(result)
    }
}
