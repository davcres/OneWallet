package com.davidcrespo.onewallet.feature.portfolio.components.bottomSheet.updateInvestment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.designsystem.composables.OWDropdownSelector
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.owDropdownSelector.DropdownItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun FormCategorySection(
    categories: ImmutableList<DropdownItem>,
    selectedCategory: DropdownItem?,
    onCategorySelected: (DropdownItem) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.category_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OWDropdownSelector(
            items = categories,
            selectedItem = selectedCategory,
            onItemClicked = onCategorySelected,
            onClick = onClick
        )
    }
}
