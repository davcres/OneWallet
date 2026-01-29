package com.davidcrespo.onewallet.presentation.portfolio.allocation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.allocation.composables.Graphic
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun AllocationTab(
    portfolioItems: ImmutableList<InvestmentView>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Graphic(
            portfolioItems = portfolioItems.map {
                AssetSlice(it.quantity * it.displayPrice, it.type.color)
            }.toPersistentList()
        )

        Text("HOLA")
    }
}

/*val assets = persistentListOf(
    AssetSlice(82f, Color(0xFFFFB300)),  // Cash
    AssetSlice(6f, Color(0xFF3DDC97)), // Stocks
    AssetSlice(6f, Color(0xFF7C4DFF)), // Crypto
    AssetSlice(6f, Color(0xFF4285F4)), // Funds
)*/




