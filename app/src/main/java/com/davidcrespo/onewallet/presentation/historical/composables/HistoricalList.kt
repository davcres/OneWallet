package com.davidcrespo.onewallet.presentation.historical.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.OWAnimatedList
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.presentation.designsystem.composables.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalList(
    items: List<List<Investment>>,
    onClick: (List<Investment>) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    OWAnimatedList(
        items = items,
        key = { "${it.first().month}-${it.first().year}" },
        state = state,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        itemContent = { modifier, historicalItem, index ->
            HistoricalItemCard(
                item = historicalItem,
                previousItem = items.getOrNull(index + 1),
                modifier = modifier
                    .bounceClick()
                    .clickable { onClick(historicalItem) }
            )
        }
    )
}
