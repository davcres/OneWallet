package com.davidcrespo.onewallet.feature.market.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.input.ImeAction
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.search_crypto_placeholder
import com.davidcrespo.onewallet.core.generated.resources.search_global_stock_placeholder
import com.davidcrespo.onewallet.core.generated.resources.search_market_cd
import com.davidcrespo.onewallet.core.generated.resources.search_us_stock_placeholder
import com.davidcrespo.onewallet.core.composables.TextField
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme

@Composable
fun MarketSearchBar(
    isCrypto: Boolean,
    marketType: MarketType,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = when {
            isCrypto -> stringResource(Res.string.search_crypto_placeholder)
            marketType == MarketType.US -> stringResource(Res.string.search_us_stock_placeholder)
            else -> stringResource(Res.string.search_global_stock_placeholder)
        },
        contentDescription = stringResource(Res.string.search_market_cd),
        leadingIcon = Icons.Outlined.Search,
        hasClearIcon = true,
        cornerRadius = 999.dp,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch(query) }
        ),
        modifier = modifier
    )
}

@Preview
@Composable
private fun MarketSearchBarPreview() {
    OneWalletTheme {
        MarketSearchBar(
            isCrypto = false,
            marketType = MarketType.GLOBAL,
            query = "",
            onQueryChange = {},
            onSearch = {}
        )
    }
}
