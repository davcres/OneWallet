package com.davidcrespo.onewallet.di

import org.koin.core.module.Module

val appModules: List<Module> = sharedAppModules

fun initKoinIos() = initKoin {}
