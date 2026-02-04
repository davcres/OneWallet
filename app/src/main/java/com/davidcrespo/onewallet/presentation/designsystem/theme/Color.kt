package com.davidcrespo.onewallet.presentation.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Accent
val MintPrimary = Color(0xFF35F28F)
val OnMintPrimary = Color(0xFF062114)

val MintContainer = Color(0xFF163A2A)
val OnMintContainer = Color(0xFFD8FFE9)


// Secondary
val SecondaryGreen = Color(0xFF22C55E)
val OnSecondaryGreen = Color(0xFF052112)

val SecondaryContainer = Color(0xFF143224)
val OnSecondaryContainer = Color(0xFFCFF7DE)

// Tertiary
val TertiaryTeal = Color(0xFF2DD4BF)
val OnTertiaryTeal = Color(0xFF001F1A)

val TertiaryContainer = Color(0xFF113833)
val OnTertiaryContainer = Color(0xFFB8FFF2)

// Background & Surfaces
val BgDark = Color(0xFF091A12)
val OnBgDark = Color(0xFFEAF1ED)

val SurfaceDark = Color(0xFF0F1412)
val OnSurfaceDark = Color(0xFFEAF1ED)

val SurfaceVariantDark = Color(0xFF161C19)
val OnSurfaceVariantDark = Color(0xFFB8C6BF)

// Outlines
val OutlineDark = Color(0xFF2A3430)
val OutlineVariantDark = Color(0xFF1F2724)

val Error = Color(0xFFFF5A5F)
val OnError = Color(0xFF2B0B0D)
val ErrorContainer = Color(0xFF3B1214)
val OnErrorContainer = Color(0xFFFFD6D8)

// Extras (Material3)
val InverseSurface = Color(0xFFE7EEE9)
val InverseOnSurface = Color(0xFF101513)
val InversePrimary = Color(0xFF1FBF67)

val ScrimDark = Color(0xFF000000)

val CardGlowInner = MintPrimary.copy(alpha = 0.10f)
val ItemBackground = Color(0xFF1B2E27)
val ItemBorder = Color(0x1AFFFFFF)

fun cardGlowBrush(): Brush = Brush.horizontalGradient(
    listOf(ItemBackground, CardGlowInner)
)
