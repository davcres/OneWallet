package com.davidcrespo.onewallet.presentation.history.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ComicBubble(
    widthPx: Int,
    heightPx: Int,
    arrowToXInWindowPx: Int,
    bubbleLeftInWindowPx: Int,
    arrowOnTop: Boolean,
    borderColor: Color = Color.Black,
    fillColor: Color = Color.White,
    borderWidth: Dp = 2.dp,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val arrowWidth: Dp = 18.dp
    val arrowHeight: Dp = 10.dp
    val cornerRadius: Dp = 32.dp

    val arrowX = (arrowToXInWindowPx - bubbleLeftInWindowPx).toFloat()

    Box(
        modifier = Modifier
            .size(
                with(density) { widthPx.toDp() },
                with(density) { (heightPx + arrowHeight.roundToPx()).toDp() }
            )
            .pointerInput(Unit) {
                // tocar fuera del contenido del popup se gestiona por focusable,
                // pero esto sirve si quieres cerrar tocando el propio bubble también
            }
    ) {
        Canvas(Modifier.matchParentSize()) {
            val bw = borderWidth.toPx()
            val r = cornerRadius.toPx()
            val ah = arrowHeight.toPx()
            val aw = arrowWidth.toPx() / 2f  // half width

            // rect del “cuerpo” del bocadillo
            val bodyTop = if (arrowOnTop) ah else 0f
            val bodyBottom = if (arrowOnTop) size.height else size.height - ah

            val clampedArrowX = arrowX.coerceIn(r + aw + bw, size.width - r - aw - bw)

            val path = Path().apply {
                // Recorremos el contorno en sentido horario, insertando el pico en el lado correspondiente
                val left = bw / 2f
                val right = size.width - bw / 2f
                val top = bodyTop + bw / 2f
                val bottom = bodyBottom - bw / 2f
                val rad = (r - bw / 2f).coerceAtLeast(0f)

                if (arrowOnTop) {
                    // Arrancamos arriba-izquierda (después de la esquina redondeada)
                    moveTo(left + rad, top)

                    // tramo superior hasta antes del pico
                    lineTo(clampedArrowX - aw, top)
                    // pico
                    lineTo(clampedArrowX, top - ah)
                    lineTo(clampedArrowX + aw, top)

                    // tramo superior restante + esquina sup derecha
                    lineTo(right - rad, top)
                    arcTo(
                        rect = Rect(right - 2 * rad, top, right, top + 2 * rad),
                        startAngleDegrees = -90f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    // derecha
                    lineTo(right, bottom - rad)
                    // esquina inf derecha
                    arcTo(
                        rect = Rect(right - 2 * rad, bottom - 2 * rad, right, bottom),
                        startAngleDegrees = 0f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    // abajo
                    lineTo(left + rad, bottom)
                    // esquina inf izquierda
                    arcTo(
                        rect = Rect(left, bottom - 2 * rad, left + 2 * rad, bottom),
                        startAngleDegrees = 90f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    // izquierda
                    lineTo(left, top + rad)
                    // esquina sup izquierda
                    arcTo(
                        rect = Rect(left, top, left + 2 * rad, top + 2 * rad),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    close()
                } else {
                    // Pico abajo
                    moveTo(left + rad, top)

                    // superior + esquina sup derecha
                    lineTo(right - rad, top)
                    arcTo(
                        rect = Rect(right - 2 * rad, top, right, top + 2 * rad),
                        startAngleDegrees = -90f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    // derecha
                    lineTo(right, bottom - rad)
                    // esquina inf derecha
                    arcTo(
                        rect = Rect(right - 2 * rad, bottom - 2 * rad, right, bottom),
                        startAngleDegrees = 0f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )

                    // tramo inferior hasta antes del pico
                    lineTo(clampedArrowX + aw, bottom)
                    // pico
                    lineTo(clampedArrowX, bottom + ah)
                    lineTo(clampedArrowX - aw, bottom)

                    // tramo inferior restante + esquina inf izquierda
                    lineTo(left + rad, bottom)
                    arcTo(
                        rect = Rect(left, bottom - 2 * rad, left + 2 * rad, bottom),
                        startAngleDegrees = 90f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    // izquierda + esquina sup izquierda
                    lineTo(left, top + rad)
                    arcTo(
                        rect = Rect(left, top, left + 2 * rad, top + 2 * rad),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    close()
                }
            }

            // Relleno
            drawPath(path = path, color = fillColor)
            // Borde (trazo) — sin “costura” en el pico
            drawPath(
                path = path,
                color = borderColor,
                style = Stroke(width = bw, join = StrokeJoin.Round, cap = StrokeCap.Round)
            )
        }

        // Contenido
        Box(
            modifier = Modifier.padding(
                top = if (arrowOnTop) arrowHeight else 0.dp,
                bottom = if (arrowOnTop) 0.dp else arrowHeight,
                start = 12.dp,
                end = 12.dp
            )
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.Black) {
                content()
            }
        }

        Box(
            modifier = Modifier.padding(
                top = if (arrowOnTop) 10.dp else 0.dp,
                bottom = if (arrowOnTop) 0.dp else 10.dp,
                start = 12.dp,
                end = 12.dp
            )
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.Black) {
                content()
            }
        }
    }
}
