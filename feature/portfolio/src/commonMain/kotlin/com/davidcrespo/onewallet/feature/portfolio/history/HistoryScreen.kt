package com.davidcrespo.onewallet.feature.portfolio.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.core.composables.modifiers.privacyBlur
import com.davidcrespo.onewallet.core.designsystem.composables.OWLoader
import com.davidcrespo.onewallet.core.extensions.orEmpty
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.export_history
import com.davidcrespo.onewallet.core.generated.resources.import_history
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.feature.portfolio.history.composables.HistoryInvestmentDetailBottomSheet
import com.davidcrespo.onewallet.feature.portfolio.history.composables.HistoryList
import com.davidcrespo.onewallet.feature.portfolio.history.composables.HistoryMonthDetailBottomSheet
import com.davidcrespo.onewallet.feature.portfolio.util.rememberFilePicker
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryTab(
    currency: CurrencyView,
    isBalanceVisible: Boolean,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val launchFilePicker = rememberFilePicker { uri ->
        viewModel.handleIntent(HistoryIntent.OnFileSelected(uri))
    }

    LaunchedEffect(viewModel) {
        viewModel.handleIntent(HistoryIntent.LoadInitialData)
    }

    LaunchedEffect(currency) {
        viewModel.handleIntent(HistoryIntent.OnCurrencyChanged)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryEffect.ShowFilePicker -> launchFilePicker()
                is HistoryEffect.ShowSnackbar -> {
                    launch {
                        snackbarHostState.showSnackbar(getString(effect.message))
                    }
                }
            }
        }
    }

    HistoryScreen(
        uiState = uiState,
        onAction = viewModel::handleIntent,
        isBalanceVisible = isBalanceVisible,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
private fun HistoryScreen(
    uiState: HistoryUiState,
    onAction: (HistoryIntent) -> Unit,
    isBalanceVisible: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val hideBackground =
        uiState.selectedMonthDetail != null ||
                uiState.selectedInvestment != null

    val blurRadius by animateDpAsState(
        targetValue = if (hideBackground) 16.dp else 0.dp,
        animationSpec = tween(
            durationMillis = if (hideBackground) 300 else 200
        ),
        label = "blur"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (hideBackground) 0.32f else 0f,
        animationSpec = tween(
            durationMillis = if (hideBackground) 300 else 200
        ),
        label = "overlay"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .privacyBlur(blurRadius)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    text = stringResource(Res.string.import_history),
                    contentDescription = stringResource(Res.string.import_history),
                    style = ButtonStyle.SECONDARY,
                    onClick = { onAction(HistoryIntent.ImportHistory) },
                    leadingIcon = Icons.Outlined.FileUpload,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    text = stringResource(Res.string.export_history),
                    contentDescription = stringResource(Res.string.export_history),
                    style = ButtonStyle.SECONDARY,
                    onClick = { onAction(HistoryIntent.ExportHistory) },
                    leadingIcon = Icons.Outlined.FileDownload,
                    modifier = Modifier.weight(1f)
                )
            }

            when {
                uiState.isLoading -> {
                    OWLoader(
                        modifier = Modifier
                            .fillMaxSize()
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
                        modifier = Modifier
                            .weight(1f)
                            /*.enableCoachMark(
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
                            )*/
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
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
                currency = uiState.selectedCurrency,
                onDismiss = { onAction(HistoryIntent.DismissInvestmentDetail) }
            )
        }
    }
}
