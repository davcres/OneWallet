package com.davidcrespo.onewallet.domain.usecase.appRoot

import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.repository.ThemeRepository


class SetThemeUseCase(
    private val repo: ThemeRepository
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        repo.setThemeMode(themeMode)
    }
}
