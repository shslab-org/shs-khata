package com.shslab.shskhata.data.repository

import com.shslab.shskhata.data.db.Customer
import com.shslab.shskhata.data.db.CustomerDao
import com.shslab.shskhata.data.db.TxnDao
import kotlinx.coroutines.flow.Flow

class CustomerRepository(
    private val customerDao: CustomerDao,
    private val txnDao: TxnDao
) {
    fun observeAll(): Flow<List<Customer>> = customerDao.observeAll()

    fun observeSearched(query: String): Flow<List<Customer>> =
        customerDao.observeSearched(query.trim())

    fun observeById(customerId: Long): Flow<Customer?> = customerDao.observeById(customerId)

    suspend fun getById(customerId: Long): Customer? = customerDao.getById(customerId)

    suspend fun add(name: String, phone: String, note: String): Long =
        customerDao.insert(
            Customer(name = name.trim(), phone = phone.trim(), note = note.trim())
        )

    suspend fun update(customerId: Long, name: String, phone: String, note: String) {
        customerDao.update(customerId, name.trim(), phone.trim(), note.trim())
    }

    suspend fun delete(customerId: Long) {
        // Delete all transactions first (FK cleanup), then the customer row.
        txnDao.deleteAllForCustomer(customerId)
        val customer = customerDao.getById(customerId)
        if (customer != null) customerDao.delete(customer)
    }
}
