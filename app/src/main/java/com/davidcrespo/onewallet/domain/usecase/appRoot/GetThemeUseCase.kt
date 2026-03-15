package com.davidcrespo.onewallet.domain.usecase.appRoot

import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class GetThemeUseCase(
    private val repo: ThemeRepository
) {
    operator fun invoke(): Flow<ThemeMode> = repo.themeModeFlow
}
