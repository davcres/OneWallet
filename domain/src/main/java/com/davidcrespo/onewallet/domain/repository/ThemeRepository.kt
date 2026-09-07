package com.davidcrespo.onewallet.domain.repository

import com.davidcrespo.onewallet.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeModeFlow: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
