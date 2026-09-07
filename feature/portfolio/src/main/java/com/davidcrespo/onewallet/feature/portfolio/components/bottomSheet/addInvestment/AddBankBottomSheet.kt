package com.davidcrespo.onewallet.feature.portfolio.components.bottomSheet.addInvestment

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.TextField
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.core.composables.modifiers.animations.shakeClickEffect
import com.davidcrespo.onewallet.core.extensions.applyIf
import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.core.extensions.toSpanishFormatNumber
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.core.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.bottomSheet.SheetHandle
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.core.models.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBankBottomSheet(
    visible: Boolean,
    currency: CurrencyView,
    isBank: Boolean,
    onDismiss: () -> Unit,
    onAddBank: (String, Double, CurrencyView) -> Unit
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            SheetContent(
                currency = currency,
                isBank = isBank,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
                onAddBank = { name, quantity, currency -> onAddBank(name, quantity, currency) }
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ---------- UI ----------
@Composable
private fun SheetContent(
    currency: CurrencyView,
    isBank: Boolean,
    onClose: () -> Unit,
    onAddBank: (String, Double, CurrencyView) -> Unit
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
        Spacer(Modifier.height(16.dp))

        Header(isBank = isBank, onClose = onClose)

        Spacer(Modifier.height(16.dp))

        Form(
            currency = currency,
            isBank = isBank,
            onClose = onClose,
            onAddBank = onAddBank
        )
    }
}

@Composable
private fun Header(isBank: Boolean, onClose: () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Row {
                Icon(
                    imageVector = if (isBank) InvestmentType.BANK.icon else InvestmentType.OTHER.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBank) stringResource(R.string.add_bank_title) else stringResource(R.string.add_other_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        OWIconButton(
            imageVector = Icons.Outlined.Close,
            onClick = onClose,
            contentDescription = stringResource(R.string.close_cd),
            modifier = Modifier
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun Form(
    currency: CurrencyView,
    isBank: Boolean,
    onClose: () -> Unit,
    onAddBank: (String, Double, CurrencyView) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Column {
        Text(
            text = if (isBank) stringResource(R.string.bank_name_label) else stringResource(R.string.other_name_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = name,
            onValueChange = { input ->
                name = input
            },
            placeholder = if (isBank) stringResource(R.string.bank_name_placeholder) else stringResource(R.string.other_name_placeholder),
            contentDescription = stringResource(R.string.asset_name_cd),
            leadingIcon = Icons.Default.Addchart,
            hasClearIcon = true,
            cornerRadius = 16.dp
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.total_money_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantity,
            onValueChange = { input ->
                val normalized = input.toSpanishFormatNumber()
                if (normalized.all { it.isDigit() || it == ',' } && normalized.count { it == ',' } <= 1) {
                    quantity = normalized
                }
            },
            placeholder = "0.0",
            contentDescription = stringResource(R.string.asset_manual_quantity_cd),
            leadingIcon = if (currency.code == EUR) Icons.Filled.Euro else Icons.Filled.AttachMoney,
            hasClearIcon = true,
            cornerRadius = 16.dp,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                text = stringResource(R.string.cancel_action),
                contentDescription = stringResource(R.string.cancel_add_fund_cd),
                style = ButtonStyle.SECONDARY,
                onClick = onClose,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            val quantity = quantity.normalizeDouble()

            Button(
                text = stringResource(R.string.add_action),
                contentDescription = stringResource(R.string.add_bank_cd),
                style = ButtonStyle.PRIMARY,
                onClick = {
                    if (name.isNotEmpty()) {
                        onAddBank(name, quantity, currency)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .applyIf(name.isEmpty()) { shakeClickEffect() }
            )
        }
    }
}

@Preview
@Composable
private fun AddBankBottomSheetPreview() {
    OneWalletTheme {
        AddBankBottomSheet(
            visible = true,
            currency = CurrencyView.get(EUR),
            isBank = true,
            onDismiss = {},
            onAddBank = { _, _, _ -> },
        )
    }
}
