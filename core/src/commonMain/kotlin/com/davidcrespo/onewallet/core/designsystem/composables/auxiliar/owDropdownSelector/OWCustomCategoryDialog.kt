package com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.owDropdownSelector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.accept_action
import com.davidcrespo.onewallet.core.generated.resources.cancel_action
import com.davidcrespo.onewallet.core.generated.resources.dropdown_custom_category_accept_cd
import com.davidcrespo.onewallet.core.generated.resources.dropdown_custom_category_cancel_cd
import com.davidcrespo.onewallet.core.generated.resources.dropdown_custom_category_placeholder
import com.davidcrespo.onewallet.core.generated.resources.dropdown_custom_category_title
import com.davidcrespo.onewallet.core.generated.resources.dropdown_select_category
import org.jetbrains.compose.resources.stringResource
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.TextField
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OWCustomCategoryDialog(
    customCategoryName: String,
    onCustomCategoryNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.dropdown_custom_category_title),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = customCategoryName,
                    onValueChange = onCustomCategoryNameChange,
                    placeholder = stringResource(Res.string.dropdown_custom_category_placeholder),
                    contentDescription = stringResource(Res.string.dropdown_select_category),
                    cornerRadius = 16.dp,
                    hasClearIcon = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        text = stringResource(Res.string.cancel_action),
                        contentDescription = stringResource(Res.string.dropdown_custom_category_cancel_cd),
                        style = ButtonStyle.SECONDARY,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        text = stringResource(Res.string.accept_action),
                        contentDescription = stringResource(Res.string.dropdown_custom_category_accept_cd),
                        style = ButtonStyle.PRIMARY,
                        enabled = customCategoryName.isNotBlank(),
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
