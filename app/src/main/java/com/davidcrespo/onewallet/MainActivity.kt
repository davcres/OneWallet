package com.davidcrespo.onewallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.davidcrespo.onewallet.presentation.screens.PriceScreen
import com.davidcrespo.onewallet.ui.theme.OneWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OneWalletTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PriceScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
