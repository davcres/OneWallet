package com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.updateInvestment.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.owDropdownSelector.DropdownItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

object Categories {

    @Composable
    fun getPredefinedCategories(): ImmutableList<DropdownItem> {
        val categories = InvestmentCategory.ALL_PREDEFINED
        val names = categories.map { category ->
            category.nameRes?.let { stringResource(it) }
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
