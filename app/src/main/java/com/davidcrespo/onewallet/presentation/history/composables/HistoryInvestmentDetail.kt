package com.davidcrespo.onewallet.presentation.history.composables

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.CopiableText
import com.davidcrespo.onewallet.core.composables.DashedDivider
import com.davidcrespo.onewallet.core.models.Quadruple
import com.davidcrespo.onewallet.domain.model.investment.hasIsin
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.bottomSheet.SheetHandle
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryInvestmentDetailBottomSheet(
    visible: Boolean,
    investment: InvestmentView,
    previousMonthInvestment: InvestmentView?,
    onDismiss: () -> Unit
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
            Column {
                SheetContent(
                    investment = investment,
                    previousMonthInvestment = previousMonthInvestment,
                    onClose = {
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion { onDismiss() }
                    },
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
    previousMonthInvestment: InvestmentView?,
    onClose: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val percentage =
        if (previousMonthInvestment?.displayPrice == null || investment.displayPrice == 0.0 || previousMonthInvestment.displayPrice == 0.0) {
            0.0
        } else {
            if (investment.type.isMarket()) {
                (investment.displayPrice - previousMonthInvestment.displayPrice) / previousMonthInvestment.displayPrice * 100
            } else {
                ((investment.displayPrice * investment.quantity) - (previousMonthInvestment.displayPrice * previousMonthInvestment.quantity)) / (previousMonthInvestment.displayPrice * previousMonthInvestment.quantity) * 100
            }
        }

    val (percentageIcon, percentageColor, backgroundColor, prefix) = when {
        percentage > 0 -> Quadruple(
            Icons.AutoMirrored.Filled.TrendingUp,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            "+"
        )

        percentage < 0 -> Quadruple(
            Icons.AutoMirrored.Filled.TrendingDown,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.error.copy(alpha = 0.25f),
            ""
        )

        else -> Quadruple(
            Icons.AutoMirrored.Filled.TrendingFlat,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
            ""
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OWIconButton(
            imageVector = Icons.Outlined.Close,
            onClick = onClose,
            contentDescription = stringResource(R.string.close_cd),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = investment.type.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (investment.type.hasIsin()) {
                CopiableText(
                    text = investment.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    snackbarHostState = snackbarHostState
                )
            } else {
                Text(
                    text = investment.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (investment.symbol != investment.name && investment.name.isNotEmpty()) {
                Text(
                    text = investment.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider()

            if (investment.type.isMarket()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.shares_in_portfolio_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(R.string.price_per_share_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "%.2f".format(investment.quantity),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "%.2f €".format(investment.displayPrice),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.total_value_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                if (previousMonthInvestment != null && previousMonthInvestment.displayPrice > 0.0) {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "%.2f €".format(investment.quantity * investment.displayPrice),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                if (previousMonthInvestment != null && previousMonthInvestment.displayPrice > 0.0) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = backgroundColor
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = percentageIcon,
                                    contentDescription = stringResource(R.string.percentage_icon_cd),
                                    tint = percentageColor
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "($prefix%.2f %%)".format(percentage),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = percentageColor
                                )
                            }
                        }
                    }
                }
            }

            if (previousMonthInvestment != null && previousMonthInvestment.displayPrice > 0.0) {
                DashedDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.pl_month_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    val variance = if (investment.type.isMarket()) {
                        investment.displayPrice - previousMonthInvestment.displayPrice
                    } else {
                        (investment.displayPrice * investment.quantity) - (previousMonthInvestment.displayPrice * previousMonthInvestment.quantity)
                    }

                    Text(
                        text = "$prefix%.2f €".format(variance),
                        style = MaterialTheme.typography.titleLarge,
                        color = percentageColor,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}