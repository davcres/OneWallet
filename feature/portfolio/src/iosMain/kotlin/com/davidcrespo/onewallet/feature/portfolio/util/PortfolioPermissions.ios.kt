package com.davidcrespo.onewallet.feature.portfolio.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberNotificationPermissionRequester(onPermissionResult: (Boolean) -> Unit): () -> Unit {
    return {
        onPermissionResult(true)
    }
}
