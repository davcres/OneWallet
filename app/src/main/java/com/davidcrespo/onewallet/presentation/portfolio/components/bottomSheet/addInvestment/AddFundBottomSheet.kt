package com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.TextField
import com.davidcrespo.onewallet.core.composables.animations.shakeClickEffect
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.core.extensions.applyIf
import com.davidcrespo.onewallet.core.extensions.isValidIsin
import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.bottomSheet.SheetHandle
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onAddFund: (String, Double) -> Unit,
    onIsinError: (String?) -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { SheetHandle() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        scrimColor = Color.Transparent // Disable default scrim (darker content out of bottom sheet) to see light error
    ) {
        SheetContent(
            onClose = {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
            },
            onAddFund = { isin, quantity -> onAddFund(isin, quantity) },
            onIsinError = onIsinError
        )

        Spacer(Modifier.height(20.dp))
    }
}

// ---------- UI ----------
@Composable
private fun SheetContent(
    onClose: () -> Unit,
    onAddFund: (String, Double) -> Unit,
    onIsinError: (String?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                }
            }
    ) {
        Header(onClose = onClose)

        Spacer(Modifier.height(16.dp))

        Form(
            onClose = onClose,
            onAddFund = onAddFund,
            onIsinError = onIsinError
        )
    }
}

@Composable
private fun Header(onClose: () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Row {
                Icon(
                    imageVector = Icons.Default.Addchart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add New Fund",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        OWIconButton(
            imageVector = Icons.Outlined.Close,
            onClick = onClose,
            contentDescription = "Close",
            modifier = Modifier
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun Form(
    onClose: () -> Unit,
    onAddFund: (String, Double) -> Unit,
    onIsinError: (String?) -> Unit
) {
    var isin by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Column {
        Text(
            text = "ISIN",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = isin,
            onValueChange = { isin = it },
            icon = Icons.Outlined.QrCode2,
            placeholder = "US0000000000",
            cornerRadius = 16.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "International Securities Identification Number",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Número de Participaciones",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantity,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) { quantity = it }
            },
            icon = Icons.Outlined.PieChartOutline,
            placeholder = "0.0",
            cornerRadius = 16.dp,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                text = "Cancelar",
                contentDescription = "Pulsa para no añadir ningún fondo",
                style = ButtonStyle.SECONDARY,
                onClick = onClose,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            val quantity = quantity.normalizeDouble() ?: 0.0

            Button(
                text = "Añadir",
                contentDescription = "Añade un nuevo fondo a tu cartera",
                style = ButtonStyle.PRIMARY,
                onClick = {
                    if (isin.isValidIsin()) {
                        onAddFund(isin, quantity)
                    } else if (isin.isEmpty()) {
                        onIsinError("Introduce un ISIN para añadirlo a tu cartera.")
                    } else {
                        onIsinError("Introduce un ISIN válido para añadirlo a tu cartera.")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .applyIf(!isin.isValidIsin()) { shakeClickEffect() }
            )
        }
    }
}

@Preview
@Composable
private fun AddFundBottomSheetPreview() {
    OneWalletTheme {
        AddFundBottomSheet(
            visible = true,
            onDismiss = {},
            onAddFund = { _, _ -> },
            onIsinError = {}
        )
    }
}
