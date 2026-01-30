package com.davidcrespo.onewallet.presentation.portfolio.positions.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.modifiers.animations.bounceClick
import com.davidcrespo.onewallet.core.composables.modifiers.privacySensitive
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWCurrencyPrice
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.TrendDisplay
import com.davidcrespo.onewallet.presentation.designsystem.theme.cardGlowBrush
import kotlinx.coroutines.delay

@Composable
fun TotalBalance(
    currency: Currency,
    totalBalance: Double,
    previousBalance: Double,
    changeBalanceVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    isBalanceVisible: Boolean,
) {
    val richPhrases = stringArrayResource(R.array.rich_phrases).toList()
    var currentRichPhrase by remember { mutableStateOf(richPhrases.random()) }

    LaunchedEffect(totalBalance) {
        if (totalBalance > 1_000_000) {
            while (true) {
                currentRichPhrase = richPhrases.random()
                delay(10000)
            }
        }
    }

    val verticalPadding by animateDpAsState(targetValue = if (isExpanded) 32.dp else 16.dp, label = "padding")
    val fontSize by animateFloatAsState(targetValue = if (isExpanded) 45f else 32f, label = "fontSize")

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
            Box(
                modifier = Modifier
                    .privacySensitive(hideContent = !isBalanceVisible)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = verticalPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.total_balance),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (totalBalance > 1_000_000 && isExpanded) {
                        AnimatedContent(
                            targetState = currentRichPhrase,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "RichPhraseTransition"
                        ) { phrase ->
                            Text(
                                text = phrase,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center,
                                lineHeight = 30.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    } else {
                        OWCurrencyPrice(
                            price = totalBalance,
                            currency = currency,
                            fontSize = fontSize.sp
                        )

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val variance = totalBalance - previousBalance
                                    val percentage =
                                        if (totalBalance == 0.0 || previousBalance == 0.0) {
                                            0.0
                                        } else {
                                            variance / previousBalance * 100
                                        }

                                    val (backgroundColor, prefix) = when {
                                        percentage > 0 -> Pair(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            "+"
                                        )
                                        percentage < 0 -> Pair(
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.25f),
                                            ""
                                        )
                                        else -> Pair(
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                                            ""
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = backgroundColor
                                    ) {
                                        TrendDisplay(
                                            value = variance,
                                            text = "%.2f %%".format(variance),
                                            showPercentage = false,
                                            currency = currency,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "($prefix%.2f %%)".format(percentage),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

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