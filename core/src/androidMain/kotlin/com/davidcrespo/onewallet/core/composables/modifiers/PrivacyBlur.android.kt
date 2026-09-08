package com.davidcrespo.onewallet.core.composables.modifiers

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp

actual fun Modifier.privacyBlur(
    radius: Dp
): Modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    this.then(Modifier.blur(radius))
} else {
    this
}
