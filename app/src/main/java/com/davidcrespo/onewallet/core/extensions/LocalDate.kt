package com.davidcrespo.onewallet.core.extensions

import java.time.LocalDate

fun LocalDate.isYesterday(): Boolean =
    this == LocalDate.now().minusDays(1)
