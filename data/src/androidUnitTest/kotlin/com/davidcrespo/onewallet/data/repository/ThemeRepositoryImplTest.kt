package com.davidcrespo.onewallet.data.repository

import app.cash.turbine.test
import com.davidcrespo.onewallet.domain.model.ThemeMode
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ThemeRepositoryImplTest {

    private lateinit var settings: MapSettings
    private lateinit var repository: ThemeRepositoryImpl

    @Test
    fun `initial emission should be DARK when saved theme is DARK`() = runTest {
        settings = MapSettings("app_theme" to ThemeMode.DARK.name)
        repository = ThemeRepositoryImpl(settings)

        repository.themeModeFlow.test {
            assertEquals(ThemeMode.DARK, awaitItem())
        }
    }

    @Test
    fun `initial emission should be LIGHT when saved theme is LIGHT`() = runTest {
        settings = MapSettings("app_theme" to ThemeMode.LIGHT.name)
        repository = ThemeRepositoryImpl(settings)

        repository.themeModeFlow.test {
            assertEquals(ThemeMode.LIGHT, awaitItem())
        }
    }

    @Test
    fun `initial emission should be DARK when no theme is saved (default)`() = runTest {
        settings = MapSettings()
        repository = ThemeRepositoryImpl(settings)

        repository.themeModeFlow.test {
            assertEquals(ThemeMode.DARK, awaitItem())
        }
    }

    @Test
    fun `setThemeMode should save to SharedPreferences and emit new value`() = runTest {
        settings = MapSettings()
        repository = ThemeRepositoryImpl(settings)

        repository.themeModeFlow.test {
            assertEquals(ThemeMode.DARK, awaitItem()) // Initial default

            repository.setThemeMode(ThemeMode.LIGHT)

            assertEquals(ThemeMode.LIGHT, awaitItem()) // New value
            assertEquals(ThemeMode.LIGHT.name, settings.getString("app_theme", ""))
        }
    }
}
