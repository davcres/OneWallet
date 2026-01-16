package com.davidcrespo.onewallet.core.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

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

    var viewportWidthPx by remember { mutableIntStateOf(0) }
    var contentWidthPx by remember { mutableIntStateOf(0) }

    // Solo tiene sentido si el contenido es más ancho que el viewport
    val maxScroll = (contentWidthPx - viewportWidthPx).coerceAtLeast(0)

    LaunchedEffect(text, viewportWidthPx, contentWidthPx) {
        if (maxScroll <= 0) return@LaunchedEffect

        // Loop: 0 -> max -> 0 -> ...
        while (true) {
            delay(edgePauseMs)
            scrollState.animateScrollTo(
                value = maxScroll,
                animationSpec = androidx.compose.animation.core.tween(
                    durationMillis = ((maxScroll / speedPxPerSecond) * 1000).toInt().coerceAtLeast(1),
                    easing = androidx.compose.animation.core.LinearEasing
                )
            )
            delay(edgePauseMs)
            scrollState.animateScrollTo(
                value = 0,
                animationSpec = androidx.compose.animation.core.tween(
                    durationMillis = ((maxScroll / speedPxPerSecond) * 1000).toInt().coerceAtLeast(1),
                    easing = androidx.compose.animation.core.LinearEasing
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { viewportWidthPx = it.width }
            .horizontalScroll(scrollState, enabled = false) // desactiva scroll manual
    ) {
        Text(
            text = text,
            style = style,
            color = color,
            fontWeight = fontWeight,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.onSizeChanged { contentWidthPx = it.width }
        )
    }
}

@Preview
@Composable
private fun AutoScrollingTextPreview() {
    AutoScrollingText(
        text = "Esto es una línea muy larga que se mueve de izquierda a derecha y vuelve al inicio.",
        speedPxPerSecond = 140f,
        edgePauseMs = 800L
    )
}