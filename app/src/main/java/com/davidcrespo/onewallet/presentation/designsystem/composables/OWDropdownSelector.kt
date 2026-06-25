package com.davidcrespo.onewallet.presentation.designsystem.composables

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.owDropdownSelector.DropdownItem
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.owDropdownSelector.OWCustomCategoryDialog
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.owDropdownSelector.OWDropdownSelectorHeader
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.owDropdownSelector.OWDropdownSelectorPopup
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun OWDropdownSelector(
    items: ImmutableList<DropdownItem>,
    selectedItem: DropdownItem?,
    onItemClicked: (DropdownItem) -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var itemHeightPx by remember { mutableIntStateOf(0) }
    var headerSize by remember { mutableStateOf(IntSize.Zero) }

    // Custom Category Dialog State
    var showCustomDialog by remember { mutableStateOf(false) }
    var customCategoryName by remember { mutableStateOf("") }

    // Use TransitionState to handle the animation within the Popup
    val animationState = remember { 
        MutableTransitionState(false).apply { targetState = false }
    }

    // Synchronize animationState with expanded
    LaunchedEffect(expanded) {
        animationState.targetState = expanded
    }

    val density = LocalDensity.current

    val maxListHeight = remember(itemHeightPx, density) {
        if (itemHeightPx > 0) {
            with(density) { (itemHeightPx * 4.5f).toDp() }
        } else {
            250.dp
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { headerSize = it }
    ) {
        OWDropdownSelectorHeader(
            selectedItemName = selectedItem?.name,
            isExpanded = expanded,
            onClick = {
                onClick?.invoke()
                expanded = !expanded
            }
        )

        OWDropdownSelectorPopup(
            items = items,
            selectedItem = selectedItem,
            onDismissRequest = { expanded = false },
            onItemClicked = { item ->
                onItemClicked(item)
                expanded = false
            },
            onCustomItemClicked = {
                expanded = false
                showCustomDialog = true
            },
            headerSize = headerSize,
            animationState = animationState,
            onItemSizeChanged = { size -> itemHeightPx = size.height },
            maxListHeight = maxListHeight
        )
    }

    if (showCustomDialog) {
        OWCustomCategoryDialog(
            customCategoryName = customCategoryName,
            onCustomCategoryNameChange = { customCategoryName = it },
            onDismiss = {
                showCustomDialog = false
                customCategoryName = ""
            },
            onConfirm = {
                if (customCategoryName.isNotBlank()) {
                    onItemClicked(DropdownItem(id = items.size + 1, name = customCategoryName, tag = customCategoryName))
                    showCustomDialog = false
                    customCategoryName = ""
                }
            }
        )
    }
}

@Preview
@Composable
private fun OWDropdownSelectorPreview() {
    OneWalletTheme {
        OWDropdownSelector(
            items = persistentListOf(
                DropdownItem(1, "Item 1"),
                DropdownItem(2, "Item 2"),
                DropdownItem(3, "Item 3"),
                DropdownItem(4, "Item 4"),
            ),
            selectedItem = DropdownItem(3, "Item 3"),
            onItemClicked = {}
        )
    }
}
