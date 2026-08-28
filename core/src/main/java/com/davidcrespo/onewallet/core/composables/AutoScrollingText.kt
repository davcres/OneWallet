package com.davidcrespo.onewallet.core.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun AutoScrollingText(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier,
    // px por segundo (más alto = más rápido)
    speedPxPerSecond: Float = 120f,
    // pausa al llegar a los extremos
    edgePauseMs: Long = 1000L,
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(text) {
        while (true) {
            // Wait for content to be scrollable
            var maxScroll = scrollState.maxValue
            if (maxScroll <= 0) {
                maxScroll = snapshotFlow { scrollState.maxValue }.first { it > 0 }
            }

            // Pause at start
            delay(edgePauseMs)

            // Scroll to End
            maxScroll = scrollState.maxValue
            if (maxScroll > 0) {
                val duration = ((maxScroll.toFloat() / speedPxPerSecond) * 1000).toInt().coerceAtLeast(1)
                scrollState.animateScrollTo(
                    maxScroll,
                    animationSpec = tween(duration, easing = LinearEasing)
                )
            }

            // Pause at End
            delay(edgePauseMs)

            // Scroll to Start
            val currentPos = scrollState.value
            if (currentPos > 0) {
                val duration = ((currentPos.toFloat() / speedPxPerSecond) * 1000).toInt().coerceAtLeast(1)
                scrollState.animateScrollTo(
                    0,
                    animationSpec = tween(duration, easing = LinearEasing)
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState, enabled = false) // desactiva scroll manual
    ) {
        Text(
            text = text,
            style = style,
            color = color,
            fontWeight = fontWeight,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )
    }
}

@Preview
@Composable
private fun AutoScrollingTextPreview() {
    OneWalletTheme {
        AutoScrollingText(
            text = "Esto es una línea muy larga que se mueve de izquierda a derecha y vuelve al inicio.",
            speedPxPerSecond = 140f,
            edgePauseMs = 800L
        )
    }
}
