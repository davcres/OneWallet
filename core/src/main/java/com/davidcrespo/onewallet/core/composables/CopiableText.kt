package com.davidcrespo.onewallet.core.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role

@Composable
fun CopiableText(
    text: String,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null
) {
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbarText = stringResource(R.string.copiable_text_snackbar)
    var copied by remember { mutableStateOf(false) }
    val copyLabel = stringResource(R.string.copiable_text_cd)

    LaunchedEffect(copied) {
        if (copied) {
            delay(900)
            copied = false
        }
    }

    Row(
        modifier = modifier
            .clearAndSetSemantics {
                role = Role.Button
                contentDescription = "$text. $copyLabel"
            }
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    clipboard.setText(AnnotatedString(text))
                    copied = true
                    scope.launch { snackbarHostState.showSnackbar(snackbarText) }
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = color,
            style = style,
            textAlign = textAlign
        )

        Spacer(modifier = Modifier.width(8.dp))

        Crossfade(targetState = copied, label = "copy_icon_crossfade") { isCopied ->
            Icon(
                imageVector = if (isCopied) Icons.Filled.CheckCircle else Icons.Rounded.ContentCopy,
                contentDescription = null,
                tint = if (isCopied) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }
    }
}
