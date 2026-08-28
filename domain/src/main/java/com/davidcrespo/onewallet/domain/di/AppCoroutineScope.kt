package com.davidcrespo.onewallet.domain.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AppCoroutineScope(
    dispatcher: DispatcherProvider
) {
    // SupervisorJob para que un fallo no cancele otros jobs si cargamos varios datos a la vez
    // Este en vez de viewModelScope, ya que se cancelaria en cuanto se pase a otra pantalla
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher.io)

    fun cancel() = scope.cancel()
}