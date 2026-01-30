package com.davidcrespo.onewallet.presentation.portfolio.allocation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllocationList(
    items: ImmutableList<InvestmentView>,
    currency: Currency,
    onSelect: (InvestmentType) -> Unit,
    modifier: Modifier = Modifier,
    isBalanceVisible: Boolean = true,
) {
    OWAnimatedList(
        items = items,
        key = { it.symbol },
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        itemContent = { modifier, priceItem, index ->
            OWInvestmentItem(
                item = priceItem,
                currency = currency,
                section = SectionType.PRICES,
                onClick = { onSelect(it.type) },
                modifier = modifier,
                isBalanceVisible = isBalanceVisible
            )
        }
    )
}
