package com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWCurrencyPrice

@Composable
fun TrendDisplay(value: Double, text: String, showPercentage: Boolean, currency: Currency) {
    Row {
        val (icon, color) = when {
            value > 0 -> Pair(
                Icons.AutoMirrored.Filled.TrendingUp,
                MaterialTheme.colorScheme.primary
            )
            value < 0 -> Pair(
                Icons.AutoMirrored.Filled.TrendingDown,
                MaterialTheme.colorScheme.error
            )
            else -> Pair(
                Icons.AutoMirrored.Filled.TrendingFlat,
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )

        if (showPercentage) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        } else {
            OWCurrencyPrice(
                price = value,
                currency = currency,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                textColor = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}