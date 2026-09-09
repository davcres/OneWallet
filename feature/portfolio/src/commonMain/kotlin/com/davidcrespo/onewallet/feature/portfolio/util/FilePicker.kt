package com.davidcrespo.onewallet.feature.portfolio.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePicker(onFileSelected: (String) -> Unit): () -> Unit
