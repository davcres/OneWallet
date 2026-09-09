package com.davidcrespo.onewallet.feature.portfolio.components.bottomSheet.updateInvestment.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.owDropdownSelector.DropdownItem
import com.davidcrespo.onewallet.core.models.nameResource
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

object Categories {

    @Composable
    fun getPredefinedCategories(): ImmutableList<DropdownItem> {
        val categories = InvestmentCategory.ALL_PREDEFINED
        val names = categories.map { category ->
            category.nameResource?.let { stringResource(it) }
        }

        return remember(names) {
            names.mapIndexed { index, name ->
                DropdownItem(
                    id = index,
                    name = name.orEmpty(),
                    tag = categories[index].id
                )
            }.toImmutableList()
        }
    }
}
