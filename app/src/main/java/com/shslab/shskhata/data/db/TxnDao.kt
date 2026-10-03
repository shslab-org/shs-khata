package com.shslab.shskhata.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TxnDao {

    @Query("SELECT * FROM txns WHERE customerId = :customerId ORDER BY createdAt DESC, id DESC")
    fun observeByCustomer(customerId: Long): Flow<List<Txn>>

    @Query("SELECT * FROM txns WHERE customerId = :customerId")
    suspend fun getByCustomer(customerId: Long): List<Txn>

    @Insert
    suspend fun insert(txn: Txn): Long

    @Delete
    suspend fun delete(txn: Txn)

    @Query("DELETE FROM txns WHERE customerId = :customerId")
    suspend fun deleteAllForCustomer(customerId: Long)
}
