package com.davidcrespo.onewallet.data.repository

import android.content.SharedPreferences
import app.cash.turbine.test
import com.davidcrespo.onewallet.domain.model.ThemeMode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ThemeRepositoryImplTest {

    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private lateinit var repository: ThemeRepositoryImpl

    @BeforeEach
    fun setUp() {
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
    }

    @Test
    fun `initial emission should be DARK when saved theme is DARK`() = runTest {
        every { sharedPreferences.getString("app_theme", any()) } returns ThemeMode.DARK.name
        repository = ThemeRepositoryImpl(sharedPreferences)

        repository.themeModeFlow.test {
            assertEquals(ThemeMode.DARK, awaitItem())
        }
    }

    @Test
    fun `initial emission should be LIGHT when saved theme is LIGHT`() = runTest {
        every { sharedPreferences.getString("app_theme", any()) } returns ThemeMode.LIGHT.name
        repository = ThemeRepositoryImpl(sharedPreferences)

        repository.themeModeFlow.test {
            assertEquals(ThemeMode.LIGHT, awaitItem())
        }
    }

    @Test
    fun `initial emission should be DARK when no theme is saved (default)`() = runTest {
        // Al no haber nada, el repo recibe el default "DARK" que él mismo pide
        every { sharedPreferences.getString("app_theme", any()) } returns ThemeMode.DARK.name
        repository = ThemeRepositoryImpl(sharedPreferences)

        repository.themeModeFlow.test {
            assertEquals(ThemeMode.DARK, awaitItem())
        }
    }

    @Test
    fun `setThemeMode should save to SharedPreferences and emit new value`() = runTest {
        every { sharedPreferences.getString("app_theme", any()) } returns ThemeMode.DARK.name
        repository = ThemeRepositoryImpl(sharedPreferences)

        repository.themeModeFlow.test {
            assertEquals(ThemeMode.DARK, awaitItem()) // Initial default

            repository.setThemeMode(ThemeMode.LIGHT)
            
            assertEquals(ThemeMode.LIGHT, awaitItem()) // New value
            
            verify { 
                editor.putString("app_theme", ThemeMode.LIGHT.name)
                editor.apply()
            }
        }
    }
}
