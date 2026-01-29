package com.davidcrespo.onewallet.core.composables.charts.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AssetSlice(
    val value: Double,
    val color: Color
)
