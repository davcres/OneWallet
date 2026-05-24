package com.davidcrespo.onewallet.presentation.market.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.TextField
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme

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
            isCrypto -> stringResource(R.string.search_crypto_placeholder)
            marketType == MarketType.US -> stringResource(R.string.search_us_stock_placeholder)
            else -> stringResource(R.string.search_global_stock_placeholder)
        },
        contentDescription = stringResource(R.string.search_market_cd),
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
