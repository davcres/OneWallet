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
import com.davidcrespo.onewallet.core.composables.OWInvestmentItem
import com.davidcrespo.onewallet.core.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.domain.model.investment.Investment

@Composable
fun CardWithComicBubble(
    item: Investment,
    section: SectionType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var show by remember { mutableStateOf(false) }
    var anchor by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Box(modifier) {
        OWInvestmentItem(
            item = item,
            section = section,
            onClick = {
                show = !show
                onClick()
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
