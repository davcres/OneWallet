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

@Composable
fun MarketSearchBar(
    isCrypto: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = { input ->
            val query = input.filter { it.isLetterOrDigit() }
            onQueryChange(query)
        },
        icon = Icons.Outlined.Search,
        placeholder = if (isCrypto) stringResource(R.string.search_crypto_placeholder) else stringResource(R.string.search_stock_placeholder),
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
    MarketSearchBar(
        isCrypto = false,
        query = "",
        onQueryChange = {},
        onSearch = {}
    )
}
