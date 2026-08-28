package com.davidcrespo.onewallet.feature.portfolio.components.bottomSheet.investmentType

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentAttribute
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.core.models.*
import com.davidcrespo.onewallet.core.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.core.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.core.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.bottomSheet.SheetHandle
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.core.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentAttributeBottomSheet(
    attribute: InvestmentAttribute,
    investments: ImmutableList<InvestmentView>,
    currency: CurrencyView,
    visible: Boolean,
    onDismiss: () -> Unit,
    isBalanceVisible: Boolean,
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxSheetHeight = screenHeight * 0.80f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { SheetHandle() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        scrimColor = Color.Transparent, // Disable default scrim (darker content out of bottom sheet) to see light error
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            SheetContent(
                attribute = attribute,
                investments = investments,
                currency = currency,
                onClose = {
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion { onDismiss() }
                },
                maxSheetHeight = maxSheetHeight,
                isBalanceVisible = isBalanceVisible
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ---------- UI ----------
@Composable
private fun SheetContent(
    attribute: InvestmentAttribute,
    investments: ImmutableList<InvestmentView>,
    currency: CurrencyView,
    onClose: () -> Unit,
    maxSheetHeight: Dp = Dp.Unspecified,
    isBalanceVisible: Boolean = true,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxSheetHeight)
    ) {
        Header(
            attribute = attribute,
            onClose = onClose,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Spacer(Modifier.height(16.dp))

        ItemsByTypeList(
            items = investments,
            currency = currency,
            modifier = Modifier.weight(1f, fill = false),
            isBalanceVisible = isBalanceVisible
        )
    }
}

@Composable
private fun Header(
    attribute: InvestmentAttribute,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
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
                    imageVector = attribute.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = attribute.nameRes?.let { stringResource(it) } ?: attribute.id,
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
                .align(Alignment.TopEnd)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsByTypeList(
    items: ImmutableList<InvestmentView>,
    currency: CurrencyView,
    modifier: Modifier = Modifier,
    isBalanceVisible: Boolean,
) {
    OWAnimatedList(
        items = items,
        key = { it.symbol },
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 32.dp),
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        itemContent = { modifier, priceItem, index ->
            OWInvestmentItem(
                item = priceItem,
                currency = currency,
                section = SectionType.PORTFOLIO,
                onClick = {},
                modifier = modifier,
                isBalanceVisible = isBalanceVisible
            )
        }
    )
}

@Preview
@Composable
private fun InvestmentAttributeBottomSheetPreview() {
    OneWalletTheme {
        InvestmentAttributeBottomSheet(
            attribute = InvestmentType.STOCK,
            investments = persistentListOf(
                InvestmentView(
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
                )
            ),
            currency = CurrencyView.get(EUR),
            visible = true,
            onDismiss = {},
            isBalanceVisible = true
        )
    }
}
