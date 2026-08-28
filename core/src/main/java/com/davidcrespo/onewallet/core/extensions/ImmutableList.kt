package com.davidcrespo.onewallet.core.extensions

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

fun <T> ImmutableList<T>?.orEmpty(): ImmutableList<T> = this ?: persistentListOf()
