package com.shslab.shskhata

import android.app.Application
import androidx.room.Room
import com.shslab.shskhata.data.db.AppDatabase
import com.shslab.shskhata.data.db.CustomerDao
import com.shslab.shskhata.data.db.TxnDao
import com.shslab.shskhata.data.repository.CustomerRepository
import com.shslab.shskhata.data.repository.TxnRepository

class SHSKhataApp : Application() {

    companion object {
        lateinit var instance: SHSKhataApp
            private set
    }


    lateinit var appDatabase: AppDatabase
        private set

    lateinit var customerRepository: CustomerRepository
        private set

    lateinit var txnRepository: TxnRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, AppDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

        val customerDao: CustomerDao = appDatabase.customerDao()
        val txnDao: TxnDao = appDatabase.txnDao()

        customerRepository = CustomerRepository(customerDao, txnDao)
        txnRepository = TxnRepository(txnDao)
    }
}
