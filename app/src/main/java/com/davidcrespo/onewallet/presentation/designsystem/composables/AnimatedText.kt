package com.davidcrespo.onewallet.presentation.designsystem.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit

@Composable
fun AnimatedText(
    text: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier,
) {
    var oldText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(text) {
        oldText = text
    }
    Row(modifier = modifier) {
        for(i in text.indices) {
            val oldChar = oldText.getOrNull(i)
            val newChar = text[i]
            val char = if(oldChar == newChar) {
                oldText[i]
            } else {
                text[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }, label = ""
            ) { char ->
                Text(
                    text = char.toString(),
                    color = color,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    softWrap = false
                )
            }
        }
    }
}

@Preview
@Composable
private fun AnimatedTextPreview() {
    /*AnimatedText(
        text = if (state.mock != null) "Hello" else "Bye",
        color = secondary,
        fontSize = 20.sp,
        fontFamily = R.font.font_bold,
        modifier = Modifier.padding(top = 8.dp),
    )*/
}