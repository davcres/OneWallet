package com.davidcrespo.onewallet.feature.portfolio.positions.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.extensions.applyIf
import com.davidcrespo.onewallet.core.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.core.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.core.models.InvestmentView
import com.davidcrespo.onewallet.feature.portfolio.models.PortfolioCoachmarks
import com.pseudoankit.coachmark.LocalCoachMarkScope
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.pseudoankit.coachmark.shape.Arrow
import com.pseudoankit.coachmark.shape.Balloon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioList(
    header: @Composable () -> Unit,
    items: ImmutableList<InvestmentView>,
    currency: CurrencyView,
    onRemove: (InvestmentView) -> Unit,
    onEdit: (InvestmentView) -> Unit,
    isBalanceVisible: Boolean,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    shouldAnimate: Boolean = true,
    isEditOnboardingActive: Boolean = false,
    isDeleteOnboardingActive: Boolean = false,
) {
    var headerHeight by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize()) {
        if (shouldAnimate) {
            OWAnimatedList(
                header = {
                    Box(modifier = Modifier.onSizeChanged { headerHeight = it.height }) {
                        header()
                    }
                },
                items = items,
                key = { it.symbol },
                state = state,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 32.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                itemContent = { itemModifier, portfolioItem, index ->
                    val density = LocalDensity.current
                    val dismissState = remember(portfolioItem.symbol) {
                        SwipeToDismissBoxState(
                            initialValue = SwipeToDismissBoxValue.Settled,
                            density = density,
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    onRemove(portfolioItem)
                                    false
                                } else {
                                    false
                                }
                            },
                            positionalThreshold = { with(density) { 56.dp.toPx() } }
                        )
                    }

                    val autoSwipeOffset = remember { androidx.compose.animation.core.Animatable(0f) }
                    var isItemPressed by remember { mutableStateOf(false) }

                    val deleteLabel = stringResource(R.string.clear_cd)

                    if (index == 0) {
                        LaunchedEffect(isDeleteOnboardingActive) {
                            if (isDeleteOnboardingActive) {
                                while (true) {
                                    delay(1000)
                                    autoSwipeOffset.animateTo(
                                        targetValue = -with(density) { 70.dp.toPx() },
                                        animationSpec = tween(500)
                                    )
                                    delay(500)
                                    autoSwipeOffset.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(500)
                                    )
                                }
                            } else {
                                autoSwipeOffset.snapTo(0f)
                            }
                        }

                        LaunchedEffect(isEditOnboardingActive) {
                            if (isEditOnboardingActive) {
                                while (true) {
                                    delay(1000)
                                    isItemPressed = true
                                    delay(2000)
                                    isItemPressed = false
                                    delay(1000)
                                }
                            } else {
                                isItemPressed = false
                            }
                        }
                    }

                    Box(
                        modifier = itemModifier
                            .applyIf(index == 0) {
                                enableCoachMark(
                                    key = PortfolioCoachmarks.EDIT_INVESTMENT,
                                    toolTipPlacement = ToolTipPlacement.Top,
                                    tooltip = {
                                        Balloon(
                                            arrow = Arrow.Bottom(),
                                            modifier = Modifier.widthIn(max = 200.dp),
                                            bgColor = MaterialTheme.colorScheme.primaryContainer,
                                            cornerRadius = 16.dp,
                                            padding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 12.dp
                                            )
                                        ) {
                                            Text(
                                                text = stringResource(PortfolioCoachmarks.EDIT_INVESTMENT.tooltip),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    },
                                    coachMarkScope = LocalCoachMarkScope.current
                                ).enableCoachMark(
                                    key = PortfolioCoachmarks.DELETE_INVESTMENT,
                                    toolTipPlacement = ToolTipPlacement.Bottom,
                                    tooltip = {
                                        Balloon(
                                            arrow = Arrow.Top(),
                                            modifier = Modifier.widthIn(max = 200.dp),
                                            bgColor = MaterialTheme.colorScheme.primaryContainer,
                                            cornerRadius = 16.dp,
                                            padding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 12.dp
                                            )
                                        ) {
                                            Text(
                                                text = stringResource(PortfolioCoachmarks.DELETE_INVESTMENT.tooltip),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    },
                                    coachMarkScope = LocalCoachMarkScope.current
                                )
                            }
                    ) {
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart || (isDeleteOnboardingActive && index == 0)) {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Transparent, Color.Red.copy(alpha = 0.8f))
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Transparent, Color.Transparent)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            },
                            content = {
                                OWInvestmentItem(
                                    item = portfolioItem,
                                    currency = currency,
                                    section = SectionType.PORTFOLIO,
                                    onClick = { onEdit(portfolioItem) },
                                    isBalanceVisible = isBalanceVisible,
                                    isPressed = isItemPressed,
                                    modifier = Modifier
                                        .offset { IntOffset(autoSwipeOffset.value.roundToInt(), 0) }
                                        .semantics(mergeDescendants = true) {
                                            customActions = listOf(
                                                CustomAccessibilityAction(deleteLabel) {
                                                    onRemove(portfolioItem)
                                                    true
                                                }
                                            )
                                        }
                                )
                            }
                        )
                    }
                }
            )

            // Coaching area for the list (shading starts below header)
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = with(density) { headerHeight.toDp() })
                    .enableCoachMark(
                        key = PortfolioCoachmarks.PORTFOLIO_LIST,
                        toolTipPlacement = ToolTipPlacement.Top,
                        tooltip = {
                            Box(modifier = Modifier.layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                layout(placeable.width, 0) {
                                    placeable.place(0, -placeable.height)
                                }
                            }) {
                                Balloon(
                                    arrow = Arrow.Bottom(),
                                    modifier = Modifier.widthIn(max = 200.dp),
                                    bgColor = MaterialTheme.colorScheme.primaryContainer,
                                    cornerRadius = 16.dp,
                                    padding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    )
                                ) {
                                    Text(
                                        text = stringResource(PortfolioCoachmarks.PORTFOLIO_LIST.tooltip),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        },
                        coachMarkScope = LocalCoachMarkScope.current
                    )
            )
        } else {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
