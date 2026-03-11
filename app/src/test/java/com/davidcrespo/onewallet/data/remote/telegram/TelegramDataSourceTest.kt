package com.davidcrespo.onewallet.data.remote.telegram

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TelegramDataSourceTest {

    private val apiClient = mockk<TelegramApiClient>()
    private val dataSource = TelegramDataSource(apiClient)

    @Test
    fun `sendMessage llama correctamente al API client`() = runTest {
        // Given
        val message = "Test Message"
        coEvery { apiClient.sendMessage(message) } returns Unit

        // When
        dataSource.sendMessage(message)

        // Then
        coVerify(exactly = 1) { apiClient.sendMessage(message) }
    }
}
