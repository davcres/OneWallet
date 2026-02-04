package com.davidcrespo.onewallet.presentation.designsystem.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.modifiers.privacySensitive
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.TrendDisplay
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import kotlinx.coroutines.delay

@Composable
fun OWBalance(
    currency: Currency,
    balance: Double,
    previousBalance: Double,
    isBalanceVisible: Boolean,
    isExpanded: Boolean,
    section: SectionType,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean
) {
    val richPhrases = stringArrayResource(R.array.rich_phrases).toList()
    var currentRichPhrase by remember { mutableStateOf(richPhrases.random()) }

    LaunchedEffect(balance) {
        if (balance > 1_000_000) {
            while (true) {
                currentRichPhrase = richPhrases.random()
                delay(10000)
            }
        }
    }

    val sizes = when (section) {
        SectionType.ALLOCATION -> BalanceSize(
            balanceTitle = MaterialTheme.typography.bodyMedium,
            richPhrase = 14.sp,
            balanceValueExpanded = 30f,
            balanceValueContracted = 18f,
            varianceStyle = MaterialTheme.typography.bodyMedium,
            percentageStyle = MaterialTheme.typography.bodySmall
        )
        else -> BalanceSize(
            balanceTitle = MaterialTheme.typography.titleMedium,
            richPhrase = 24.sp,
            balanceValueExpanded = 45f,
            balanceValueContracted = 32f,
            varianceStyle = MaterialTheme.typography.titleMedium,
            percentageStyle = MaterialTheme.typography.bodyMedium
        )
    }

    val verticalPadding by animateDpAsState(targetValue = if (isExpanded) 32.dp else 16.dp, label = "padding")
    val fontSize by animateFloatAsState(targetValue = if (isExpanded) sizes.balanceValueExpanded else sizes.balanceValueContracted, label = "fontSize")

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
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
                        style = sizes.balanceTitle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (balance > 1_000_000 && isExpanded) {
                AnimatedContent(
                    targetState = currentRichPhrase,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "RichPhraseTransition"
                ) { phrase ->
                    Text(
                        text = phrase,
                        fontSize = sizes.richPhrase,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        lineHeight = sizes.richPhrase,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            } else {
                OWCurrencyPrice(
                    price = balance,
                    currency = currency,
                    fontSize = fontSize.sp,
                    shouldAnimate = shouldAnimate
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
                            val variance = balance - previousBalance
                            val percentage =
                                if (balance == 0.0 || previousBalance == 0.0) {
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
                                    showPercentage = false,
                                    currency = currency,
                                    style = sizes.varianceStyle
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "($prefix%.2f %%)".format(percentage),
                                style = sizes.percentageStyle,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class BalanceSize(
    val balanceTitle: TextStyle,
    val richPhrase: TextUnit,
    val balanceValueExpanded: Float,
    val balanceValueContracted: Float,
    val varianceStyle: TextStyle,
    val percentageStyle: TextStyle
)

@Preview
@Composable
private fun OWBalancePreview() {
    OneWalletTheme {
        OWBalance(
            currency = Currency.EUR,
            balance = 110.0,
            previousBalance = 100.0,
            isBalanceVisible = true,
            isExpanded = true,
            section = SectionType.PORTFOLIO,
            shouldAnimate = true
        )
    }
}