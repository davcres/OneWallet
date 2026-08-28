package com.davidcrespo.onewallet.core.composables.modifiers.animations

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

fun Modifier.animatedBorder(
    progress: Float,
    color: Color,
    strokeWidth: Dp,
    cornerRadius: Dp
) = this.drawBehind {
    val strokePx = strokeWidth.toPx()
    val radiusPx = cornerRadius.toPx()
    val halfStroke = strokePx / 2f

    val rect = Rect(
        left = halfStroke,
        top = halfStroke,
        right = size.width - halfStroke,
        bottom = size.height - halfStroke
    )

    val fullPath = android.graphics.Path().apply {
        addRoundRect(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            radiusPx,
            radiusPx,
            android.graphics.Path.Direction.CW
        )
    }

    val pathMeasure = PathMeasure()
    pathMeasure.setPath(fullPath.asComposePath(), false)

    val animatedPath = Path()
    pathMeasure.getSegment(
        0f,
        pathMeasure.length * progress,
        animatedPath
    )

    drawPath(
        path = animatedPath,
        color = color,
        style = Stroke(width = strokePx)
    )
}
