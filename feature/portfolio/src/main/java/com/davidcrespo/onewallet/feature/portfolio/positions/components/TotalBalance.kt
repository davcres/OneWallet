package com.davidcrespo.onewallet.feature.portfolio.positions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.composables.modifiers.animations.bounceClick
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.core.designsystem.composables.OWBalance
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.core.designsystem.theme.gradients
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.feature.portfolio.models.PortfolioCoachmarks
import com.pseudoankit.coachmark.LocalCoachMarkScope
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.pseudoankit.coachmark.shape.Arrow
import com.pseudoankit.coachmark.shape.Balloon

@Composable
fun TotalBalance(
    currency: CurrencyView,
    totalBalance: Double,
    previousBalance: Double,
    changeBalanceVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    isBalanceVisible: Boolean,
    shouldAnimate: Boolean
) {
    Card(
        modifier = modifier
            .bounceClick()
            .enableCoachMark(
                key = PortfolioCoachmarks.TOTAL_BALANCE,
                toolTipPlacement = ToolTipPlacement.Bottom,
                tooltip = {
                    Balloon(
                        arrow = Arrow.Top(),
                        modifier = Modifier.widthIn(max = 200.dp),
                        bgColor = MaterialTheme.colorScheme.primaryContainer,
                        cornerRadius = 16.dp,
                        padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = stringResource(PortfolioCoachmarks.TOTAL_BALANCE.tooltip),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                coachMarkScope = LocalCoachMarkScope.current
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = MaterialTheme.gradients.cardGlow,
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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
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
}

@Preview
@Composable
private fun TotalBalancePreview() {
    OneWalletTheme {
        TotalBalance(
            currency = CurrencyView.get(EUR),
            totalBalance = 110.0,
            previousBalance = 100.0,
            changeBalanceVisibility = {},
            isBalanceVisible = true,
            shouldAnimate = true
        )
    }
}