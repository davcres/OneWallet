package com.davidcrespo.onewallet.feature.onboarding.util

import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable

@Composable
actual fun rememberOnboardingContentWindowInsets(): WindowInsets {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        WindowInsets(0, 0, 0, 0)
    } else {
        ScaffoldDefaults.contentWindowInsets
    }
}
