package com.davidcrespo.onewallet.feature.widget.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.glance.material3.ColorProviders
import com.davidcrespo.onewallet.core.designsystem.theme.BgDark
import com.davidcrespo.onewallet.core.designsystem.theme.BgLight
import com.davidcrespo.onewallet.core.designsystem.theme.Error
import com.davidcrespo.onewallet.core.designsystem.theme.ErrorContainer
import com.davidcrespo.onewallet.core.designsystem.theme.InverseOnSurface
import com.davidcrespo.onewallet.core.designsystem.theme.InverseOnSurfaceLight
import com.davidcrespo.onewallet.core.designsystem.theme.InversePrimary
import com.davidcrespo.onewallet.core.designsystem.theme.InversePrimaryLight
import com.davidcrespo.onewallet.core.designsystem.theme.InverseSurface
import com.davidcrespo.onewallet.core.designsystem.theme.InverseSurfaceLight
import com.davidcrespo.onewallet.core.designsystem.theme.MintContainer
import com.davidcrespo.onewallet.core.designsystem.theme.MintPrimary
import com.davidcrespo.onewallet.core.designsystem.theme.OnBgDark
import com.davidcrespo.onewallet.core.designsystem.theme.OnBgLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnError
import com.davidcrespo.onewallet.core.designsystem.theme.OnErrorContainer
import com.davidcrespo.onewallet.core.designsystem.theme.OnMintContainer
import com.davidcrespo.onewallet.core.designsystem.theme.OnMintPrimary
import com.davidcrespo.onewallet.core.designsystem.theme.OnPrimaryContainerLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnPrimaryLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnSecondaryContainer
import com.davidcrespo.onewallet.core.designsystem.theme.OnSecondaryContainerLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnSecondaryGreen
import com.davidcrespo.onewallet.core.designsystem.theme.OnSecondaryLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnSurfaceDark
import com.davidcrespo.onewallet.core.designsystem.theme.OnSurfaceLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnSurfaceVariantDark
import com.davidcrespo.onewallet.core.designsystem.theme.OnSurfaceVariantLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnTertiaryContainer
import com.davidcrespo.onewallet.core.designsystem.theme.OnTertiaryContainerLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnTertiaryLight
import com.davidcrespo.onewallet.core.designsystem.theme.OnTertiaryTeal
import com.davidcrespo.onewallet.core.designsystem.theme.OutlineDark
import com.davidcrespo.onewallet.core.designsystem.theme.OutlineLight
import com.davidcrespo.onewallet.core.designsystem.theme.OutlineVariantDark
import com.davidcrespo.onewallet.core.designsystem.theme.OutlineVariantLight
import com.davidcrespo.onewallet.core.designsystem.theme.PrimaryContainerLight
import com.davidcrespo.onewallet.core.designsystem.theme.PrimaryLight
import com.davidcrespo.onewallet.core.designsystem.theme.ScrimDark
import com.davidcrespo.onewallet.core.designsystem.theme.SecondaryContainer
import com.davidcrespo.onewallet.core.designsystem.theme.SecondaryContainerLight
import com.davidcrespo.onewallet.core.designsystem.theme.SecondaryGreen
import com.davidcrespo.onewallet.core.designsystem.theme.SecondaryLight
import com.davidcrespo.onewallet.core.designsystem.theme.SurfaceDark
import com.davidcrespo.onewallet.core.designsystem.theme.SurfaceLight
import com.davidcrespo.onewallet.core.designsystem.theme.SurfaceVariantDark
import com.davidcrespo.onewallet.core.designsystem.theme.SurfaceVariantLight
import com.davidcrespo.onewallet.core.designsystem.theme.TertiaryContainer
import com.davidcrespo.onewallet.core.designsystem.theme.TertiaryContainerLight
import com.davidcrespo.onewallet.core.designsystem.theme.TertiaryLight
import com.davidcrespo.onewallet.core.designsystem.theme.TertiaryTeal

val WidgetColors = ColorProviders(
    light = ColorScheme(
        primary = PrimaryLight,
        onPrimary = OnPrimaryLight,
        primaryContainer = PrimaryContainerLight,
        onPrimaryContainer = OnPrimaryContainerLight,

        secondary = SecondaryLight,
        onSecondary = OnSecondaryLight,
        secondaryContainer = SecondaryContainerLight,
        onSecondaryContainer = OnSecondaryContainerLight,

        tertiary = TertiaryLight,
        onTertiary = OnTertiaryLight,
        tertiaryContainer = TertiaryContainerLight,
        onTertiaryContainer = OnTertiaryContainerLight,

        // Error
        error = Error,
        onError = OnError,
        errorContainer = ErrorContainer,
        onErrorContainer = OnErrorContainer,

        // Background & surfaces
        background = BgLight,
        onBackground = OnBgLight,

        surface = SurfaceLight,
        onSurface = OnSurfaceLight,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = OnSurfaceVariantLight,

        // Outlines
        outline = OutlineLight,
        outlineVariant = OutlineVariantLight,

        // Inverse
        inverseSurface = InverseSurfaceLight,
        inverseOnSurface = InverseOnSurfaceLight,
        inversePrimary = InversePrimaryLight,

        surfaceTint = PrimaryLight,
        scrim = Color.Black.copy(alpha = 0.32f)
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

        surfaceTint = MintPrimary,
        scrim = ScrimDark
    )
)
