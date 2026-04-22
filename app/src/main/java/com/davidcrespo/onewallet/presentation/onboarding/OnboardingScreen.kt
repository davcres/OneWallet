package com.davidcrespo.onewallet.presentation.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.presentation.onboarding.models.OnboardingPage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingRoot(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    OnboardingScreen(
        onFinish = {
            viewModel.handleIntent(OnboardingIntent.SetOnboardingCompleted)
            onFinish()
        },
        modifier = modifier
    )
}

@Composable
private fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_1_title),
            description = stringResource(R.string.onboarding_1_description),
            icon = R.drawable.onboarding_1
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_2_title),
            description = stringResource(R.string.onboarding_2_description),
            icon = R.drawable.onboarding_2
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_3_title),
            description = stringResource(R.string.onboarding_3_description),
            icon = R.drawable.onboarding_3
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_4_title),
            description = stringResource(R.string.onboarding_4_description),
            icon = R.drawable.onboarding_4
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_5_title),
            description = stringResource(R.string.onboarding_5_description),
            icon = R.drawable.onboarding_5
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_6_title),
            description = stringResource(R.string.onboarding_6_description),
            icon = R.drawable.onboarding_6
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // Necesaria para no solapar al no utilizar una BottomBar estandar de material3
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PageIndicator(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage
                )

                Spacer(modifier = Modifier.height(32.dp))

                val isLastPage = pagerState.currentPage == pages.size - 1
                val buttonText = if (isLastPage) stringResource(R.string.onboarding_button_get_started) else stringResource(R.string.onboarding_button_next)

                Button(
                    text = buttonText,
                    contentDescription = buttonText,
                    style = ButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (isLastPage) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { pageIndex ->
            OnboardingContent(page = pages[pageIndex])
        }
    }
}

@Composable
fun OnboardingContent(
    page: OnboardingPage
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(page.icon),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(50.dp))
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 12.dp,
                animationSpec = tween(500, easing = LinearEasing),
                label = "dotWidth"
            )
            val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .height(12.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
