package com.davidcrespo.onewallet.presentation.historical.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.modifiers.privacyBlur
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.bottomSheet.SheetHandle
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalMonthDetailBottomSheet(
    investments: ImmutableList<InvestmentView>,
    previousInvestments: ImmutableList<InvestmentView>,
    currency: CurrencyView,
    visible: Boolean,
    onClickInvestment: (InvestmentView) -> Unit,
    onDismiss: () -> Unit,
    hideBackground: Boolean,
    isBalanceVisible: Boolean,
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxSheetHeight = screenHeight * 0.80f

    val blurRadius by animateDpAsState(
        targetValue = if (hideBackground) 16.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 1000
        ),
        label = "blur"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (hideBackground) 0.32f else 0f,
        label = "overlay"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { SheetHandle() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        modifier = Modifier.padding(top = 120.dp)
    ) {
        Box(Modifier
            .fillMaxWidth()
            .privacyBlur(blurRadius)
            .background(Color.Black.copy(alpha = overlayAlpha))
        ) {
            Column {
                SheetContent(
                    investments = investments,
                    previousInvestments = previousInvestments,
                    currency = currency,
                    onClickInvestment = onClickInvestment,
                    onClose = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    maxSheetHeight = maxSheetHeight,
                    isBalanceVisible = isBalanceVisible
                )

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

// ---------- UI ----------
@Composable
private fun SheetContent(
    investments: ImmutableList<InvestmentView>,
    previousInvestments: ImmutableList<InvestmentView>,
    currency: CurrencyView,
    onClickInvestment: (InvestmentView) -> Unit,
    onClose: () -> Unit,
    maxSheetHeight: Dp = Dp.Unspecified,
    isBalanceVisible: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxSheetHeight)
            .padding(horizontal = 16.dp)
    ) {
        Header(
            investment = investments.first(),
            onClose = onClose
        )

        Spacer(Modifier.height(16.dp))

        OWAnimatedList(
            items = investments,
            key = { it.symbol },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            itemContent = { modifier, historicalItem, index ->
                val previousMonthItem = previousInvestments.find { it.symbol == historicalItem.symbol }
                OWInvestmentItem(
                    item = historicalItem,
                    currency = currency,
                    previousMonthItem = previousMonthItem,
                    section = SectionType.HISTORICAL,
                    onClick = { onClickInvestment(it) },
                    modifier = modifier,
                    isBalanceVisible = isBalanceVisible
                )
            }
        )

    }
}

@Composable
private fun Header(
    investment: InvestmentView,
    onClose: () -> Unit
) {
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Spacer(Modifier.height(8.dp))

            val monthName = Month.of(investment.month)
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            Text(
                text = stringResource(R.string.investments_of_month_fmt, monthName, investment.year),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        OWIconButton(
            imageVector =  Icons.Outlined.Close,
            onClick = onClose,
            contentDescription = stringResource(R.string.close_cd),
            modifier = Modifier
                .align(Alignment.CenterEnd)
        )
    }
}
