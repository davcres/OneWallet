package com.davidcrespo.onewallet.presentation.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Accent
val MintPrimary = Color(0xFF35F28F)
val OnMintPrimary = Color(0xFF052112)

val MintContainer = Color(0xFF123826)
val OnMintContainer = Color(0xFFC8F7DD)

// Secondary
val SecondaryGreen = Color(0xFF22C55E)
val OnSecondaryGreen = OnMintPrimary
val SecondaryContainer = Color(0xFF0F2A1D)
val OnSecondaryContainer = Color(0xFFB7F3CE)

// Tertiary (Teal/Cyan accent for Dark)
val TertiaryTeal = Color(0xFF2DD4BF)
val OnTertiaryTeal = Color(0xFF001F1A)
val TertiaryContainer = Color(0xFF0D3A33)
val OnTertiaryContainer = Color(0xFFB8FFF2)

// Background & Surfaces
val BgDark = Color(0xFF07130E)
val OnBgDark = Color(0xFFF2F5F3)

val SurfaceDark = Color(0xFF0B1712)
val OnSurfaceDark = OnBgDark

val SurfaceVariantDark = Color(0xFF0F221A)
val OnSurfaceVariantDark = Color(0xFFB9C6C0)

// Outlines
val OutlineDark = Color(0xFF1D3A2D)
val OutlineVariantDark = Color(0xFF132A21)

// States
val Success = MintPrimary
val Error = Color(0xFFFF5A5F)

// Error tokens (Material3)
val OnError = Color(0xFFFFD6D8)          // mejor legibilidad en dark que un onError oscuro
val ErrorContainer = Color(0xFF3B1214)
val OnErrorContainer = Color(0xFFFFD6D8)

// Extras (Material3)
val InverseSurface = Color(0xFFE9EEEC)
val InverseOnSurface = Color(0xFF12201A)
val InversePrimary = Color(0xFF1FBF67)



// Glows (look 1:1)
val HeaderGlowInner = Color(0xFF163728)
val HeaderGlowOuter = SurfaceDark

val CardGlowInner = Color(0xFF1a3627)
val CardGlowOuter = Color(0xFF1b2620)

val ChipGlowInner = Color(0xFF1A4A33)
val ChipGlowOuter = MintContainer

/**
 * Fondo general tipo “pane” con un toque de gradiente muy sutil.
 * Si quieres 100% plano, usa solo BgDark.
 */
val AppBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF091A12),
        BgDark
    )
)

/** Glow radial como en la card del balance */
fun cardGlowBrush(): Brush = Brush.horizontalGradient(
        listOf(
            CardGlowOuter,
            CardGlowInner
        )
    )

/** Glow del header (Portfolio Overview) */
fun headerGlowBrush(): Brush = Brush.radialGradient(
    colors = listOf(HeaderGlowInner, HeaderGlowOuter),
    radius = 1000f
)

/** Chip positivo (subida) */
fun positiveChipBrush(): Brush = Brush.horizontalGradient(
    colors = listOf(ChipGlowInner, ChipGlowOuter)
)
