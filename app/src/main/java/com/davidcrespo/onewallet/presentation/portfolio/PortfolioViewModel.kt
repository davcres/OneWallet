package com.davidcrespo.onewallet.presentation.portfolio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.domain.model.investment.isManual
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.appRoot.GetThemeUseCase
import com.davidcrespo.onewallet.domain.usecase.appRoot.SetThemeUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddInvestmentToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetInvestmentPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SaveMonthlyPortfolioUseCase
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.models.toDomain
import com.davidcrespo.onewallet.presentation.models.toUI
import com.davidcrespo.onewallet.presentation.portfolio.allocation.models.ItemsByTypeView
import com.davidcrespo.onewallet.presentation.widget.WidgetsRefreshWorker
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate

private const val SUBSCRIPTION_DURATION = 5_000L

class PortfolioViewModel(
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val getPortfolioItemsUseCase: GetPortfolioItemsUseCase,
    private val getInvestmentPriceUseCase: GetInvestmentPriceUseCase,
    private val saveMonthlyPortfolioUseCase: SaveMonthlyPortfolioUseCase,
    private val addInvestmentToPortfolioUseCase: AddInvestmentToPortfolioUseCase,
    private val removePortfolioItemUseCase: RemovePortfolioItemUseCase,
    private val financialRepository: FinancialRepository,
    private val currencyConverter: CurrencyConverter,
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> =
        combine(
            _uiState,
            getThemeUseCase().distinctUntilChanged()
        ) { state, themeMode ->
            state.copy(themeMode = themeMode)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_DURATION),
            initialValue = _uiState.value
        )

    fun handleIntent(intent: PortfolioIntent) {
        when (intent) {
            is PortfolioIntent.UpdateBalance -> setTotalBalance()
            is PortfolioIntent.ChangeCurrency -> changeCurrency()
            is PortfolioIntent.ToggleTheme -> toggleTheme(intent.themeMode)

            is PortfolioIntent.EditQuantity -> _uiState.update { it.copy(editingItem = intent.item) }
            is PortfolioIntent.UpdateQuantity -> updateQuantity(intent.item, intent.quantity)
            is PortfolioIntent.RemoveItem -> removeItem(intent.item)
            is PortfolioIntent.ShowDeleteDialog -> _uiState.update { it.copy(deletingItem = intent.item) }

            is PortfolioIntent.AddFundItem -> addFundItem(intent.name, intent.quantity)
            is PortfolioIntent.ShowFundDialog -> _uiState.update { it.copy(isFundDialogVisible = true) }
            is PortfolioIntent.DismissFundDialog -> _uiState.update { it.copy(isFundDialogVisible = false) }

            is PortfolioIntent.AddEtfItem -> addEtfItem(intent.name, intent.quantity)
            is PortfolioIntent.ShowEtfDialog -> _uiState.update { it.copy(isEtfDialogVisible = true) }
            is PortfolioIntent.DismissEtfDialog -> _uiState.update { it.copy(isEtfDialogVisible = false) }

            is PortfolioIntent.AddBankItem -> addBankItem(intent.name, intent.quantity, intent.currency)
            is PortfolioIntent.ShowBankDialog -> _uiState.update { it.copy(isBankDialogVisible = true) }
            is PortfolioIntent.DismissBankDialog -> _uiState.update { it.copy(isBankDialogVisible = false) }

            is PortfolioIntent.AddOtherItem -> addOtherItem(intent.name, intent.quantity, intent.currency)
            is PortfolioIntent.ShowOtherDialog -> _uiState.update { it.copy(isOtherDialogVisible = true) }
            is PortfolioIntent.DismissOtherDialog -> _uiState.update { it.copy(isOtherDialogVisible = false) }

            is PortfolioIntent.SetError -> _uiState.update { it.copy(error = intent.error) }
            is PortfolioIntent.ClearError -> _uiState.update { it.copy(error = null) }

            is PortfolioIntent.NavigateToMarket -> {}

            is PortfolioIntent.GetItemsByType -> getItemsByType()
            is PortfolioIntent.SelectInvestmentType -> _uiState.update { it.copy(typeDetail = intent.type) }
            is PortfolioIntent.DismissInvestmentType -> _uiState.update { it.copy(typeDetail = null) }
        }
    }

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getSelectedCurrency()
            getPortfolioItems()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun getPortfolioItems() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        getPortfolioItemsUseCase()
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .collectLatest { domainItems ->
                val baseItems = domainItems
                    .map { it.toUI() }
                    .distinctBy { it.symbol }
                    .toImmutableList()

                val priced = fetchPricesForItems(baseItems)

                // Sort once, based on the freshly-priced list
                val sorted = priced.sortedByDescending { it.quantity * it.displayPrice }.toImmutableList()

                updateWidgets()

                _uiState.update {
                    it.copy(
                        portfolioItems = sorted,
                        isLoading = false
                    )
                }
            }
    }

    private suspend fun fetchPricesForItems(items: ImmutableList<InvestmentView>): List<InvestmentView> {
        // Take a stable snapshot
        val state = _uiState.value
        val selectedCurrency = state.selectedCurrency
        val alreadyPriced = state.symbolsWithPrice.toSet()
        val existingBySymbol = state.portfolioItems.associateBy { it.symbol }

        val (manualItems, marketItems) = items
            .distinctBy { it.symbol }
            .partition { it.type.isManual() }
            .let { (a, b) -> a.toImmutableList() to b.toImmutableList() }

        val updatedMarketItems = supervisorScope {
            marketItems.map { item ->
                async {
                    val symbol = item.symbol

                    // Reuse if already priced
                    if (symbol in alreadyPriced) {
                        return@async existingBySymbol[symbol]?.copy(
                            quantity = item.quantity
                        ) ?: item
                    }

                    getInvestmentPriceUseCase(
                        symbol = symbol,
                        type = item.type,
                        name = item.name,
                        selectedCurrency = state.selectedCurrency.toDomain(),
                        investmentCurrency = item.originalCurrency.toDomain()
                    ).fold(
                        onSuccess = { api ->
                            val currency = item.originalCurrency.takeIf { it.code != UNKNOWN } ?: api.currency.toUI()
                            val rate = getCurrencyRateUseCase(
                                from = currency.code,
                                to = selectedCurrency.code
                            ).fold(
                                onSuccess = { it },
                                onFailure = { 1.0 }
                            )

                            val priceConverted = currencyConverter.convert(
                                amount = api.price,
                                from = currency.code,
                                to = selectedCurrency.code,
                                rate = rate
                            )

                            val previousPriceConverted = currencyConverter.convert(
                                amount = api.previousPrice,
                                from = currency.code,
                                to = selectedCurrency.code,
                                rate = rate
                            )

                            item.copy(
                                originalCurrency = currency,
                                originalPrice = api.price,
                                originalPreviousPrice = api.previousPrice,
                                displayPrice = priceConverted,
                                displayPreviousPrice = previousPriceConverted
                            )
                        },
                        onFailure = { error ->
                            error.printStackTrace()
                            item
                        }
                    )
                }
            }.awaitAll()
        }

        val updatedManualItems = manualItems.map { manualItem ->
            val rate = getCurrencyRateUseCase(
                from = manualItem.originalCurrency.code,
                to = selectedCurrency.code
            ).fold(
                onSuccess = { it },
                onFailure = { 1.0 }
            )

            val priceConverted = currencyConverter.convert(
                amount = manualItem.originalPrice,
                from = manualItem.originalCurrency.code,
                to = selectedCurrency.code,
                rate = rate
            )

            val previousPriceConverted = currencyConverter.convert(
                amount = manualItem.originalPreviousPrice,
                from = manualItem.originalCurrency.code,
                to = selectedCurrency.code,
                rate = rate
            )

            manualItem.copy(
                displayPrice = priceConverted,
                displayPreviousPrice = previousPriceConverted
            )
        }

        val finalList = (updatedManualItems + updatedMarketItems).distinctBy { it.symbol }.toImmutableList()
        val newlyPricedSymbols = updatedMarketItems.map { it.symbol }.toSet()
        val newSymbolsWithPrice = (alreadyPriced + newlyPricedSymbols).toImmutableList()

        _uiState.update {
            it.copy(
                portfolioItems = finalList,
                symbolsWithPrice = newSymbolsWithPrice
            )
        }

        // Save only if we priced something new
        if (newlyPricedSymbols.any { it !in alreadyPriced }) {
            savePortfolio(finalList)
        }

        return finalList
    }

    private fun setTotalBalance() {
        val totalBalance = _uiState.value.portfolioItems.sumOf {
            it.quantity * it.displayPrice
        }
        val previousBalance = _uiState.value.portfolioItems.sumOf {
            if (it.type.isMarket()) {
                it.quantity * it.displayPreviousPrice
            } else {
                it.quantity * it.displayPrice
            }
        }

        _uiState.update {
            it.copy(
                totalBalance = totalBalance,
                previousBalance = previousBalance
            )
        }
    }

    private suspend fun savePortfolio(itemsView: List<InvestmentView>) {
        val items = itemsView.map { it.toDomain() }
        saveMonthlyPortfolioUseCase(items)
    }

    private fun updateWidgets() {
        viewModelScope.launch {
            WidgetsRefreshWorker.enqueueNow(context)
        }
    }

    private fun addFundItem(isin: String, quantity: Double) {
        viewModelScope.launch {
            getInvestmentPriceUseCase(isin, InvestmentType.FUND)
                .onSuccess { investment ->
                    val now = LocalDate.now()
                    val year = now.year
                    val month = now.monthValue

                    val fund = investment.copy(
                        quantity = quantity,
                        year = year,
                        month = month
                    )

                    addInvestmentToPortfolioUseCase(fund)
                    _uiState.update { it.copy(isFundDialogVisible = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(error = "Desafortunadamente no hemos podido obtener el fondo.\nPrueba con otro ISIN.") }
                }
        }
    }

    private fun addEtfItem(isin: String, quantity: Double) {
        viewModelScope.launch {
            getInvestmentPriceUseCase(
                symbol = isin,
                type = InvestmentType.ETF,
                selectedCurrency = uiState.value.selectedCurrency.toDomain()
            )
                .onSuccess { investment ->
                    val now = LocalDate.now()
                    val year = now.year
                    val month = now.monthValue

                    val etf = investment.copy(
                        quantity = quantity,
                        year = year,
                        month = month
                    )

                    addInvestmentToPortfolioUseCase(etf)
                    _uiState.update { it.copy(isEtfDialogVisible = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(error = "Desafortunadamente no hemos podido obtener el ETF.\nPrueba con otro ISIN.") }
                }
        }
    }

    private fun addBankItem(name: String, quantity: Double, currency: CurrencyView) {
        viewModelScope.launch {
            val now = LocalDate.now()
            val year = now.year
            val month = now.monthValue

            val bank = InvestmentView(
                symbol = name,
                name = name,
                quantity = quantity,
                originalPrice = 1.0,
                originalPreviousPrice = 0.0,
                originalCurrency = currency,
                type = InvestmentType.BANK,
                year = year,
                month = month,
                displayPrice = 0.0,
                displayPreviousPrice = 0.0,
                changePercent = 0.0
            )

            addInvestmentToPortfolioUseCase(bank.toDomain())
            _uiState.update { it.copy(isBankDialogVisible = false) }
        }
    }

    private fun addOtherItem(name: String, quantity: Double, currency: CurrencyView) {
        viewModelScope.launch {
            val now = LocalDate.now()
            val year = now.year
            val month = now.monthValue

            val other = InvestmentView(
                symbol = name,
                name = name,
                quantity = quantity,
                originalPrice = 1.0,
                originalPreviousPrice = 0.0,
                originalCurrency = currency,
                type = InvestmentType.OTHER,
                year = year,
                month = month,
                displayPrice = 0.0,
                displayPreviousPrice = 0.0,
                changePercent = 0.0
            )

            addInvestmentToPortfolioUseCase(other.toDomain())
            _uiState.update { it.copy(isOtherDialogVisible = false) }
        }
    }

    private fun removeItem(item: InvestmentView) {
        viewModelScope.launch {
            removePortfolioItemUseCase(item.toDomain())
            _uiState.update { it.copy(deletingItem = null) }
        }
    }

    private fun updateQuantity(item: InvestmentView, quantity: Double) {
        viewModelScope.launch {
            val now = LocalDate.now()
            val year = now.year
            val month = now.monthValue

            val itemUpdated = item.copy(
                quantity = quantity,
                year = year,
                month = month
            )
            addInvestmentToPortfolioUseCase(itemUpdated.toDomain())

            _uiState.update { it.copy(editingItem = null) }
        }
    }

    private fun getSelectedCurrency() {
        val selectedCurrency = financialRepository.getSelectedCurrency()
        _uiState.update {
            it.copy(
                selectedCurrency = selectedCurrency.toUI()
            )
        }
    }

    private fun changeCurrency() {
        viewModelScope.launch {
            val state = _uiState.value
            val selectedCurrency = state.selectedCurrency
            val portfolioItems = state.portfolioItems
            val newSelectedCurrency = if (selectedCurrency.code == EUR)
                CurrencyView.get(USD)
            else
                CurrencyView.get(EUR)

            financialRepository.setSelectedCurrency(newSelectedCurrency.toDomain())

            val portfolioItemsConverted = portfolioItems.map { item ->
                val rate = getCurrencyRateUseCase(
                    from = item.originalCurrency.code,
                    to = newSelectedCurrency.code
                ).fold(
                    onSuccess = { it },
                    onFailure = { 1.0 }
                )

                val priceConverted = currencyConverter.convert(
                    amount = item.originalPrice,
                    from = item.originalCurrency.code,
                    to = newSelectedCurrency.code,
                    rate = rate
                )

                val previousPriceConverted = currencyConverter.convert(
                    amount = item.originalPreviousPrice,
                    from = item.originalCurrency.code,
                    to = newSelectedCurrency.code,
                    rate = rate
                )

                item.copy(
                    displayPrice = priceConverted,
                    displayPreviousPrice = previousPriceConverted
                )
            }

            _uiState.update {
                it.copy(
                    selectedCurrency = newSelectedCurrency,
                    portfolioItems = portfolioItemsConverted.toImmutableList()
                )
            }

            updateWidgets()
        }
    }

    private fun getItemsByType() {
        viewModelScope.launch {
            val items = _uiState.value.portfolioItems

            val groups: ImmutableList<ItemsByTypeView> =
                items
                    .groupBy { it.type }
                    .map { (type, list) ->
                        val total = list.sumOf { it.quantity * it.displayPrice }
                        ItemsByTypeView(type, list.toImmutableList(), total)
                    }
                    .sortedByDescending { it.totalValue }
                    .toImmutableList()

            _uiState.update { it.copy(portfolioItemsByType = groups) }
        }
    }

    private fun toggleTheme(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeUseCase(mode)
        }
    }
}