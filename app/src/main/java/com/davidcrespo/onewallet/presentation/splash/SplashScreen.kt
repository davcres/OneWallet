package com.davidcrespo.onewallet.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.davidcrespo.onewallet.R
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private const val START_ANIMATION = 0.65f
private const val ANIMATION_DURATION = 400

@Composable
fun SplashRoot(
    onAnimationFinished: (onboardingCompleted: Boolean, portfolioOnboardingCompleted: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.handleIntent(SplashIntent.LoadMarkets)
        viewModel.handleIntent(SplashIntent.IsOnboardingCompleted)
    }

    SplashScreen(
        uiState = uiState,
        onAnimationFinished = onAnimationFinished,
        modifier = modifier
    )
}

@Composable
private fun SplashScreen(
    uiState: SplashUiState,
    onAnimationFinished: (onboardingCompleted: Boolean, portfolioOnboardingCompleted: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        speed = 2.5f
    )

    var isSkipped by remember { mutableStateOf(false) }

    val startAnimation by remember {
        derivedStateOf { progress > START_ANIMATION || isSkipped }
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 5f else 0.6f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION
        ),
        label = "scaleAnimation"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION
        ),
        label = "alphaAnimation"
    )

    LaunchedEffect(startAnimation) {
        if (startAnimation) {
            delay(ANIMATION_DURATION.toLong())
            onAnimationFinished(
                uiState.onboardingCompleted ?: false,
                uiState.portfolioOnboardingCompleted ?: false
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                if (!startAnimation) {
                    isSkipped = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha = alpha)
                .scale(scale = scale)
        )
    }
}
