package com.davidcrespo.onewallet.core.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.presentation.designsystem.theme.Error
import com.davidcrespo.onewallet.presentation.designsystem.theme.OnError
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ErrorBanner(
    message: String?,
    autoCloseable: Boolean,
    showCloseIcon: Boolean,
    duration: Long = 4000,
    onErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(message) {
        if (message != null) {
            offsetY = 0f
            if (autoCloseable || !showCloseIcon) {
                delay(duration)
                onErrorDismiss()
            }
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
            initialOffsetY = { -it }
        ),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .draggable(
                state = rememberDraggableState { delta ->
                    val newOffset = offsetY + delta
                    if (newOffset <= 0) {
                        offsetY = newOffset
                    }
                },
                orientation = Orientation.Vertical,
                onDragStopped = {
                    if (offsetY < -50) { // Threshold to dismiss
                        onErrorDismiss()
                    } else {
                        offsetY = 0f
                    }
                }
            )
    ) {
        Surface(
            color = Error,
            contentColor = OnError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {

                Text(
                    text = message.orEmpty(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )

                if (showCloseIcon) {
                    IconButton(
                        onClick = onErrorDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close_cd)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ErrorBannerPreview() {
    ErrorBanner(
        message = "Error de conexión. Intente nuevamente. Error de conexión. Intente nuevamente. Error de conexión. Intente nuevamente. Error de conexión. Intente nuevamente. Error de conexión. Intente nuevamente.",
        autoCloseable = false,
        showCloseIcon = true,
        onErrorDismiss = {}
    )
}