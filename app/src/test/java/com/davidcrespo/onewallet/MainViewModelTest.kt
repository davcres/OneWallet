package com.davidcrespo.onewallet

import app.cash.turbine.test
import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.usecase.appRoot.GetThemeUseCase
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val getThemeUseCase = mockk<GetThemeUseCase>()
    private val themeFlow = MutableStateFlow(ThemeMode.SYSTEM)

    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        every { getThemeUseCase() } returns themeFlow
    }

    private fun createViewModel() {
        viewModel = MainViewModel(getThemeUseCase)
    }

    @Test
    fun `themeMode should initial emit SYSTEM and update when use case emits new values`() = runTest {
        createViewModel()

        viewModel.themeMode.test {
            // Initial value from stateIn or flow
            assertEquals(ThemeMode.SYSTEM, awaitItem())

            // Update flow
            themeFlow.value = ThemeMode.DARK
            assertEquals(ThemeMode.DARK, awaitItem())

            // Update flow again
            themeFlow.value = ThemeMode.LIGHT
            assertEquals(ThemeMode.LIGHT, awaitItem())
        }
    }

    @Test
    fun `themeMode should not emit when use case emits same value`() = runTest {
        createViewModel()

        viewModel.themeMode.test {
            assertEquals(ThemeMode.SYSTEM, awaitItem())

            // Emit same value
            themeFlow.value = ThemeMode.SYSTEM
            // No new item should be emitted due to distinctUntilChanged
            expectNoEvents()

            // Emit different value
            themeFlow.value = ThemeMode.DARK
            assertEquals(ThemeMode.DARK, awaitItem())
        }
    }
}
