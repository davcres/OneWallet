package com.davidcrespo.onewallet.core.composables.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

expect fun Modifier.privacyBlur(
    radius: Dp = 16.dp
): Modifier
