package com.davidcrespo.onewallet.presentation.portfolio.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import java.time.LocalTime

@Composable
fun Header(
    text: String,
    currency: CurrencyView,
    themeMode: ThemeMode,
    onCurrencyChange: () -> Unit,
    onChangeUIMode: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(greetingByTime()),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedContent(
                targetState = text,
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn())
                        .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                },
                label = "section"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        AnimatedContent(
            targetState = currency.code == EUR,
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn())
                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
            },
            label = "currency"
        ) { showEUR ->
            OWIconButton(
                imageVector = if (showEUR) Icons.Filled.Euro else Icons.Filled.AttachMoney,
                onClick = onCurrencyChange,
                contentDescription = stringResource(R.string.currency_selector_cd)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        AnimatedContent(
            targetState = themeMode,
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn())
                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
            },
            label = "uiMode"
        ) { currentTheme ->
            val nextTheme = when (currentTheme) {
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.SYSTEM
                ThemeMode.SYSTEM -> ThemeMode.LIGHT
            }
            OWIconButton(
                imageVector = when(currentTheme) {
                    ThemeMode.LIGHT -> Icons.Filled.LightMode
                    ThemeMode.DARK -> Icons.Filled.DarkMode
                    ThemeMode.SYSTEM -> Icons.Filled.BrightnessAuto
                },
                onClick = { onChangeUIMode(nextTheme) },
                contentDescription = stringResource(R.string.ui_mode_cd, currentTheme.name, nextTheme.name)
            )
        }
    }
}

@Preview
@Composable
private fun HeaderPreview() {
    OneWalletTheme {
        Header(
            text = "Resumen de tu portfolio",
            currency = CurrencyView.get(EUR),
            themeMode = ThemeMode.LIGHT,
            onCurrencyChange = {},
            onChangeUIMode = {}
        )
    }
}

private fun greetingByTime(): Int {
    val hour = LocalTime.now().hour

    return when (hour) {
        in 6..11 -> R.string.greeting_morning
        in 12..19 -> R.string.greeting_afternoon
        else -> R.string.greeting_evening
    }
}