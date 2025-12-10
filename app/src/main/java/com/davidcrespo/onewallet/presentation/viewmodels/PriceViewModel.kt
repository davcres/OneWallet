package com.davidcrespo.onewallet.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.usecase.GetPriceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PriceViewModel(private val getPriceUseCase: GetPriceUseCase) : ViewModel() {

    private val _priceState = MutableStateFlow<String>("Loading...")
    val priceState = _priceState.asStateFlow()

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

    private val _quoteState = MutableStateFlow<String>("Loading...")
    val quoteState = _quoteState.asStateFlow()

    fun getQuote(symbol: String) {
        viewModelScope.launch {
            val result = getPriceUseCase(symbol)
            result.onSuccess {
                _quoteState.value = "$${it.price}"
            }.onFailure {
                _quoteState.value = "Error: ${it.message}"
            }
        }
    }
}
