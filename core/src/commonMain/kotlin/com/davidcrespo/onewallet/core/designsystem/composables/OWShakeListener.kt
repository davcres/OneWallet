package com.davidcrespo.onewallet.core.designsystem.composables

import androidx.compose.runtime.Composable

@Composable
expect fun OWShakeListener(
    enabled: Boolean = true,
    onShake: () -> Unit,
    threshold: Float = 5f,
    slopTimeMs: Long = 1000L
)
