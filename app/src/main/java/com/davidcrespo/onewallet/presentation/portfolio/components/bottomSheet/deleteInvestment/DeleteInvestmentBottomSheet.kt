package com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.deleteInvestment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWIconButton
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.bottomSheet.SheetHandle
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteInvestmentBottomSheet(
    investment: InvestmentView,
    visible: Boolean,
    onDismiss: () -> Unit,
    onDelete: (InvestmentView) -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { SheetHandle() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        scrimColor = Color.Transparent, // Disable default scrim (darker content out of bottom sheet) to see light error
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            Header(
                investment = investment,
                onClose = {
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion { onDismiss() }
                }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.delete_investment_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    text = stringResource(R.string.cancel_action),
                    contentDescription = stringResource(R.string.cancel_action),
                    style = ButtonStyle.SECONDARY,
                    onClick = {
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion { onDismiss() }
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    text = stringResource(R.string.delete_action),
                    contentDescription = stringResource(R.string.delete_action),
                    style = ButtonStyle.PRIMARY,
                    onClick = {
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion { onDelete(investment) }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun Header(investment: InvestmentView, onClose: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Spacer(Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = investment.type.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = investment.symbol,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            if (investment.symbol != investment.name && investment.name.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = investment.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        OWIconButton(
            imageVector = Icons.Outlined.Close,
            onClick = onClose,
            contentDescription = stringResource(R.string.close_cd),
            modifier = Modifier
                .align(Alignment.TopEnd)
        )
    }
}
