package com.davidcrespo.onewallet.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.data.datasource.FinancialDataSource
import com.davidcrespo.onewallet.data.repository.FinancialRepositoryImpl
import com.davidcrespo.onewallet.domain.usecase.GetPriceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PriceViewModel : ViewModel() {

    private val _priceState = MutableStateFlow<String>("Loading...")
    val priceState = _priceState.asStateFlow()

    // This should be injected with a DI framework like Hilt
    private val getPriceUseCase = GetPriceUseCase(FinancialRepositoryImpl(FinancialDataSource()))


    fun getPrice(symbol: String) {
        viewModelScope.launch {
            val result = getPriceUseCase(symbol)
            result.onSuccess {
                _priceState.value = "$${it.price}"
            }.onFailure {
                _priceState.value = "Error: ${it.message}"
            }
        }
    }
}
