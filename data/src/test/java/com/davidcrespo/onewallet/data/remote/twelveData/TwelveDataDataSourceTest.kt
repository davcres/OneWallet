package com.davidcrespo.onewallet.data.remote.twelveData

import com.davidcrespo.onewallet.data.remote.twelveData.models.RateResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TwelveDataDataSourceTest {

    private val apiClient = mockk<TwelveDataApiClient>()
    private val dataSource = TwelveDataDataSource(apiClient)

    @Test
    fun `getRate devuelve el RateResponse del API client`() = runTest {
        // Given
        val from = "EUR"
        val to = "USD"
        val mockResponse = RateResponse(symbol = "EUR/USD", rate = 1.0850)
        coEvery { apiClient.getRate(from, to) } returns mockResponse

        // When
        val result = dataSource.getRate(from, to)

        // Then
        assertEquals(mockResponse, result)
        assertEquals(1.0850, result.rate, 0.0)
    }
}
