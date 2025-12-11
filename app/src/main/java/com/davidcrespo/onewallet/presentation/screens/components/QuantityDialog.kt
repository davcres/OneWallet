package com.davidcrespo.onewallet.presentation.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.PortfolioItem

@Composable
fun QuantityDialog(
    item: PortfolioItem,
    onDismiss: () -> Unit,
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Cantidad") },
        text = {
            Column {
                Text(text = "Introduce la cantidad para ${item.stockInfo.displaySymbol}:")
                OutlinedTextField(
                    value = text,
                    onValueChange = { newValue ->
                        text = newValue
                        if (item.quantity == 0.0 && newValue.isEmpty()) {
                            hasClearedZero = true
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantity = text.toDoubleOrNull() ?: 0.0
                    onConfirm(quantity)
                },
                enabled = text.toDoubleOrNull() != null && text.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
