package com.davidcrespo.onewallet.presentation.historical.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.davidcrespo.onewallet.presentation.designsystem.theme.CardGlowOuter
import kotlinx.coroutines.delay

@Composable
fun ComicBubblePopup(
    anchor: LayoutCoordinates,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val view = LocalView.current

    // Posición del anchor (Card) en la ventana
    val anchorPos = anchor.positionInWindow()
    val anchorSize = anchor.size

    // Medidas del bocadillo
    val bubbleWidth = with(density) { 260.dp.roundToPx() }
    val bubbleHeight = with(density) { 60.dp.roundToPx() }
    val gap = with(density) { 8.dp.roundToPx() }       // separación
    val arrowH = with(density) { 10.dp.roundToPx() }   // alto del pico

    // Centro X de la Card
    val anchorCenterX = (anchorPos.x + anchorSize.width / 2f).toInt()

    // Por defecto lo ponemos encima; si no cabe, debajo
    val placeAbove = anchorPos.y.toInt() - (bubbleHeight + arrowH + gap) > 0

    val x = (anchorCenterX - bubbleWidth / 2)
        .coerceIn(0, view.width - bubbleWidth)

    val y = if (placeAbove) {
        anchorPos.y.toInt() - (bubbleHeight + arrowH + gap)
    } else {
        anchorPos.y.toInt() + anchorSize.height + gap
    }

    // ---- Animación: mantenemos el popup montado hasta terminar la salida ----
    var visible by remember { mutableStateOf(false) }
    var closing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    fun requestClose() {
        if (closing) return
        closing = true
        visible = false
    }

    // Cuando termina la animación de salida, llamamos al onDismiss real (desmonta)
    LaunchedEffect(visible, closing) {
        if (!visible && closing) {
            // duración acorde a tus anims (ajústala si cambias specs)
            delay(180)
            onDismiss()
        }
    }

    Popup(
        onDismissRequest = { requestClose() },
        properties = PopupProperties(focusable = true),
        popupPositionProvider = remember(x, y) {
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset = IntOffset(x, y)
            }
        }
    ) {

        // Origen del scale: en el lado del pico
        val origin = if (placeAbove) TransformOrigin(0.5f, 1f) else TransformOrigin(0.5f, 0f)

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn(
                initialScale = 0.0f,
                transformOrigin = origin
            ) + slideInVertically { full ->
                // un pelín desde el lado del pico
                if (placeAbove) full / 10 else -full / 10
            },
            exit = fadeOut() + scaleOut(
                targetScale = 0.0f,
                transformOrigin = origin
            ) + slideOutVertically { full ->
                if (placeAbove) full / 10 else -full / 10
            }
        ) {
            ComicBubble(
                widthPx = bubbleWidth,
                heightPx = bubbleHeight,
                arrowToXInWindowPx = anchorCenterX,
                bubbleLeftInWindowPx = x,
                arrowOnTop = !placeAbove, // si va debajo, el pico arriba; si va arriba, el pico abajo,
                borderColor = MaterialTheme.colorScheme.primary,
                fillColor = CardGlowOuter,
                borderWidth = 2.dp,
                onDismiss = onDismiss,
                content = content
            )
        }
    }
}
