package com.davidcrespo.onewallet.core.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Dot(size: Int, color: Color, modifier: Modifier = Modifier, onClick : () -> Unit = {}) {
    Canvas(modifier = modifier.size(size.dp).clickable { onClick() }) {
        drawCircle(color)
    }
}