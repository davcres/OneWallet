package com.davidcrespo.onewallet.presentation.portfolio.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.modifiers.animations.shakeClickEffect
import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.presentation.models.InvestmentView

@Composable
fun StockDetailDialog(
    item: InvestmentView,
    onDismiss: () -> Unit,
    onConfirmQuantity: (Double) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = item.symbol) },
        text = {
            QuantityTab(item, onConfirmQuantity)
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close_cd))
            }
        }
    )
}

@Composable
fun QuantityTab(
    item: InvestmentView,
    onConfirm: (Double) -> Unit
) {
    var text by remember { mutableStateOf(item.quantity.toString()) }
    val focusRequester = remember { FocusRequester() }
    var hasClearedZero by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        if (item.quantity == 0.0) {
            text = ""
            hasClearedZero = true
        }
    }

    Column {
        Text(text = stringResource(R.string.total_shares_label))
        OutlinedTextField(
            value = text,
            onValueChange = { newValue ->
                val normalized = newValue.replace('.', ',')
                if (normalized.all { it.isDigit() || it == ',' } && normalized.count { it == ',' } <= 1) {
                    text = normalized
                    if (item.quantity == 0.0 && normalized.isEmpty()) {
                        hasClearedZero = true
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && item.quantity == 0.0 && !hasClearedZero) {
                        text = ""
                        hasClearedZero = true
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))

        val quantity = text.normalizeDouble()
        val isValid = quantity != null
        Button(
            onClick = {
                if (isValid) {
                    onConfirm(quantity)
                }
            },
            modifier = Modifier.fillMaxWidth().then(
                if (isValid) {
                    Modifier
                } else {
                    Modifier.shakeClickEffect()
                }
            )
        ) {
            Text(stringResource(R.string.update_quantity_action))
        }
    }
}
