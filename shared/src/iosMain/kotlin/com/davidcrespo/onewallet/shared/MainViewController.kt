package com.davidcrespo.onewallet.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.MainViewModel
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.di.initKoinIos
import com.davidcrespo.onewallet.shared.navigation.IosNavigation
import org.koin.compose.viewmodel.koinViewModel
import platform.UIKit.UIViewController

fun initOneWallet() {
    initKoinIos()
}

fun MainViewController(): UIViewController = ComposeUIViewController {
    val viewModel: MainViewModel = koinViewModel()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    OneWalletTheme(themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            IosNavigation()
        }
    }
}
