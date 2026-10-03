package com.shslab.shskhata.data.repository

import com.shslab.shskhata.data.db.Txn
import com.shslab.shskhata.data.db.TxnDao
import com.shslab.shskhata.data.db.TxnType
import kotlinx.coroutines.flow.Flow

class TxnRepository(
    private val txnDao: TxnDao
) {
    fun observeByCustomer(customerId: Long): Flow<List<Txn>> =
        txnDao.observeByCustomer(customerId)

    suspend fun add(customerId: Long, type: TxnType, amount: Double, note: String): Long =
        txnDao.insert(
            Txn(
                customerId = customerId,
                type = type,
                amount = amount,
                note = note.trim()
            )
        )

    suspend fun delete(txn: Txn) {
        txnDao.delete(txn)
    }
}
