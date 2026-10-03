package com.shslab.shskhata.ui.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shslab.shskhata.data.db.Customer
import com.shslab.shskhata.data.db.Txn
import com.shslab.shskhata.data.db.TxnType
import com.shslab.shskhata.data.repository.CustomerRepository
import com.shslab.shskhata.data.repository.TxnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LedgerUiState(
    val customer: Customer? = null,
    val txns: List<Txn> = emptyList(),
    val balance: Double = 0.0
)

class LedgerViewModel(
    private val customerRepository: CustomerRepository,
    private val txnRepository: TxnRepository,
    private val customerId: Long
) : ViewModel() {

    private val _txns = MutableStateFlow<List<Txn>>(emptyList())
    private val _customer = MutableStateFlow<Customer?>(null)

    val uiState: StateFlow<LedgerUiState> =
        kotlinx.coroutines.flow.combine(_customer, _txns) { c, txns ->
            LedgerUiState(
                customer = c,
                txns = txns,
                balance = txns.fold(0.0) { acc, t ->
                    acc + when (t.type) {
                        TxnType.GAVE -> t.amount
                        TxnType.GOT -> -t.amount
                    }
                }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = LedgerUiState()
        )

    init {
        viewModelScope.launch {
            customerRepository.observeById(customerId).collect { c ->
                _customer.value = c
            }
        }
        viewModelScope.launch {
            txnRepository.observeByCustomer(customerId).collect { txns ->
                _txns.value = txns
            }
        }
    }

    fun addTransaction(type: TxnType, amount: Double, note: String) {
        if (amount <= 0.0) return
        viewModelScope.launch {
            txnRepository.add(customerId, type, amount, note)
        }
    }

    fun deleteTransaction(txn: Txn) {
        viewModelScope.launch {
            txnRepository.delete(txn)
        }
    }

}
