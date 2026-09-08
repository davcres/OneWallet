package com.davidcrespo.onewallet.feature.portfolio.util

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun rememberFilePicker(onFileSelected: (String) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onFileSelected(it.toString()) }
    }
    return { launcher.launch("*/*") }
}
