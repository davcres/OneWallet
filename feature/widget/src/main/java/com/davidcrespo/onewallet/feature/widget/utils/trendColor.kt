package com.davidcrespo.onewallet.feature.widget.utils

import androidx.glance.unit.ColorProvider
import com.davidcrespo.onewallet.core.designsystem.theme.Error
import com.davidcrespo.onewallet.core.designsystem.theme.MintPrimary
import com.davidcrespo.onewallet.core.designsystem.theme.OnSurfaceVariantDark

fun trendColor(value: Double): ColorProvider {
    return when {
        value > 0 -> ColorProvider(MintPrimary)
        value < 0 -> ColorProvider(Error)
        else -> ColorProvider(OnSurfaceVariantDark)
    }
}