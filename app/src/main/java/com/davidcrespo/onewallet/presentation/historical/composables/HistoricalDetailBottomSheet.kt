package com.davidcrespo.onewallet.presentation.historical.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.domain.model.investment.Investment
import kotlinx.coroutines.launch
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalDetailBottomSheet(
    investments: List<Investment>,
    previousInvestments: List<Investment>,
    visible: Boolean,
    onClickInvestment: (Investment) -> Unit,
    onDismiss: () -> Unit
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
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        modifier = Modifier.padding(top = 120.dp)
    ) {
        SheetContent(
            investments = investments,
            previousInvestments = previousInvestments,
            onClickInvestment = onClickInvestment,
            onClose = {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
            }
        )

        Spacer(Modifier.height(80.dp))
    }
}

// ---------- UI ----------
@Composable
private fun SheetContent(
    investments: List<Investment>,
    previousInvestments: List<Investment>,
    onClickInvestment: (Investment) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
    ) {
        Header(
            investment = investments.first(),
            onClose = onClose
        )

        Spacer(Modifier.height(16.dp))

        OWAnimatedList(
            items = investments,
            key = { it.symbol },
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            itemContent = { modifier, historicalItem, index ->
                val previousMonthItem = previousInvestments.find { it.symbol == historicalItem.symbol }
                OWInvestmentItem(
                    item = historicalItem,
                    previousMonthItem = previousMonthItem,
                    section = SectionType.HISTORICAL,
                    onClick = { onClickInvestment(it) },
                    modifier = modifier
                )
            }
        )

    }
}

@Composable
private fun Header(
    investment: Investment,
    onClose: () -> Unit
) {
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Spacer(Modifier.height(8.dp))

            val monthName = Month.of(investment.month)
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            Text(
                text = "Inversiones de $monthName ${investment.year}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onTertiary)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SheetHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(5.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}
