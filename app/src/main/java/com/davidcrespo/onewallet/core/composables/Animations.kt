package com.davidcrespo.onewallet.core.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo

inline fun Modifier.applyIf(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (condition) this.block() else this

enum class ButtonState { Pressed, Idle }
fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.97f else 1f)

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {  }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Modifier.shakeClickEffect() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }

    val tx by animateFloatAsState(
        targetValue = if (buttonState == ButtonState.Pressed) 50f else 0f,
        animationSpec = repeatable(
            iterations = 2,
            animation = tween(durationMillis = 50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    this
        .graphicsLayer {
            translationX = tx
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

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
