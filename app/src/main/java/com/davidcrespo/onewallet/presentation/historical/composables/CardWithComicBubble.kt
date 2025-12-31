package com.davidcrespo.onewallet.presentation.historical.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType

@Composable
fun CardWithComicBubble(
    item: Investment,
    currency: Currency,
    section: SectionType,
    modifier: Modifier = Modifier,
    onClick: (Investment) -> Unit
) {
    var show by remember { mutableStateOf(false) }
    var anchor by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Box(modifier) {
        OWInvestmentItem(
            item = item,
            currency = currency,
            section = section,
            onClick = {
                show = !show
                onClick(item)
            },
            onGloballyPositioned = { anchor = it }
        )

        if (show && anchor != null) {
            ComicBubblePopup(
                anchor = anchor!!,
                onDismiss = { show = false }
            ) {
                Text(
                    text = "Unidades en cartera: ${item.quantity}\n" + "Precio por unidad: %.2f €".format(item.price),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
