package com.davidcrespo.onewallet.presentation.onboarding

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PortfolioOnboardingRoot(
    onStartTutorial: () -> Unit,
    onSkipTutorial: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortfolioOnboardingViewModel = koinViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.handleIntent(PortfolioOnboardingIntent.StartTutorial)
    }

    PortfolioOnboardingScreen(
        onStartTutorial = {
            onStartTutorial()
        },
        onSkipTutorial = {
            viewModel.handleIntent(PortfolioOnboardingIntent.SkipTutorial)
            onSkipTutorial()
        },
        modifier = modifier
    )
}

@Composable
private fun PortfolioOnboardingScreen(
    onStartTutorial: () -> Unit,
    onSkipTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    text = stringResource(R.string.portfolio_onboarding_start),
                    contentDescription = stringResource(R.string.portfolio_onboarding_start),
                    style = ButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onStartTutorial
                )

                Button(
                    text = stringResource(R.string.portfolio_onboarding_skip),
                    contentDescription = stringResource(R.string.portfolio_onboarding_skip),
                    style = ButtonStyle.TERTIARY,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSkipTutorial
                )
            }
        },
        contentWindowInsets = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            WindowInsets(0, 0, 0, 0)
        } else {
            ScaffoldDefaults.contentWindowInsets
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.portfolio_onboarding_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.portfolio_onboarding_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun PortfolioOnboardingScreenPreview() {
    OneWalletTheme {
        PortfolioOnboardingScreen(
            onStartTutorial = {},
            onSkipTutorial = {}
        )
    }
}
