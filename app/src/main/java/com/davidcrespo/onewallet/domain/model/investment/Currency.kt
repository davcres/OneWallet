package com.davidcrespo.onewallet.domain.model.investment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Euro
import androidx.compose.ui.graphics.vector.ImageVector

enum class Currency(val text: String, val symbol: String, val icon: ImageVector) {
    USD("USD", "$", Icons.Filled.AttachMoney),
    EUR("EUR", "€", Icons.Filled.Euro);

    companion object {
        fun from(value: String?, default: Currency = USD): Currency {
            return entries.firstOrNull { it.name == value }
                ?: default
        }
    }
}