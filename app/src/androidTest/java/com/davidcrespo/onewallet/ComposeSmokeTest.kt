package com.davidcrespo.onewallet

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoke test para comprobar que:
 * - el runner de instrumentation funciona
 * - el emulador arranca
 * - los Compose UI tests se ejecutan en CI
 */
@RunWith(AndroidJUnit4::class)
class ComposeSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun clickingButton_changesText() {
        composeRule.setContent {
            val clicked = remember { mutableStateOf(false) }

            Column {
                Text(if (clicked.value) "Clicked" else "Hello")
                Button(onClick = { clicked.value = true }) {
                    Text("Press")
                }
            }
        }

        composeRule.onNodeWithText("Hello").assertExists()
        composeRule.onNodeWithText("Press").performClick()
        composeRule.onNodeWithText("Clicked").assertExists()
    }
}
