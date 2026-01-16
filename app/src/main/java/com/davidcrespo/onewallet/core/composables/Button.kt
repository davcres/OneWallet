package com.davidcrespo.onewallet.core.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.animations.bounceClick
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme

@Composable
fun Button(
    text: String,
    contentDescription: String,
    style: ButtonStyle,
    onClick: () -> Unit,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val (containerColor, contentColor, borderColor) = when (style) {
        ButtonStyle.PRIMARY -> {
            Triple(
                first = when {
                    !enabled -> MaterialTheme.colorScheme.outline
                    isPressed -> MaterialTheme.colorScheme.inversePrimary
                    else -> MaterialTheme.colorScheme.primary
                },
                second = when {
                    !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onPrimary
                },
                third = null
            )
        }
        ButtonStyle.SECONDARY -> {
            Triple(
                first = Color.Transparent,
                second = when {
                    !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
                    isPressed -> MaterialTheme.colorScheme.inversePrimary
                    else -> MaterialTheme.colorScheme.primary
                },
                third = when {
                    !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
                    isPressed -> MaterialTheme.colorScheme.inversePrimary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
        ButtonStyle.TERTIARY -> {
            Triple(
                first = Color.Transparent,
                second = when {
                    !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
                    isPressed -> MaterialTheme.colorScheme.inversePrimary
                    else -> MaterialTheme.colorScheme.primary
                },
                third = null
            )
        }
    }

    Surface(
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier
            .defaultMinSize(minWidth = 100.dp)
            .border(
                width = 1.dp,
                color = borderColor ?: Color.Transparent,
                shape = shape
            )
            .bounceClick()
            .clip(shape)
            .clickable(
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    radius = 999.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                onClick = onClick,
                onClickLabel = contentDescription,
                enabled = enabled
            )
            .semantics {
                role = Role.Button
                testTag = text
                this.contentDescription = contentDescription
                this.onClick(label = contentDescription) {
                    onClick()
                    true
                }
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = 16.dp)
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = contentColor
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        }
    }
}

data class ButtonPreviewParams(
    val buttonStyle: ButtonStyle,
    val leadingIcon: ImageVector?,
    val trailingIcon: ImageVector?,
    val enabled: Boolean
)

class ButtonPreviewParamsProvider : PreviewParameterProvider<ButtonPreviewParams> {
    override val values = getAllCombinations()

    private fun getAllCombinations(): Sequence<ButtonPreviewParams> {
        val samples = mutableListOf<ButtonPreviewParams>()

        ButtonStyle.entries.forEach { buttonStyle ->
            listOf(true, false).forEach { showLeadingIcon ->
                listOf(true, false).forEach { showTrailingIcon ->
                    listOf(true, false).forEach { enabled ->
                        samples.add(
                            ButtonPreviewParams(
                                buttonStyle = buttonStyle,
                                leadingIcon = if (showLeadingIcon) Icons.Default.Star else null,
                                trailingIcon = if (showTrailingIcon) Icons.Default.Star else null,
                                enabled = enabled
                            )
                        )
                    }
                }
            }
        }
        return samples.asSequence()
    }
}

@Preview
@Composable
fun ButtonPreview(
    @PreviewParameter(ButtonPreviewParamsProvider::class) buttonParams: ButtonPreviewParams
) {
    OneWalletTheme {
        Button(
            text = "Button",
            contentDescription = "Button",
            onClick = {},
            style = buttonParams.buttonStyle,
            leadingIcon = buttonParams.leadingIcon,
            trailingIcon = buttonParams.trailingIcon,
            enabled = buttonParams.enabled
        )
    }
}