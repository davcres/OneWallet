package com.davidcrespo.onewallet.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.portfolio_onboarding_description
import com.davidcrespo.onewallet.core.generated.resources.portfolio_onboarding_skip
import com.davidcrespo.onewallet.core.generated.resources.portfolio_onboarding_start
import com.davidcrespo.onewallet.core.generated.resources.portfolio_onboarding_title
import com.davidcrespo.onewallet.feature.onboarding.util.rememberOnboardingContentWindowInsets
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

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
                    text = stringResource(Res.string.portfolio_onboarding_start),
                    contentDescription = stringResource(Res.string.portfolio_onboarding_start),
                    style = ButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onStartTutorial
                )

                Button(
                    text = stringResource(Res.string.portfolio_onboarding_skip),
                    contentDescription = stringResource(Res.string.portfolio_onboarding_skip),
                    style = ButtonStyle.TERTIARY,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSkipTutorial
                )
            }
        },
        contentWindowInsets = rememberOnboardingContentWindowInsets(),
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
                text = stringResource(Res.string.portfolio_onboarding_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.portfolio_onboarding_description),
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
