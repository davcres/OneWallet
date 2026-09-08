package com.davidcrespo.onewallet.feature.portfolio.components.bottomSheet.updateInvestment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.owDropdownSelector.DropdownItem
import com.davidcrespo.onewallet.core.extensions.normalizeDouble
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.alert_threshold_placeholder
import com.davidcrespo.onewallet.core.generated.resources.error_alert_threshold_invalid
import com.davidcrespo.onewallet.core.generated.resources.error_category_empty
import com.davidcrespo.onewallet.core.generated.resources.error_quantity_empty
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.core.models.InvestmentView
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.feature.portfolio.util.rememberNotificationPermissionRequester
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateInvestmentForm(
    investment: InvestmentView,
    currency: CurrencyView,
    onClose: () -> Unit,
    onEditInvestment: (newQuantity: Double, alertThreshold: Double?, category: InvestmentCategory) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val initialQuantityValue = investment.quantity.toString()
    val initialQuantityPlaceholder = initialQuantityValue.takeIf { it.isNotEmpty() } ?: "0.0"
    var quantity by remember { mutableStateOf("") }

    val initialThresholdValue = investment.alertThreshold?.toString().orEmpty()
    val thresholdPlaceholder = initialThresholdValue.takeIf { it.isNotEmpty() }
        ?: stringResource(Res.string.alert_threshold_placeholder)
    var threshold by remember { mutableStateOf("") }

    val predefinedCategories = Categories.getPredefinedCategories()

    // Dynamic categories list state
    var categories by remember {
        val category = investment.category
        val currentCategoryId = category.id

        // Try to find by tag in predefined, otherwise it's custom
        val customItem = if (predefinedCategories.none { it.tag == currentCategoryId }) {
            // For custom, name and tag are the same
            DropdownItem(-1, currentCategoryId, currentCategoryId)
        } else null

        mutableStateOf(
            if (customItem != null) {
                (persistentListOf(customItem) + predefinedCategories).toImmutableList()
            } else {
                predefinedCategories
            }
        )
    }

    var selectedCategory by remember {
        mutableStateOf(
            categories.find { it.tag == investment.category.id }
        )
    }

    var notificationsEnabled by remember { mutableStateOf(investment.alertThreshold != null) }
    val newPrice = investment.displayPrice * quantity.normalizeDouble()

    val requestNotificationsPermissionIfNeeded = rememberNotificationPermissionRequester { isGranted ->
        notificationsEnabled = isGranted
    }

    val enableNotifications = {
        notificationsEnabled = true
        requestNotificationsPermissionIfNeeded()
    }

    Column(modifier = modifier) {
        FormQuantitySection(
            investment = investment,
            quantity = quantity,
            onQuantityChange = { quantity = it },
            placeholder = initialQuantityPlaceholder
        )

        Spacer(modifier = Modifier.height(24.dp))

        FormCategorySection(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { item ->
                if (categories.none { it.name == item.name }) {
                    categories = (persistentListOf(item) + categories).toImmutableList()
                }
                selectedCategory = item
            },
            onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )

        if (investment.type.isMarket()) {
            Spacer(modifier = Modifier.height(24.dp))

            FormNotificationSection(
                notificationsEnabled = notificationsEnabled,
                onNotificationsEnabledChange = { isEnabled ->
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    notificationsEnabled = isEnabled
                    if (isEnabled) requestNotificationsPermissionIfNeeded()
                },
                threshold = threshold,
                onThresholdChange = { threshold = it },
                thresholdPlaceholder = thresholdPlaceholder,
                onEnableNotificationsRequested = enableNotifications
            )
        }

        Spacer(Modifier.height(32.dp))

        FormSummarySection(
            newPrice = newPrice,
            variance = newPrice - (investment.displayPrice * investment.quantity),
            currency = currency
        )

        Spacer(Modifier.height(32.dp))

        val normalizedQuantity = quantity
            .takeIf { it.isNotBlank() }
            ?.normalizeDouble()
            ?: initialQuantityValue.normalizeDouble()

        val normalizedThreshold = threshold
            .takeIf { it.isNotBlank() }
            ?.normalizeDouble()
            ?: initialThresholdValue
                .takeIf { it.isNotBlank() }
                ?.normalizeDouble()

        val isValidQuantity = normalizedQuantity >= 0
        val isValidThreshold = !notificationsEnabled ||
                normalizedThreshold == null ||
                normalizedThreshold > 0
        val isValidCategory = !selectedCategory?.name.isNullOrEmpty()

        val errorInvalidQuantity = stringResource(Res.string.error_quantity_empty)
        val errorInvalidThreshold = stringResource(Res.string.error_alert_threshold_invalid)
        val errorInvalidCategory = stringResource(Res.string.error_category_empty)

        FormActionButtons(
            onClose = onClose,
            isValidQuantity = isValidQuantity,
            isValidThreshold = isValidThreshold,
            isValidCategory = isValidCategory,
            onUpdate = {
                when {
                    !isValidQuantity -> onError(errorInvalidQuantity)
                    !isValidThreshold -> onError(errorInvalidThreshold)
                    !isValidCategory -> onError(errorInvalidCategory)
                    else -> onEditInvestment(
                        normalizedQuantity,
                        if (notificationsEnabled) normalizedThreshold else null,
                        InvestmentCategory.fromName(selectedCategory?.tag)
                    )
                }
            }
        )
    }
}
