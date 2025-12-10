package com.davidcrespo.onewallet.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.usecase.GetPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.GetQuoteUseCase
import com.davidcrespo.onewallet.domain.usecase.GetSymbolsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PriceViewModel(
    private val getPriceUseCase: GetPriceUseCase,
    private val getSymbolsUseCase: GetSymbolsUseCase,
    private val getQuoteUseCase: GetQuoteUseCase,
) : ViewModel() {

    private val _priceState = MutableStateFlow("Loading...")
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

    private val _symbolsState = MutableStateFlow<List<StockInfo>>(emptyList())
    val symbolsState = _symbolsState.asStateFlow()

    fun getSymbols(exchange: String) {
        viewModelScope.launch {
            val result = getSymbolsUseCase(exchange)
            result.onSuccess {
                _symbolsState.value = it
            }.onFailure {
                _symbolsState.value = emptyList()
            }
        }
    }

    private val _quoteState = MutableStateFlow("Loading...")
    val quoteState = _quoteState.asStateFlow()

    fun getQuote(symbol: String) {
        viewModelScope.launch {
            val result = getQuoteUseCase(symbol)
            result.onSuccess {
                _quoteState.value = "$${it.currentPrice}"
            }.onFailure {
                _quoteState.value = "Error: ${it.message}"
            }
        }
    }
}
