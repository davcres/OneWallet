package com.davidcrespo.onewallet.data.repository

import android.content.SharedPreferences
import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

private const val THEME_PREFERENCE_KEY = "app_theme"

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    private val _themeModeFlow = MutableStateFlow(getSavedTheme())
    override val themeModeFlow: Flow<ThemeMode> = _themeModeFlow

    override suspend fun setThemeMode(theme: ThemeMode) {
        sharedPreferences.edit()
            .putString(THEME_PREFERENCE_KEY, theme.name)
            .apply()

        _themeModeFlow.value = theme
    }

    private fun getSavedTheme(): ThemeMode {
        return when (sharedPreferences.getString(THEME_PREFERENCE_KEY, ThemeMode.DARK.name)) {
            ThemeMode.DARK.name -> ThemeMode.DARK
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            else -> ThemeMode.SYSTEM
        }
    }
}

