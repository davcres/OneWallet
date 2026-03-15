package com.davidcrespo.onewallet.presentation.history.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryList(
    items: ImmutableList<ImmutableList<InvestmentView>>,
    currency: CurrencyView,
    onClick: (ImmutableList<InvestmentView>) -> Unit,
    isBalanceVisible: Boolean,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    OWAnimatedList(
        items = items,
        key = { "${it.first().month}-${it.first().year}" },
        state = state,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 32.dp),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        itemContent = { modifier, historyItem, index ->
            HistoryMonthCard(
                item = historyItem,
                previousItem = items.getOrNull(index + 1),
                currency = currency,
                onClick = { onClick(historyItem) },
                isBalanceVisible = isBalanceVisible,
                modifier = modifier
            )
        }
    )
}
