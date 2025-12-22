package com.davidcrespo.onewallet.presentation.portfolio.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableFab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAddStockClick: () -> Unit,
    onAddCryptoClick: () -> Unit,
    onAddFundClick: () -> Unit,
    onAddBankClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 135f else 0f, 
        label = "fab_rotation"
    )

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier.padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                FabItem(
                    icon = Icons.Default.ShowChart,
                    text = "Acciones",
                    onClick = {
                        onAddStockClick()
                        onExpandedChange(false)
                    }
                )

                FabItem(
                    icon = Icons.Default.CurrencyBitcoin,
                    text = "Cripto",
                    onClick = {
                        onAddCryptoClick()
                        onExpandedChange(false)
                    }
                )

                FabItem(
                    icon = Icons.Default.PieChart,
                    text = "Fondo / ETF",
                    onClick = {
                        onAddFundClick()
                        onExpandedChange(false)
                    }
                )
                
                FabItem(
                    icon = Icons.Default.AccountBalance,
                    text = "Banco / Deposito",
                    onClick = {
                        onAddBankClick()
                        onExpandedChange(false)
                    }
                )
            }
        }
        
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun FabItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 2.dp,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        SmallFloatingActionButton(
            onClick = onClick
        ) {
            Icon(icon, contentDescription = text)
        }
    }
}
