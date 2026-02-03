package com.davidcrespo.onewallet.domain.model.investment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Euro
import androidx.compose.ui.graphics.vector.ImageVector

enum class Currency(val symbol: String, val icon: ImageVector) {
    USD("$", Icons.Filled.AttachMoney),
    EUR("€", Icons.Filled.Euro)
}