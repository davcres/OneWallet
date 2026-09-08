package com.davidcrespo.onewallet.core.extensions

import java.util.Locale

actual fun Double.formatTwoDecimals(): String = "%.2f".format(Locale.getDefault(), this)
