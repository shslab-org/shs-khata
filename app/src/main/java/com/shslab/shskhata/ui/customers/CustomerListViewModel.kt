package com.shslab.shskhata.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shslab.shskhata.data.db.Customer
import com.shslab.shskhata.data.db.Txn
import com.shslab.shskhata.data.db.TxnType
import com.shslab.shskhata.data.repository.CustomerRepository
import com.shslab.shskhata.data.repository.TxnRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI state for the customer list screen.
 *
 * [balances] maps customerId -> running balance for that customer.
 * Balance rule: sum(GAVE) - sum(GOT). Positive = customer owes the shop (বাকি).
 */
data class CustomerListUiState(
    val customers: List<Customer> = emptyList(),
    val balances: Map<Long, Double> = emptyMap(),
    val totalDue: Double = 0.0
)

class CustomerListViewModel(
    private val customerRepository: CustomerRepository,
    private val txnRepository: TxnRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<CustomerListUiState> =
        _query.flatMapLatest { q ->
            if (q.isBlank()) {
                customerRepository.observeAll()
            } else {
                customerRepository.observeSearched(q)
            }
        }
            .flatMapLatest { customers ->
                if (customers.isEmpty()) {
                    flowOf(CustomerListUiState())
                } else {
                    // Build one Flow<Map<id, balance>> per customer, then fold-combine
                    // them into a single Flow<Map<Long, Double>>.
                    @OptIn(FlowPreview::class)
                    customers
                        .map { c ->
                            txnRepository.observeByCustomer(c.id)
                                .map { txns -> c.id to balanceOf(txns) }
                        }
                        .fold(flowOf(emptyMap<String, Double>())) { accFlow, nextFlow ->
                            accFlow.combine(nextFlow) { a, b ->
                                @Suppress("UNCHECKED_CAST")
                                (a as Map<Long, Double>).plus(b as Map<Long, Double>)
                            }
                        }
                        .distinctUntilChanged()
                        .map { balanceMap ->
                            CustomerListUiState(
                                customers = customers,
                                balances = balanceMap,
                                totalDue = customers.sumOf { c ->
                                    val b = balanceMap[c.id] ?: 0.0
                                    if (b > 0.0) b else 0.0
                                }
                            )
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CustomerListUiState()
            )

    fun onQueryChange(q: String) {
        _query.value = q
    }

    fun addCustomer(name: String, phone: String, note: String) {
        viewModelScope.launch {
            customerRepository.add(name, phone, note)
        }
    }

    fun updateCustomer(id: Long, name: String, phone: String, note: String) {
        viewModelScope.launch {
            customerRepository.update(id, name, phone, note)
        }
    }

    fun deleteCustomer(id: Long) {
        viewModelScope.launch {
            customerRepository.delete(id)
        }
    }

    /**
     * Compute running balance for a customer:
     *   balance = sum(GAVE) - sum(GOT)
     * Positive = customer owes the shop (বাকি / due).
     * Negative  = shop owes the customer (অগ্রিম / advance).
     */
    fun balanceOf(txns: List<Txn>): Double =
        txns.fold(0.0) { acc, t ->
            acc + when (t.type) {
                TxnType.GAVE -> t.amount
                TxnType.GOT -> -t.amount
            }
        }

    private fun Map<Long, Double>.plus(other: Map<Long, Double>): Map<Long, Double> =
        toMutableMap().also { it.putAll(other) }
}
