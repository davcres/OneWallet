package com.davidcrespo.onewallet.domain.usecase.appRoot

import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.repository.ThemeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SetThemeUseCaseTest {

    private val repo = mockk<ThemeRepository>()
    private val useCase = SetThemeUseCase(repo)

    @Test
    fun `invoke should call setThemeMode on repository`() = runTest {
        val themeMode = ThemeMode.LIGHT
        coEvery { repo.setThemeMode(themeMode) } returns Unit

        useCase(themeMode)

        coVerify(exactly = 1) { repo.setThemeMode(themeMode) }
    }
}
