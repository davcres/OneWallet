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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWIconButton
import java.time.LocalTime

@Composable
fun Header(
    currency: Currency,
    onCurrencyChange: () -> Unit,
    navigateToHistorical: () -> Unit,
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

            Text(
                text = stringResource(R.string.portfolio_summary),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        AnimatedContent(
            targetState = currency == Currency.USD,
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn())
                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
            },
            label = "currency"
        ) { showUSD ->
            OWIconButton(
                imageVector = if (showUSD) Icons.Filled.AttachMoney else Icons.Filled.Euro,
                onClick = onCurrencyChange,
                contentDescription = stringResource(R.string.currency_selector_cd)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        OWIconButton(
            imageVector = Icons.Filled.AutoGraph,
            onClick = navigateToHistorical,
            contentDescription = stringResource(R.string.history_cd)
        )
    }
}

fun greetingByTime(): Int {
    val hour = LocalTime.now().hour

    return when (hour) {
        in 6..11 -> R.string.greeting_morning
        in 12..19 -> R.string.greeting_afternoon
        else -> R.string.greeting_evening
    }
}