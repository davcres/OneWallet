package com.davidcrespo.onewallet.core.composables.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.privacyBlur(
    radius: Dp = 16.dp,
): Modifier = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
    this.then(
        Modifier.blur(radius)
    )
} else {
    // Android 11 y anteriores: NO hay blur real
    this
}
