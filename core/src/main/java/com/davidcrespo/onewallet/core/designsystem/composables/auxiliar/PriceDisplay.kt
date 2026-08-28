package com.davidcrespo.onewallet.core.designsystem.composables.auxiliar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.designsystem.composables.OWCurrencyPrice
import com.davidcrespo.onewallet.core.models.CurrencyView

@Composable
fun PriceDisplay(value: Double, currency: CurrencyView, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.padding(4.dp)
    ) {
        OWCurrencyPrice(
            price = value,
            currency = currency,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
