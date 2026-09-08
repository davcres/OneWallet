package com.davidcrespo.onewallet.core.composables.modifiers.animations

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo

fun Modifier.pulse(
    minScale: Float = 0.9f,
    maxScale: Float = 1.1f,
    minAlpha: Float = 0.75f,
    maxAlpha: Float = 1f,
    durationMillis: Int = 900,
): Modifier = this.then(
    Modifier.composed(
        inspectorInfo = debugInspectorInfo { name = "pulse" }
    ) {
        val transition = rememberInfiniteTransition(label = "pulse")
        val scale = transition.animateFloat(
            initialValue = minScale,
            targetValue = maxScale,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        ).value

        val alpha = transition.animateFloat(
            initialValue = minAlpha,
            targetValue = maxAlpha,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        ).value

        Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(alpha)
    }
)
