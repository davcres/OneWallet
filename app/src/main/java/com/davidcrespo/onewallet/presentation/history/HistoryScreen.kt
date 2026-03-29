package com.davidcrespo.onewallet.presentation.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.core.composables.modifiers.privacyBlur
import com.davidcrespo.onewallet.core.extensions.orEmpty
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWLoader
import com.davidcrespo.onewallet.presentation.history.composables.HistoryInvestmentDetailBottomSheet
import com.davidcrespo.onewallet.presentation.history.composables.HistoryList
import com.davidcrespo.onewallet.presentation.history.composables.HistoryMonthDetailBottomSheet
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.portfolio.models.PortfolioCoachmarks
import com.pseudoankit.coachmark.LocalCoachMarkScope
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.pseudoankit.coachmark.shape.Arrow
import com.pseudoankit.coachmark.shape.Balloon
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryTab(
    currency: CurrencyView,
    isBalanceVisible: Boolean,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.handleIntent(HistoryIntent.LoadInitialData)
    }

    LaunchedEffect(currency) {
        viewModel.handleIntent(HistoryIntent.OnCurrencyChanged)
    }

    HistoryScreen(
        uiState = uiState,
        onAction = viewModel::handleIntent,
        isBalanceVisible = isBalanceVisible,
        modifier = modifier
    )
}

@Composable
private fun HistoryScreen(
    uiState: HistoryUiState,
    onAction: (HistoryIntent) -> Unit,
    isBalanceVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val hideBackground =
        uiState.selectedMonthDetail != null ||
                uiState.selectedInvestment != null

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .privacyBlur(blurRadius)
    ) {
        when {
            uiState.isLoading -> {
                OWLoader(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            }
            else -> {
                HistoryList(
                    items = uiState.history,
                    currency = uiState.selectedCurrency,
                    onClick = {
                        onAction(
                            HistoryIntent.SelectMonth(
                                it.first().year,
                                it.first().month
                            )
                        )
                    },
                    isBalanceVisible = isBalanceVisible,
                    modifier = Modifier.enableCoachMark(
                        key = PortfolioCoachmarks.HISTORY_LIST,
                        toolTipPlacement = ToolTipPlacement.Top,
                        tooltip = {
                            Balloon(
                                arrow = Arrow.Bottom(),
                                modifier = Modifier.widthIn(max = 200.dp),
                                bgColor = MaterialTheme.colorScheme.primaryContainer,
                                cornerRadius = 16.dp,
                                padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(PortfolioCoachmarks.HISTORY_LIST.tooltip),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        coachMarkScope = LocalCoachMarkScope.current
                    )
                )

                if (hideBackground) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = overlayAlpha))
                    )
                }

                HistoryMonthDetailBottomSheet(
                    investments = uiState.selectedMonthDetail.orEmpty(),
                    previousInvestments = uiState.selectedPreviousMonth.orEmpty(),
                    currency = uiState.selectedCurrency,
                    visible = uiState.selectedMonthDetail != null,
                    onClickInvestment = { onAction(HistoryIntent.SelectInvestment(it)) },
                    onDismiss = { onAction(HistoryIntent.DismissBottomSheet) },
                    hideBackground = uiState.selectedInvestment != null,
                    isBalanceVisible = isBalanceVisible
                )

                uiState.selectedInvestment?.let {
                    HistoryInvestmentDetailBottomSheet(
                        visible = true,
                        investment = it,
                        previousMonthInvestment = uiState.selectedPreviousInvestment,
                        onDismiss = { onAction(HistoryIntent.DismissInvestmentDetail) }
                    )
                }
            }
        }
    }
}
