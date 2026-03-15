package com.davidcrespo.onewallet.presentation.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.core.composables.modifiers.privacyBlur
import com.davidcrespo.onewallet.core.extensions.orEmpty
import com.davidcrespo.onewallet.presentation.history.composables.HistoryInvestmentDetailBottomSheet
import com.davidcrespo.onewallet.presentation.history.composables.HistoryList
import com.davidcrespo.onewallet.presentation.history.composables.HistoryMonthDetailBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryTab(
    isBalanceVisible: Boolean,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.handleIntent(HistoryIntent.LoadInitialData)
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
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
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
