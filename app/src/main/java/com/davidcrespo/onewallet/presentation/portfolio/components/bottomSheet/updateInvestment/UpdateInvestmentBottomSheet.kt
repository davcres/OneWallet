package com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.updateInvestment

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.CopiableText
import com.davidcrespo.onewallet.core.composables.TextField
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.core.composables.modifiers.animations.shakeClickEffect
import com.davidcrespo.onewallet.core.extensions.applyIf
import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.hasIsin
import com.davidcrespo.onewallet.domain.model.investment.isManual
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWCurrencyPrice
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.TrendDisplay
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.bottomSheet.SheetHandle
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateInvestmentBottomSheet(
    investment: InvestmentView,
    currency: CurrencyView,
    visible: Boolean,
    onDismiss: () -> Unit,
    onEditInvestment: (newQuantity: Double) -> Unit,
    onQuantityError: (String) -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { SheetHandle() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        scrimColor = Color.Transparent // Disable default scrim (darker content out of bottom sheet) to see light error
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                SheetContent(
                    investment = investment,
                    currency = currency,
                    onClose = {
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion { onDismiss() }
                    },
                    onEditQuantity = { quantity -> onEditInvestment(quantity) },
                    onQuantityError = onQuantityError,
                    snackbarHostState = snackbarHostState
                )

                Spacer(Modifier.height(20.dp))
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

// ---------- UI ----------
@Composable
private fun SheetContent(
    investment: InvestmentView,
    currency: CurrencyView,
    onClose: () -> Unit,
    onEditQuantity: (Double) -> Unit,
    onQuantityError: (String) -> Unit,
    snackbarHostState: SnackbarHostState
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

        Header(
            investment = investment,
            onClose = onClose,
            snackbarHostState = snackbarHostState
        )

        Spacer(Modifier.height(16.dp))

        CurrentHolding(
            investment = investment,
            currency = currency
        )

        Spacer(Modifier.height(32.dp))

        Form(
            investment = investment,
            currency = currency,
            onClose = onClose,
            onEditQuantity = onEditQuantity,
            onError = onQuantityError
        )
    }
}

@Composable
private fun Header(investment: InvestmentView, onClose: () -> Unit, snackbarHostState: SnackbarHostState) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Spacer(Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = investment.type.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (investment.type.hasIsin()) {
                    CopiableText(
                        text = investment.symbol,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        snackbarHostState = snackbarHostState
                    )
                } else {
                    Text(
                        text = investment.symbol,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (investment.symbol != investment.name && investment.name.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = investment.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        OWIconButton(
            imageVector = Icons.Outlined.Close,
            onClick = onClose,
            contentDescription = stringResource(R.string.close_cd),
            modifier = Modifier
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun CurrentHolding(
    investment: InvestmentView,
    currency: CurrencyView,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.onTertiary,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            if (investment.type.isMarket()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.current_holding_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.current_holding, investment.quantity),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                VerticalDivider(
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(CircleShape)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.total_value),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(8.dp))

                OWCurrencyPrice(
                    price = investment.displayPrice * investment.quantity,
                    currency = currency,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun Form(
    investment: InvestmentView,
    currency: CurrencyView,
    onClose: () -> Unit,
    onEditQuantity: (newQuantity: Double) -> Unit,
    onError: (String) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    val newPrice = investment.displayPrice * quantity.normalizeDouble()

    Column {
        Text(
            text = if (investment.type.isMarket()) stringResource(R.string.new_quantity_market) else stringResource(
                R.string.new_quantity_manual
            ),
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
            icon = if (investment.type.isMarket()) Icons.Outlined.PieChartOutline else investment.originalCurrency.icon,
            placeholder = "0.0",
            cornerRadius = 16.dp,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        if (investment.type.isManual()) {
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
                    text = stringResource(R.string.new_quantity_manual_info, investment.originalCurrency.symbol),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.estimated_new_value),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OWCurrencyPrice(
                price = newPrice,
                currency = currency,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                textColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val variance = newPrice - (investment.displayPrice * investment.quantity)

            val backgroundColor = when {
                variance > 0 -> MaterialTheme.colorScheme.primaryContainer
                variance < 0 -> MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = backgroundColor
            ) {
                TrendDisplay(
                    value = variance,
                    showPercentage = false,
                    currency = currency,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                text = stringResource(R.string.cancel_action),
                contentDescription = stringResource(R.string.cancel_edit_investment_cd),
                style = ButtonStyle.SECONDARY,
                onClick = onClose,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            val quantity = quantity.normalizeDouble()
            val errorInvalidQuantity = stringResource(R.string.error_quantity_empty)

            Button(
                text = stringResource(R.string.update_quantity_action),
                contentDescription = stringResource(R.string.update_quantity_action),
                style = ButtonStyle.PRIMARY,
                onClick = {
                    if (quantity >= 0) {
                        onEditQuantity(quantity)
                    } else {
                        onError(errorInvalidQuantity)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .applyIf(quantity < 0) { shakeClickEffect() }
            )
        }
    }
}

@Preview
@Composable
private fun UpdateInvestmentBottomSheetPreview() {
    OneWalletTheme {
        UpdateInvestmentBottomSheet(
            investment = InvestmentView(
                symbol = "AAPL",
                name = "Apple",
                quantity = 10.0,
                displayPrice = 150.0,
                displayPreviousPrice = 140.0,
                originalPrice = 150.0,
                originalPreviousPrice = 140.0,
                originalCurrency = CurrencyView.get(EUR),
                changePercent = 10.0,
                type = InvestmentType.STOCK,
                month = 0,
                year = 0
            ),
            currency = CurrencyView.get(EUR),
            visible = true,
            onDismiss = {},
            onEditInvestment = { _ -> },
            onQuantityError = {}
        )
    }
}
