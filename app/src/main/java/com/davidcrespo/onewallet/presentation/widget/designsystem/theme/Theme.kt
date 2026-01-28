package com.davidcrespo.onewallet.presentation.widget.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.glance.material3.ColorProviders
import com.davidcrespo.onewallet.presentation.designsystem.theme.BgDark
import com.davidcrespo.onewallet.presentation.designsystem.theme.Error
import com.davidcrespo.onewallet.presentation.designsystem.theme.ErrorContainer
import com.davidcrespo.onewallet.presentation.designsystem.theme.InverseOnSurface
import com.davidcrespo.onewallet.presentation.designsystem.theme.InversePrimary
import com.davidcrespo.onewallet.presentation.designsystem.theme.InverseSurface
import com.davidcrespo.onewallet.presentation.designsystem.theme.MintContainer
import com.davidcrespo.onewallet.presentation.designsystem.theme.MintPrimary
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnBgDark
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnError
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnErrorContainer
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnMintContainer
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnMintPrimary
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnSecondaryContainer
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnSecondaryGreen
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnSurfaceDark
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnSurfaceVariantDark
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnTertiaryContainer
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnTertiaryTeal
import com.davidcrespo.onewallet.presentation.designsystem.theme.OutlineDark
import com.davidcrespo.onewallet.presentation.designsystem.theme.OutlineVariantDark
import com.davidcrespo.onewallet.presentation.designsystem.theme.SecondaryContainer
import com.davidcrespo.onewallet.presentation.designsystem.theme.SecondaryGreen
import com.davidcrespo.onewallet.presentation.designsystem.theme.SurfaceDark
import com.davidcrespo.onewallet.presentation.designsystem.theme.SurfaceVariantDark
import com.davidcrespo.onewallet.presentation.designsystem.theme.TertiaryContainer
import com.davidcrespo.onewallet.presentation.designsystem.theme.TertiaryTeal

val WidgetColors = ColorProviders(
    light = ColorScheme(
        primary = MintPrimary,
        onPrimary = OnMintPrimary,
        primaryContainer = MintContainer,
        onPrimaryContainer = OnMintContainer,

        secondary = SecondaryGreen,
        onSecondary = OnSecondaryGreen,
        secondaryContainer = SecondaryContainer,
        onSecondaryContainer = OnSecondaryContainer,

        tertiary = TertiaryTeal,
        onTertiary = OnTertiaryTeal,
        tertiaryContainer = TertiaryContainer,
        onTertiaryContainer = OnTertiaryContainer,

        // Error
        error = Error,
        onError = OnError,
        errorContainer = ErrorContainer,
        onErrorContainer = OnErrorContainer,

        // Background & surfaces
        background = BgDark,
        onBackground = OnBgDark,

        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = OnSurfaceVariantDark,

        // Outlines
        outline = OutlineDark,
        outlineVariant = OutlineVariantDark,

        // Inverse
        inverseSurface = InverseSurface,
        inverseOnSurface = InverseOnSurface,
        inversePrimary = InversePrimary,

        // Extras útiles en M3
        surfaceTint = MintPrimary,          // tint de elevación (si usas tonal elevation)
        scrim = Color(0xFF000000)           // típico en dark
    ),
    dark = ColorScheme(
        primary = MintPrimary,
        onPrimary = OnMintPrimary,
        primaryContainer = MintContainer,
        onPrimaryContainer = OnMintContainer,

        secondary = SecondaryGreen,
        onSecondary = OnSecondaryGreen,
        secondaryContainer = SecondaryContainer,
        onSecondaryContainer = OnSecondaryContainer,

        tertiary = TertiaryTeal,
        onTertiary = OnTertiaryTeal,
        tertiaryContainer = TertiaryContainer,
        onTertiaryContainer = OnTertiaryContainer,

        // Error
        error = Error,
        onError = OnError,
        errorContainer = ErrorContainer,
        onErrorContainer = OnErrorContainer,

        // Background & surfaces
        background = BgDark,
        onBackground = OnBgDark,

        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = OnSurfaceVariantDark,

        // Outlines
        outline = OutlineDark,
        outlineVariant = OutlineVariantDark,

        // Inverse
        inverseSurface = InverseSurface,
        inverseOnSurface = InverseOnSurface,
        inversePrimary = InversePrimary,

        // Extras útiles en M3
        surfaceTint = MintPrimary,          // tint de elevación (si usas tonal elevation)
        scrim = Color(0xFF000000)           // típico en dark
    )
)
