package com.davidcrespo.onewallet.core.composables.charts.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ChartSequentialAnimation(
    slices: ImmutableList<AssetSlice>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 28.dp,
    durationMs: Int = 2000,
    minSlicePercent: Float = 0.01f,
    shouldAnimate: Boolean = true
) {
    val sweepProgress = remember { Animatable(0f) }

    LaunchedEffect(slices, shouldAnimate) {
        if (shouldAnimate) {
            sweepProgress.snapTo(0f)
            sweepProgress.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = durationMs,
                    easing = FastOutSlowInEasing
                )
            )
        } else {
            sweepProgress.snapTo(0f)
        }
    }

    val sweeps = remember(slices) {
        computeMinSweeps(
            values = slices.map { it.value.toFloat() },
            minFraction = minSlicePercent
        )
    }

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val strokePx = strokeWidth.toPx()
        val capRadius = strokePx / 2f
        val epsilon = 0.01f

        val arcRect = Rect(
            left = capRadius,
            top = capRadius,
            right = size.width - capRadius,
            bottom = size.height - capRadius
        )

        // Si estás usando círculos para el cap final, aquí normalmente conviene Butt:
        // Round redondea ambos extremos del arco.
        val stroke = Stroke(width = strokePx, cap = StrokeCap.Round)

        fun pointOnArc(angleDeg: Float): Offset {
            val rad = Math.toRadians(angleDeg.toDouble())
            val cx = arcRect.center.x
            val cy = arcRect.center.y
            val rx = arcRect.width / 2f
            val ry = arcRect.height / 2f
            return Offset(
                x = cx + rx * kotlin.math.cos(rad).toFloat(),
                y = cy + ry * kotlin.math.sin(rad).toFloat()
            )
        }

        // ---- Pasada 1: Arcos ----
        var startAngle = -90f
        var remaining = sweepProgress.value

        for (i in slices.indices) {
            val fullSweep = sweeps[i]
            val drawSweep = remaining.coerceIn(0f, fullSweep)

            if (drawSweep > epsilon) {
                drawArc(
                    color = slices[i].color,
                    startAngle = startAngle,
                    sweepAngle = drawSweep,
                    useCenter = false,
                    topLeft = arcRect.topLeft,
                    size = arcRect.size,
                    style = stroke
                )
            }

            startAngle += fullSweep
            remaining -= fullSweep
            if (remaining <= 0f) break
        }

        // ---- Pasada 2: Caps (encima) ----
        //Pintando los ciruclos de utlimo a primero para que no se superpongan
        // Precalcula el startAngle de cada slice (posición real)
        val startAngles = FloatArray(slices.size)
        run {
            var a = -90f
            for (i in slices.indices) {
                startAngles[i] = a
                a += sweeps[i]
            }
        }

        // Pintar caps del último al primero (encima y en orden inverso)
        for (i in slices.lastIndex downTo 0) {
            val fullSweep = sweeps[i]

            // remaining hay que recomputarlo para este índice, porque el "break" ya no aplica igual al ir al revés
            val alreadyDrawnBeforeThisSlice = sweeps.take(i).sum()
            val remainingForThisSlice = (sweepProgress.value - alreadyDrawnBeforeThisSlice).coerceAtLeast(0f)
            val drawSweep = remainingForThisSlice.coerceIn(0f, fullSweep)

            if (drawSweep > epsilon) {
                val end = pointOnArc(startAngles[i] + drawSweep)
                drawCircle(
                    color = slices[i].color,
                    radius = capRadius,
                    center = end
                )
            }
        }
    }
}

