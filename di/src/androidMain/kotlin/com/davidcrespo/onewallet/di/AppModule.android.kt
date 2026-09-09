package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.feature.widget.di.widgetFeatureModule
import org.koin.core.module.Module

val appModules: List<Module> = sharedAppModules + listOf(widgetFeatureModule)
