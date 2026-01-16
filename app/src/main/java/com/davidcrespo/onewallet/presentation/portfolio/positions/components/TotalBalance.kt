package com.davidcrespo.onewallet.presentation.portfolio.positions.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidcrespo.onewallet.core.composables.animations.bounceClick
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWCurrencyPrice
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWShakeListener
import com.davidcrespo.onewallet.presentation.designsystem.theme.cardGlowBrush
import kotlinx.coroutines.delay

@Composable
fun TotalBalance(
    currency: Currency,
    totalBalance: Double,
    previousBalance: Double,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true
) {
    val richPhrases = remember {
        listOf(
            "Demasiado dinero para mostrarlo sin gafas de sol.",
            "A Hacienda le gusta esto.",
            "Eres rico, ¿para qué quieres saber si has ganado 5€ más?",
            "Con esto te dejan entrar al Época sin hacer cola.",
            "Seguro que te puedes permitir hacerle un bizum al humilde desarrollador de la app.",
            "¿Seguro que no has añadido ceros de más? Te dejo un momento para reflexionar.",
            "Deja algo para los demás Javito.",
        )
    }
    var currentRichPhrase by remember { mutableStateOf(richPhrases.random()) }
    var isBalanceVisible by rememberSaveable { mutableStateOf(true) }

    OWShakeListener(
        onShake = { isBalanceVisible = !isBalanceVisible }
    )

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
    val blurRadius by animateDpAsState(
        targetValue = if (isBalanceVisible) 0.dp else 16.dp,
        animationSpec = tween(
            durationMillis = 400
        ),
        label = "blur"
    )

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
                modifier = Modifier.blur(blurRadius)
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
                                text = "Balance Total",
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
                                    val percentage =
                                        if (totalBalance == 0.0 || previousBalance == 0.0) {
                                            0.0
                                        } else {
                                            (totalBalance - previousBalance) / previousBalance * 100
                                        }
                                    val (percentageIcon, percentageColor, prefix) = when {
                                        percentage > 0 -> Triple(
                                            Icons.AutoMirrored.Filled.TrendingUp,
                                            MaterialTheme.colorScheme.primary,
                                            "+"
                                        )

                                        percentage < 0 -> Triple(
                                            Icons.AutoMirrored.Filled.TrendingDown,
                                            MaterialTheme.colorScheme.error,
                                            ""
                                        )

                                        else -> Triple(
                                            Icons.AutoMirrored.Filled.TrendingFlat,
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                            ""
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = percentageIcon,
                                                contentDescription = "Percentage Icon",
                                                tint = percentageColor
                                            )

                                            Spacer(modifier = Modifier.width(8.dp))

                                            OWCurrencyPrice(
                                                price = totalBalance - previousBalance,
                                                currency = currency,
                                                fontSize = 18.sp,
                                                textColor = percentageColor
                                            )
                                        }
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
                onClick = { isBalanceVisible = !isBalanceVisible },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isBalanceVisible) "Ocultar balance" else "Mostrar balance",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}