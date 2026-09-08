package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.domain.model.ThemeMode
import com.davidcrespo.onewallet.domain.repository.ThemeRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

private const val THEME_PREFERENCE_KEY = "app_theme"

class ThemeRepositoryImpl(
    private val settings: Settings
) : ThemeRepository {

    private val _themeModeFlow = MutableStateFlow(getSavedTheme())
    override val themeModeFlow: Flow<ThemeMode> = _themeModeFlow

    override suspend fun setThemeMode(theme: ThemeMode) {
        settings.putString(THEME_PREFERENCE_KEY, theme.name)
        _themeModeFlow.value = theme
    }

    private fun getSavedTheme(): ThemeMode {
        return when (settings.getString(THEME_PREFERENCE_KEY, ThemeMode.DARK.name)) {
            ThemeMode.DARK.name -> ThemeMode.DARK
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            else -> ThemeMode.SYSTEM
        }
    }
}
