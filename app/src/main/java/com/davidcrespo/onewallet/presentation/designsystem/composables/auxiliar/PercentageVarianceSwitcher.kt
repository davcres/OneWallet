package com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import kotlinx.coroutines.delay

@Composable
fun PercentageVarianceSwitcher(
    currentPrice: Double,
    previousPrice: Double,
    currency: CurrencyView
) {
    var showPercentageState by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            showPercentageState = !showPercentageState
        }
    }

    AnimatedContent(
        targetState = showPercentageState,
        transitionSpec = {
            (slideInVertically { height -> height } + fadeIn())
                .togetherWith(slideOutVertically { height -> -height } + fadeOut())
        },
        label = "PercentageVarianceAnimation"
    ) { showPercentage ->
        if (currentPrice == 0.0 || previousPrice == 0.0) return@AnimatedContent

        if (showPercentage) {
            val percentage = (currentPrice - previousPrice) / previousPrice * 100
            TrendDisplay(value = percentage, text = "%.2f %%".format(percentage), showPercentage = true, currency = currency)
        } else {
            val variance = currentPrice - previousPrice
            TrendDisplay(value = variance, showPercentage = false, currency = currency)
        }
    }
}

@Preview
@Composable
private fun PercentageVarianceSwitcherPreview() {
    OneWalletTheme {
        PercentageVarianceSwitcher(
            currentPrice = 100.0,
            previousPrice = 95.0,
            currency = CurrencyView.get(EUR)
        )
    }
}