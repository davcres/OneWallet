package com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.TextField
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.core.composables.modifiers.animations.shakeClickEffect
import com.davidcrespo.onewallet.core.extensions.applyIf
import com.davidcrespo.onewallet.core.extensions.isValidIsin
import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWLoader
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.bottomSheet.SheetHandle
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundBottomSheet(
    visible: Boolean,
    isLoading: Boolean,
    isFund: Boolean,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            SheetContent(
                isLoading = isLoading,
                isFund = isFund,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
                onAddFund = { isin, quantity -> onAddFund(isin, quantity) },
                onIsinError = onIsinError
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ---------- UI ----------
@Composable
private fun SheetContent(
    isLoading: Boolean,
    isFund: Boolean,
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
        AnimatedVisibility(
            visible = isLoading
        ) {
            Column {
                Spacer(Modifier.height(16.dp))

                OWLoader(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Header(isFund = isFund, onClose = onClose)

        Spacer(Modifier.height(16.dp))

        Form(
            onClose = onClose,
            onAddFund = onAddFund,
            onIsinError = onIsinError
        )
    }
}

@Composable
private fun Header(isFund: Boolean, onClose: () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Row {
                Icon(
                    imageVector = if (isFund) InvestmentType.FUND.icon else InvestmentType.ETF.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isFund) stringResource(R.string.add_new_fund_title) else stringResource(R.string.add_new_etf_title),
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
    onClose: () -> Unit,
    onAddFund: (String, Double) -> Unit,
    onIsinError: (String?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isin by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Column {
        Text(
            text = stringResource(R.string.isin_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = isin,
            onValueChange = { input ->
                isin = input
                    .uppercase()
                    .filter { it.isLetterOrDigit() }
                    .take(12)
            },
            placeholder = "US0000000000",
            contentDescription = stringResource(R.string.asset_isin_cd),
            leadingIcon = Icons.Outlined.QrCode2,
            hasClearIcon = true,
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
                text = stringResource(R.string.isin_helper_text),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.quantity_shares_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantity,
            onValueChange = { input ->
                val normalized = input.replace('.', ',')
                if (normalized.all { it.isDigit() || it == ',' } && normalized.count { it == ',' } <= 1) {
                    quantity = normalized
                }
            },
            placeholder = "0.0",
            contentDescription = stringResource(R.string.asset_market_quantity_cd),
            leadingIcon = Icons.Outlined.PieChartOutline,
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
            val errorIsinEmpty = stringResource(R.string.error_isin_empty)
            val errorIsinInvalid = stringResource(R.string.error_isin_invalid)

            Button(
                text = stringResource(R.string.add_action),
                contentDescription = stringResource(R.string.add_fund_cd),
                style = ButtonStyle.PRIMARY,
                onClick = {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()

                    if (isin.isValidIsin()) {
                        onAddFund(isin, quantity)
                    } else if (isin.isEmpty()) {
                        onIsinError(errorIsinEmpty)
                    } else {
                        onIsinError(errorIsinInvalid)
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
            isLoading = true,
            isFund = true,
            onDismiss = {},
            onAddFund = { _, _ -> },
            onIsinError = {}
        )
    }
}
