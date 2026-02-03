package com.davidcrespo.onewallet.presentation.designsystem.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.AnimatedCounter
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme

@Composable
fun OWCurrencyPrice(
    price: Double,
    currency: Currency,
    fontSize: TextUnit,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean = true
) {
    val usdWidth by animateDpAsState(targetValue = if (currency == Currency.USD) fontSize.value.dp/2 else 0.dp, label = "usdWidth")
    val eurWidth by animateDpAsState(targetValue = if (currency == Currency.EUR) fontSize.value.dp/2 else 0.dp, label = "eurWidth")
    val usdSpacer by animateDpAsState(targetValue = if (currency == Currency.USD) 4.dp else 0.dp, label = "usdSpacer")
    val eurSpacer by animateDpAsState(targetValue = if (currency == Currency.EUR) 4.dp else 0.dp, label = "eurSpacer")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = currency == Currency.USD,
            enter = fadeIn(tween(150)) + slideInHorizontally { -it },
            exit = fadeOut(tween(150)) + slideOutHorizontally { -it },
            modifier = Modifier.width(usdWidth)
        ) {
            Text(
                "$",
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.End
            )
        }

        Spacer(Modifier.width(usdSpacer))

        AnimatedCounter(
            targetValue = price,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = textColor,
            shouldAnimate = shouldAnimate
        )

        Spacer(Modifier.width(eurSpacer))

        androidx.compose.animation.AnimatedVisibility(
            visible = currency == Currency.EUR,
            enter = fadeIn(tween(150)) + slideInHorizontally { it },
            exit = fadeOut(tween(150)) + slideOutHorizontally { it },
            modifier = Modifier.width(eurWidth)
        ) {
            Text(
                "€",
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview
@Composable
private fun OWCurrencyPricePreview() {
    OneWalletTheme {
        OWCurrencyPrice(
            price = 100.0,
            currency = Currency.EUR,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
