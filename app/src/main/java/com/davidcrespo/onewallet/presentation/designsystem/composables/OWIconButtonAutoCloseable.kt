package com.davidcrespo.onewallet.presentation.designsystem.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.modifiers.animations.bounceClick

@Composable
fun OWIconButtonAutoCloseable(
    imageVector: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    durationMillis: Int = 5000,
    delayMillis: Int = 500
) {

    val borderColor = MaterialTheme.colorScheme.primary
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = LinearEasing)
        )
        onClick()
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
            .bounceClick()
            .drawBehind {
                val stroke = 4.dp.toPx()
                drawArc(
                    color = borderColor,
                    startAngle = -90f,
                    sweepAngle = 360 * progress.value,
                    useCenter = false,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onTertiary)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}