package com.davidcrespo.onewallet.core.designsystem.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.davidcrespo.onewallet.core.R

@Composable
fun OWLoader(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.loading_cd)
    CircularProgressIndicator(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize()
            .semantics {
                contentDescription = loadingLabel
            },
        color = MaterialTheme.colorScheme.primary
    )
}
