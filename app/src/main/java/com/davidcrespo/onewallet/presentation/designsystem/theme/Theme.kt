package com.davidcrespo.onewallet.presentation.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.davidcrespo.onewallet.core.models.ThemeMode

val DarkScheme = darkColorScheme(
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

val LightScheme = lightColorScheme(
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
)

private val LightGradients = AppGradients(
    cardGlow = Brush.horizontalGradient(
        colors = listOf(
            SecondaryLight.copy(alpha = 0.03f),
            SecondaryLight.copy(alpha = 0.08f)
        )
    )
)

private val DarkGradients = AppGradients(
    cardGlow = Brush.horizontalGradient(
        colors = listOf(
            TertiaryContainer,
            MintPrimary.copy(alpha = 0.10f)
        )
    )
)


@Composable
fun OneWalletTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (darkTheme) DarkScheme else LightScheme
    val gradients = if (darkTheme) DarkGradients else LightGradients

    CompositionLocalProvider(
        LocalAppGradients provides gradients
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

@Immutable
data class AppGradients(
    val cardGlow: Brush
)

private val LocalAppGradients = staticCompositionLocalOf<AppGradients> {
    error("No AppGradients provided")
}

val MaterialTheme.gradients: AppGradients
    @Composable
    @ReadOnlyComposable
    get() = LocalAppGradients.current
