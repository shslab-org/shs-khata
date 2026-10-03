package com.shslab.shskhata.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY name COLLATE NOCASE ASC")
    fun observeSearched(query: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun observeById(customerId: Long): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getById(customerId: Long): Customer?

    @Insert
    suspend fun insert(customer: Customer): Long

    @Query("UPDATE customers SET name = :name, phone = :phone, note = :note WHERE id = :customerId")
    suspend fun update(customerId: Long, name: String, phone: String, note: String)

    @Delete
    suspend fun delete(customer: Customer)
}
