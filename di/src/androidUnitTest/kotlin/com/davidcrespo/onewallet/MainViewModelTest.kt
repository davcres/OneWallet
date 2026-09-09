package com.davidcrespo.onewallet

import app.cash.turbine.test
import com.davidcrespo.onewallet.domain.model.ThemeMode
import com.davidcrespo.onewallet.domain.usecase.appRoot.GetThemeUseCase
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val getThemeUseCase = mockk<GetThemeUseCase>()

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `themeMode emits initial value and collected theme mode from use case`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            every { getThemeUseCase() } returns flowOf(ThemeMode.DARK)

            val viewModel = MainViewModel(getThemeUseCase)

            viewModel.themeMode.test {
                assertEquals(ThemeMode.DARK, awaitItem())
            }
        }
}
