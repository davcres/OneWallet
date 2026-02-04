package com.davidcrespo.onewallet.presentation.portfolio.positions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.modifiers.animations.bounceClick
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWBalance
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.designsystem.theme.cardGlowBrush

@Composable
fun TotalBalance(
    currency: Currency,
    totalBalance: Double,
    previousBalance: Double,
    changeBalanceVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    isBalanceVisible: Boolean,
    shouldAnimate: Boolean
) {
    Card(
        modifier = modifier.bounceClick(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = cardGlowBrush(),
                    shape = RoundedCornerShape(32.dp)
                )
                .fillMaxWidth()
        ) {
            OWBalance(
                currency = currency,
                balance = totalBalance,
                previousBalance = previousBalance,
                isBalanceVisible = isBalanceVisible,
                isExpanded = isExpanded,
                section = SectionType.PORTFOLIO,
                shouldAnimate = shouldAnimate
            )

            IconButton(
                onClick = { changeBalanceVisibility() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isBalanceVisible) stringResource(R.string.hide_balance_cd) else stringResource(R.string.show_balance_cd),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview
@Composable
private fun TotalBalancePreview() {
    OneWalletTheme {
        TotalBalance(
            currency = Currency.EUR,
            totalBalance = 110.0,
            previousBalance = 100.0,
            changeBalanceVisibility = {},
            isBalanceVisible = true,
            shouldAnimate = true
        )
    }
}