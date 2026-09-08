package com.davidcrespo.onewallet.core.composables.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp

actual fun Modifier.privacyBlur(
    radius: Dp
): Modifier = this.then(Modifier.blur(radius))
