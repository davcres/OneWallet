package com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.updateInvestment.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.core.composables.modifiers.animations.shakeClickEffect
import com.davidcrespo.onewallet.core.extensions.applyIf

@Composable
fun FormActionButtons(
    onClose: () -> Unit,
    onUpdate: () -> Unit,
    isValidQuantity: Boolean,
    isValidThreshold: Boolean,
    isValidCategory: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Button(
            text = stringResource(R.string.cancel_action),
            contentDescription = stringResource(R.string.cancel_edit_investment_cd),
            style = ButtonStyle.SECONDARY,
            onClick = onClose,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            text = stringResource(R.string.update_quantity_action),
            contentDescription = stringResource(R.string.update_quantity_action),
            style = ButtonStyle.PRIMARY,
            onClick = onUpdate,
            modifier = Modifier
                .weight(1f)
                .applyIf(!isValidQuantity || !isValidThreshold || !isValidCategory) {
                    shakeClickEffect()
                }
        )
    }
}
