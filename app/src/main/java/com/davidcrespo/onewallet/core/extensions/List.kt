package com.davidcrespo.onewallet.core.extensions

fun <T> List<T>.groupByInitial(keySelector: (T) -> String): List<Pair<Char, List<T>>> {
        return this.groupBy { keySelector(it).trim().first().uppercaseChar() }.toList()
    }