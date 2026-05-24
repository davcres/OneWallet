package com.davidcrespo.onewallet.core.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

@Composable
fun AnimatedCounter(
    targetValue: Double,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
    prefix: String = "",
    suffix: String = "",
    shouldAnimate: Boolean = true
) {
    val targetValueFloat = targetValue
        .toFloat()
        .takeIf { it.isFinite() }
        ?.coerceIn(-Float.MAX_VALUE, Float.MAX_VALUE)
        ?: Float.MAX_VALUE

    val animatedValue = remember {
        Animatable(targetValueFloat)
    }

    val formattedTargetValue = remember(targetValue, prefix, suffix) {
        "$prefix${"%.2f".format(targetValue)}$suffix"
    }

    LaunchedEffect(targetValueFloat, shouldAnimate) {
        if (shouldAnimate) {
            animatedValue.animateTo(
                targetValue = targetValueFloat,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
        } else {
            animatedValue.snapTo(targetValueFloat)
        }
    }

    Text(
        text = "$prefix${"%.2f".format(animatedValue.value)}$suffix",
        modifier = modifier.semantics {
            contentDescription = formattedTargetValue
        },
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        style = style
    )
}
