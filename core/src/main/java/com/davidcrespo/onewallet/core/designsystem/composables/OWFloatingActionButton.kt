package com.davidcrespo.onewallet.core.designsystem.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.composables.modifiers.animations.bounceClick
import kotlin.math.max

@Composable
fun OWFloatingActionButton(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isPressedForced: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressedActual by interactionSource.collectIsPressedAsState()
    val pressed = isPressedActual || isPressedForced

    val fabContentDescription = stringResource(R.string.accessibility_add_investment_fab)

    val fabStateDescription = if (expanded) {
        stringResource(R.string.accessibility_add_investment_expanded)
    } else {
        stringResource(R.string.accessibility_add_investment_collapsed)
    }

    val fabClickLabel = if (expanded) {
        stringResource(R.string.accessibility_dismiss_add_investment)
    } else {
        stringResource(R.string.accessibility_show_add_investment)
    }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 135f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fab_rotation"
    )

    val pressedRotation by animateFloatAsState(
        targetValue = if (pressed) 315f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        ),
        label = "fab_rotation"
    )

    FloatingActionButton(
        onClick = { onExpandedChange(!expanded) },
        interactionSource = interactionSource,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .bounceClick(pressed)
            .semantics(mergeDescendants = true) {
                contentDescription = fabContentDescription
                stateDescription = fabStateDescription
                onClick(
                    label = fabClickLabel,
                    action = null
                )
            }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .graphicsLayer {
                    rotationZ = max(rotation, pressedRotation)
                }
        )
    }
}
