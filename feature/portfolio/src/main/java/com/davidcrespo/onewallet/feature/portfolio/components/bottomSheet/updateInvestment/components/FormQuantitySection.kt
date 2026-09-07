package com.davidcrespo.onewallet.feature.portfolio.components.bottomSheet.updateInvestment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.composables.TextField
import com.davidcrespo.onewallet.core.extensions.toSpanishFormatNumber
import com.davidcrespo.onewallet.domain.model.investment.isManual
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.core.models.InvestmentView

@Composable
fun FormQuantitySection(
    investment: InvestmentView,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = if (investment.type.isMarket()) stringResource(R.string.new_quantity_market) else stringResource(
                R.string.new_quantity_manual
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantity,
            onValueChange = { input ->
                val normalized = input.toSpanishFormatNumber()
                if (normalized.all { it.isDigit() || it == ',' } && normalized.count { it == ',' } <= 1) {
                    onQuantityChange(normalized)
                }
            },
            leadingIcon = if (investment.type.isMarket()) Icons.Outlined.PieChartOutline else investment.originalCurrency.icon,
            placeholder = placeholder,
            contentDescription = stringResource(R.string.asset_market_quantity_cd),
            cornerRadius = 16.dp,
            hasClearIcon = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        if (investment.type.isManual()) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.new_quantity_manual_info, investment.originalCurrency.symbol),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
