package com.davidcrespo.onewallet.presentation.historical

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.modifiers.privacyBlur
import com.davidcrespo.onewallet.core.extensions.orEmpty
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalInvestmentDetailBottomSheet
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalList
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalMonthDetailBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoricalRoot(
    isBalanceVisible: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoricalViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.handleIntent(HistoricalIntent.LoadInitialData)
    }

    HistoricalScreen(
        uiState = uiState,
        onAction = viewModel::handleIntent,
        onBack = onBack,
        isBalanceVisible = isBalanceVisible,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoricalScreen(
    uiState: HistoricalUiState,
    onAction: (HistoricalIntent) -> Unit,
    onBack: () -> Unit,
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.historical_monthly_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_cd)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )

        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .privacyBlur(blurRadius)
                .background(Color.Black.copy(alpha = overlayAlpha))
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
                    HistoricalList(
                        items = uiState.history,
                        currency = uiState.selectedCurrency,
                        onClick = {
                            onAction(
                                HistoricalIntent.SelectMonth(
                                    it.first().year,
                                    it.first().month
                                )
                            )
                        },
                        isBalanceVisible = isBalanceVisible,
                    )

                    HistoricalMonthDetailBottomSheet(
                        investments = uiState.selectedMonthDetail.orEmpty(),
                        previousInvestments = uiState.selectedPreviousMonth.orEmpty(),
                        currency = uiState.selectedCurrency,
                        visible = uiState.selectedMonthDetail != null,
                        onClickInvestment = { onAction(HistoricalIntent.SelectInvestment(it)) },
                        onDismiss = { onAction(HistoricalIntent.DismissBottomSheet) },
                        hideBackground = uiState.selectedInvestment != null,
                        isBalanceVisible = isBalanceVisible
                    )

                    uiState.selectedInvestment?.let {
                        HistoricalInvestmentDetailBottomSheet(
                            visible = true,
                            investment = it,
                            previousMonthInvestment = uiState.selectedPreviousInvestment,
                            onDismiss = { onAction(HistoricalIntent.DismissInvestmentDetail) }
                        )
                    }
                }
            }
        }
    }
}
