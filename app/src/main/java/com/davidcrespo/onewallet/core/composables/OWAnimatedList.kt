package com.davidcrespo.onewallet.core.composables

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AnimatedList(
    items: List<T>,
    key: (T) -> Any,
    state: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    modifier: Modifier = Modifier,
    itemContent: @Composable (modifier: Modifier, item: T) -> Unit
) {
    var initialVisibleIndex by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val hasInitialVisibleIndex by remember {
        derivedStateOf { initialVisibleIndex.isNotEmpty() }
    }

    // Captura UNA sola vez (primer layout válido)
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.visibleItemsInfo.map { it.index } } // index de cada item visible
            .collect { firstVisibleItems ->
                if (!hasInitialVisibleIndex) {
                    initialVisibleIndex = firstVisibleItems
                }
            }
    }

    // Importante: la lista se compone y mide, pero no se dibuja hasta ready=true
    LazyColumn(
        state = state,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier.graphicsLayer { alpha = if (hasInitialVisibleIndex) 1f else 0f }
    ) {
        itemsIndexed(items, key = { _, it -> key(it) }) { index, item ->

            val rank =
                initialVisibleIndex.indexOf(index) // devuelve -1 si el item no está visible al entrar
            val shouldAnimate = hasInitialVisibleIndex && rank >= 0
            AnimatableRow(
                item = item,
                key = key,
                shouldAnimate = shouldAnimate,
                staggerMs = if (shouldAnimate) (rank * 100L) else 0L,
                itemContent = itemContent
            )
        }
    }
}

@Composable
private fun <T> AnimatableRow(
    item: T,
    key: (T) -> Any,
    shouldAnimate: Boolean,
    staggerMs: Long,
    itemContent: @Composable (modifier: Modifier, item: T) -> Unit
) {
    var played by rememberSaveable(key(item)) { mutableStateOf(false) }

    if (!shouldAnimate || played) {
        itemContent(Modifier, item)
        return
    }

    val animationState =
        remember { MutableTransitionState(false) } // para controlar estado animacion

    LaunchedEffect(key(item)) {
        delay(staggerMs)
        animationState.targetState = true // start animation
    }

    LaunchedEffect(animationState) {
        snapshotFlow { animationState.isIdle && animationState.currentState }
            .collect { isFinished ->
                if (isFinished) { // end animation
                    played = true
                }
            }
    }

    val transition = rememberTransition(animationState, label = "entry")

    val alpha by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        },
        label = "alpha"
    ) { visible ->
        if (visible) 1f else 0f
    }

    val slideFactor by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        },
        label = "slide"
    ) { visible ->
        if (visible) 0f else -1f
    }

    itemContent(
        Modifier.graphicsLayer {
            this.alpha = alpha
            this.translationX = size.width * slideFactor
        },
        item
    )
}
