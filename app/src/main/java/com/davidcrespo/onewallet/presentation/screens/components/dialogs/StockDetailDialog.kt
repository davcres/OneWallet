package com.davidcrespo.onewallet.presentation.screens.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockDetailDialog(
    item: PortfolioItem,
    onDismiss: () -> Unit,
    onConfirmQuantity: (Double) -> Unit,
    onConfirmDca: (Double, String, Long?, Double) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Posición", "DCA")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = item.stockInfo.displaySymbol) },
        text = {
            Column {
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(text = title) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> QuantityTab(item, onConfirmQuantity)
                    1 -> DcaTab(item, onConfirmDca)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun QuantityTab(
    item: PortfolioItem,
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
        Text(text = "Cantidad total de acciones:")
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val quantity = text.toDoubleOrNull() ?: 0.0
                onConfirm(quantity)
            },
            enabled = text.toDoubleOrNull() != null && text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar Cantidad")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DcaTab(
    item: PortfolioItem,
    onConfirm: (Double, String, Long?, Double) -> Unit
) {
    var amountText by remember { mutableStateOf(if (item.dcaAmount == 0.0) "" else item.dcaAmount.toString()) }
    var initialInvestmentText by remember { mutableStateOf(if (item.dcaInitialInvestment == 0.0) "" else item.dcaInitialInvestment.toString()) }
    var frequency by remember { mutableStateOf(item.dcaFrequency) }
    var startDateMillis by remember { mutableStateOf(item.dcaStartDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    var expanded by remember { mutableStateOf(false) }
    val frequencies = listOf("Diario", "Semanal", "Mensual")

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val startDateText by remember(startDateMillis) {
        derivedStateOf {
            startDateMillis?.let { dateFormatter.format(Date(it)) } ?: ""
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDateMillis ?: System.currentTimeMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableYear(year: Int): Boolean = year >= 2024
                
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= (System.currentTimeMillis() - 86400000)
                }
            }
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column {
        Text(text = "Inversión inicial ($):")
        OutlinedTextField(
            value = initialInvestmentText,
            onValueChange = { initialInvestmentText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Inversión periódica ($):")
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Fecha de inicio:")
        OutlinedTextField(
            value = startDateText,
            onValueChange = { },
            readOnly = true,
            trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable { showDatePicker = true },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                showDatePicker = true
                            }
                        }
                    }
                }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Frecuencia:")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = frequency,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                frequencies.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            frequency = selection
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                val initial = initialInvestmentText.toDoubleOrNull() ?: 0.0
                onConfirm(amount, frequency, startDateMillis, initial)
            },
            enabled = amountText.toDoubleOrNull() != null && amountText.isNotBlank(), // Assuming DCA Amount is mandatory, others optional/default
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Configuración DCA")
        }
    }
}