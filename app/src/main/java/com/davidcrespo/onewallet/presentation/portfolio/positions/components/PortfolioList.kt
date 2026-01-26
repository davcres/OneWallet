package com.davidcrespo.onewallet.presentation.portfolio.positions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.modifiers.animations.bounceClick
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioList(
    items: ImmutableList<InvestmentView>,
    currency: Currency,
    onRemove: (InvestmentView) -> Unit,
    onEdit: (InvestmentView) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    OWAnimatedList(
        items = items,
        key = { it.symbol },
        state = state,
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        itemContent = { modifier, portfolioItem, index ->
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

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
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
                            contentDescription = "Borrar",
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
                    )
                },
                modifier = modifier.bounceClick()
            )
        }
    )
}