@Composable
fun ChartRevealAllAnimation(
    slices: ImmutableList<AssetSlice>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 28.dp,
    animationDurationMs: Int = 900,
    minSlicePercent: Float = 0.01f,
    shouldAnimate: Boolean = true
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(slices, shouldAnimate) {
        if (shouldAnimate) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = animationDurationMs,
                    easing = FastOutSlowInEasing
                )
            )
        } else {
            progress.snapTo(0f)
        }
    }

    // Sweeps “visuales” con mínimo (suman 360)
    val sweeps = remember(slices, minSlicePercent) {
        computeMinSweeps(
            values = slices.map { it.value.toFloat() },
            minFraction = minSlicePercent
        )
    }

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val p = progress.value
        val strokePx = strokeWidth.toPx()
        val capRadius = strokePx / 2f
        val epsilon = 0.01f

        val arcRect = Rect(
            left = capRadius,
            top = capRadius,
            right = size.width - capRadius,
            bottom = size.height - capRadius
        )

        // Si usas drawCircle para el cap, lo normal es Butt para que el inicio sea plano
        val stroke = Stroke(width = strokePx, cap = StrokeCap.Butt)

        fun pointOnArc(angleDeg: Float): Offset {
            val rad = Math.toRadians(angleDeg.toDouble())
            val cx = arcRect.center.x
            val cy = arcRect.center.y
            val rx = arcRect.width / 2f
            val ry = arcRect.height / 2f
            return Offset(
                x = cx + rx * kotlin.math.cos(rad).toFloat(),
                y = cy + ry * kotlin.math.sin(rad).toFloat()
            )
        }

        // Precalcula startAngle por slice para poder pintar caps al revés
        val startAngles = FloatArray(slices.size)
        run {
            var a = -90f
            for (i in slices.indices) {
                startAngles[i] = a
                a += sweeps[i]
            }
        }

        // 1) Arcos (debajo) - orden normal
        var startAngle = -90f
        for (i in slices.indices) {
            val fullSweep = sweeps[i]
            val animatedSweep = fullSweep * p

            if (animatedSweep > epsilon) {
                drawArc(
                    color = slices[i].color,
                    startAngle = startAngle,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = arcRect.topLeft,
                    size = arcRect.size,
                    style = stroke
                )
            }

            startAngle += fullSweep
        }

        // 2) Caps (encima) - del último al primero
        for (i in slices.lastIndex downTo 0) {
            val fullSweep = sweeps[i]
            val animatedSweep = fullSweep * p

            if (animatedSweep > epsilon) {
                val end = pointOnArc(startAngles[i] + animatedSweep)
                drawCircle(
                    color = slices[i].color,
                    radius = capRadius,
                    center = end
                )
            }
        }
    }
}

private fun computeMinSweeps(
    values: List<Float>,
    minFraction: Float = 0.01f
): List<Float> {
    val total = values.sum().coerceAtLeast(0.0001f)
    val actual = values.map { (it / total) * 360f }

    val nonZeroCount = values.count { it > 0f }
    if (nonZeroCount == 0) return values.map { 0f }

    val requestedMin = 360f * minFraction
    // Si hay demasiados slices, no caben todos con 6%: reparte el máximo posible
    val minSweep = if (nonZeroCount * requestedMin > 360f) 360f / nonZeroCount else requestedMin

    val clamped = actual
        .mapIndexed { i, a -> if (values[i] > 0f) maxOf(a, minSweep) else 0f }
        .toMutableList()

    val sumClamped = clamped.sum()
    if (kotlin.math.abs(sumClamped - 360f) < 0.001f) return clamped

    if (sumClamped > 360f) {
        val excess = sumClamped - 360f
        val capacities = clamped.mapIndexed { i, a -> if (values[i] > 0f) maxOf(0f, a - minSweep) else 0f }
        val capSum = capacities.sum().coerceAtLeast(0.0001f)
        val ratio = (excess / capSum).coerceIn(0f, 1f)

        for (i in clamped.indices) {
            if (capacities[i] > 0f) clamped[i] -= capacities[i] * ratio
        }
    } else {
        val remaining = 360f - sumClamped
        // reparte proporcional al ángulo real
        val weightSum = actual.sum().coerceAtLeast(0.0001f)
        for (i in clamped.indices) {
            clamped[i] += remaining * (actual[i] / weightSum)
        }
    }

    return clamped
}
