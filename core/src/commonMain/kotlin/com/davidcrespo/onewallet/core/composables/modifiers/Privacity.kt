package com.davidcrespo.onewallet.core.composables.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Extension function to apply a privacy-sensitive effect when the app enters the recent apps menu.
 * Supports different privacy effects like blackout (redact) and blur.
 *
 * @param effect The privacy effect to apply. Defaults to `Redact(Color.Black)`.
 *
 * Usage Example:
 * ```
 * Modifier.privacySensitive() // Defaults to Redact(Black)
 * Modifier.privacySensitive(PrivacyEffect.Blur(10.dp)) // Applies a blur effect
 * Modifier.privacySensitive(PrivacyEffect.Redact(Color.Gray)) // Redacts with gray overlay
 * ```
 */
@Composable
fun Modifier.privacySensitive(hideContent: Boolean, effect: PrivacyEffect = PrivacyEffect.Blur(16.dp)): Modifier {
    val windowInfo = LocalWindowInfo.current
    val isInRecentApps by rememberUpdatedState(!windowInfo.isWindowFocused)
    val shouldHide = isInRecentApps || hideContent

    return this
        .then(
            if (shouldHide) {
                Modifier.clearAndSetSemantics { }
            } else Modifier
        )
        .then(
            if (shouldHide) {
                when (effect) {
                    is PrivacyEffect.Redact -> Modifier.applyRedact(effect.color)
                    is PrivacyEffect.Blur -> Modifier.applyBlur(effect.blurRadius)
                }
            } else Modifier
        )
}

/**
 * Applies a solid color overlay to redact the content when the app loses focus.
 *
 * @param color The color used for redaction. Default is black.
 */
private fun Modifier.applyRedact(color: Color = Color.Black) = drawWithContent {
    drawContent()
    drawRect(color)
}

/**
 * Applies a blur effect to obscure content when the app loses focus.
 *
 * @param blurRadius The radius of the blur effect in Dp. Default is 16.dp.
 */
fun Modifier.applyBlur(blurRadius: Dp = 16.dp): Modifier = this.privacyBlur(blurRadius)

/**
 * Sealed class defining different privacy effects.
 */
sealed class PrivacyEffect {
    /**
     * Redact effect applies a solid color overlay.
     *
     * @param color The color of the overlay. Default is black.
     */
    data class Redact(val color: Color = Color.Black) : PrivacyEffect()

    /**
     * Blur effect applies a blur filter.
     *
     * @param blurRadius The intensity of the blur effect. Default is 16.dp.
     */
    data class Blur(val blurRadius: Dp = 16.dp) : PrivacyEffect()
}
