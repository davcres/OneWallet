package com.davidcrespo.onewallet.domain.usecase.appRoot

import app.cash.turbine.test
import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.repository.ThemeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetThemeUseCaseTest {

    private val repo = mockk<ThemeRepository>()
    private val useCase = GetThemeUseCase(repo)

    @Test
    fun `invoke should return themeModeFlow from repository`() = runTest {
        val expectedMode = ThemeMode.DARK
        every { repo.themeModeFlow } returns flowOf(expectedMode)

        useCase().test {
            assertEquals(expectedMode, awaitItem())
            awaitComplete()
        }
    }
}
