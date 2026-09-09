package com.davidcrespo.onewallet.core.designsystem.composables

import androidx.compose.runtime.Composable

@Composable
actual fun OWShakeListener(
    enabled: Boolean,
    onShake: () -> Unit,
    threshold: Float,
    slopTimeMs: Long
) {
    // iOS shake listener placeholder (CoreMotion or ShakeMotion notification)
}
