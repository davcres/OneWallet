package com.davidcrespo.onewallet.presentation.historical.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.domain.model.investment.Investment

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
            HistoricalMonthCard(
                item = historicalItem,
                previousItem = items.getOrNull(index + 1),
                onClick = { onClick(historicalItem) },
                modifier = modifier
            )
        }
    )
}
