package com.davidcrespo.onewallet.core.designsystem.composables.auxiliar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.designsystem.composables.OWCurrencyPrice
import com.davidcrespo.onewallet.core.models.CurrencyView

@Composable
fun TrendDisplay(
    value: Double,
    text: String = "",
    showPercentage: Boolean,
    currency: CurrencyView,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
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
            tint = color,
            modifier = Modifier.size(style.fontSize.value.dp + 6.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (showPercentage) {
            Text(
                text = text,
                style = style,
                fontWeight = FontWeight.Bold,
                color = color
            )
        } else {
            OWCurrencyPrice(
                price = value,
                currency = currency,
                fontSize = style.fontSize,
                textColor = color
            )
        }
    }
}
