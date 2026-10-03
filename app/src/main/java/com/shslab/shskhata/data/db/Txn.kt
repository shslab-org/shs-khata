package com.shslab.shskhata.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "txns",
    indices = [Index(value = ["customerId"], name = "idx_txn_customer")]
)
data class Txn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val customerId: Long,
    val type: TxnType,
    val amount: Double,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TxnType { GAVE, GOT }
