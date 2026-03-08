package com.davidcrespo.onewallet.presentation.widget.designsystem.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.widget.utils.formatPrice
import kotlin.math.roundToInt

@Composable
fun TotalBalance(balance: Double, currency: CurrencyView) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = LocalContext.current.getString(R.string.total_balance),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 16.sp
            )
        )

        Text(
            text = if (balance >= 1000000)
                "${balance.roundToInt()}€"
            else
                formatPrice(balance, currency, false),
            style = TextStyle(
                color = GlanceTheme.colors.onPrimaryContainer,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
    }
}
