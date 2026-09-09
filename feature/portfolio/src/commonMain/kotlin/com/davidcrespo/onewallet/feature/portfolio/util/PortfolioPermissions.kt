package com.davidcrespo.onewallet.feature.portfolio.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionRequester(onPermissionResult: (Boolean) -> Unit): () -> Unit
