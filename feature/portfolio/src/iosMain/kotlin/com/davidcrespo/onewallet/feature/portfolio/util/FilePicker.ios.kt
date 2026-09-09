package com.davidcrespo.onewallet.feature.portfolio.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberFilePicker(onFileSelected: (String) -> Unit): () -> Unit {
    return {}
}
