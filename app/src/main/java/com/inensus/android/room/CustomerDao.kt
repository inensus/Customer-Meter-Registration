package com.inensus.android.room

import androidx.room.*
import com.inensus.android.model.Customer
import io.reactivex.Flowable

@Dao
interface CustomerDao {
    @Insert
    fun insertCustomer(customer: Customer)

    @Update
    fun updateCustomer(customer: Customer)

    @Delete
    fun deleteCustomer(customer: Customer)

    @Query("DELETE from Customers")
    fun deleteAll()

    @Query("SELECT * from Customers ORDER BY name ASC")
    fun getCustomerList(): Flowable<List<Customer>>
}