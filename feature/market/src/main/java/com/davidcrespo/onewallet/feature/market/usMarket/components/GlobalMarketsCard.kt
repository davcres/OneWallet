package com.davidcrespo.onewallet.feature.market.usMarket.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.core.designsystem.composables.OWIconButtonAutoCloseable
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun GlobalMarketsCard(
    visible: Boolean,
    onOpenGlobalMarkets: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val dismissThresholdPx = with(density) { 140.dp.toPx() }
    val dismissToPx = with(density) { 500.dp.toPx() } // mas que la altura de la card para que se deslice del todo al irse

    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Evita que la card reaparezca al deslizarla
    LaunchedEffect(visible) {
        if (visible) offsetY.snapTo(0f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    val newValue = (offsetY.value + delta).coerceAtLeast(0f)
                    scope.launch { offsetY.snapTo(newValue) }
                },
                onDragStopped = { velocity ->
                    scope.launch {
                        val shouldDismiss =
                            offsetY.value > dismissThresholdPx || velocity > 1800f

                        if (shouldDismiss) {
                            offsetY.animateTo(dismissToPx)
                            onClose()
                        } else {
                            offsetY.animateTo(0f)
                        }
                    }
                }
            ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Box(Modifier.fillMaxWidth()) {
            OWIconButtonAutoCloseable(
                imageVector = Icons.Outlined.Close,
                onClick = onClose,
                contentDescription = stringResource(R.string.close_cd),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onTertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(45.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.global_markets_card_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.global_markets_card_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    text = stringResource(R.string.global_markets_card_button),
                    contentDescription = stringResource(R.string.global_markets_card_button_cd),
                    style = ButtonStyle.PRIMARY,
                    onClick = onOpenGlobalMarkets,
                )
            }
        }
    }
}

@Preview
@Composable
private fun GlobalMarketsCardPreview() {
    OneWalletTheme {
        GlobalMarketsCard(
            visible = true,
            onOpenGlobalMarkets = {},
            onClose = {}
        )
    }
}