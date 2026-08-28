package com.davidcrespo.onewallet.feature.portfolio.components.bottomSheet.updateInvestment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.designsystem.composables.OWCurrencyPrice
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.TrendDisplay
import com.davidcrespo.onewallet.core.models.CurrencyView

@Composable
fun FormSummarySection(
    newPrice: Double,
    variance: Double,
    currency: CurrencyView,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.estimated_new_value),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OWCurrencyPrice(
            price = newPrice,
            currency = currency,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val backgroundColor = when {
            variance > 0 -> MaterialTheme.colorScheme.primaryContainer
            variance < 0 -> MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = backgroundColor
        ) {
            TrendDisplay(
                value = variance,
                showPercentage = false,
                currency = currency,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
