package com.davidcrespo.onewallet.core.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.composables.modifiers.animations.animatedBorder
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    contentDescription: String?,
    hasClearIcon: Boolean,
    cornerRadius: Dp,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(cornerRadius)
    val bgColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    val borderProgress by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "border-progress"
    )

    val iconSize = 22.dp
    val animatedIconSize by animateDpAsState(
        targetValue = if (isFocused) {
            iconSize + 4.dp
        } else {
            iconSize
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseIn
        ),
        label = "icon-size"
    )
    val animatedIconTint by animateColorAsState(
        targetValue = if (isFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseIn
        ),
        label = "icon-color"
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(bgColor)
            .animatedBorder(
                progress = borderProgress,
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 1.dp,
                cornerRadius = cornerRadius
            )
            .padding(horizontal = 16.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = animatedIconTint,
                        modifier = Modifier.size(animatedIconSize)
                    )

                    Spacer(Modifier.width(12.dp))
                }

                Box(Modifier.weight(1f)) {
                    if (!isFocused && value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    innerTextField()
                }

                Spacer(Modifier.width(12.dp))

                if (value.isNotEmpty() && hasClearIcon) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(animatedIconSize)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.clear_text_cd),
                            tint = animatedIconTint
                        )
                    }
                } else if (trailingIcon != null) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = animatedIconTint,
                        modifier = Modifier.size(animatedIconSize)
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun TextFieldPreview() {
    OneWalletTheme {
        TextField(
            value = "",
            onValueChange = {},
            leadingIcon = Icons.Default.Search,
            placeholder = "Search",
            contentDescription = "Search field",
            hasClearIcon = true,
            cornerRadius = 16.dp
        )
    }
}
