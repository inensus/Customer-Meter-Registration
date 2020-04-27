package com.inensus.android.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.inensus.android.model.*
import com.inensus.android.network.api.CustomerApi
import com.inensus.android.room.CustomerDao
import com.inensus.android.util.SharedPreferencesWrapper
import com.inensus.android.util.Utils
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


class CustomerRepository internal constructor(private val context: Context, private val customerDao: CustomerDao, private val customerApi: CustomerApi) {

    @SuppressLint("CheckResult")
    fun getCustomers(): Observable<List<Customer>>? {
        return if (SharedPreferencesWrapper.getInstance().networkState == 1) {
            Observable.zip(
                    getCustomersFromDb(),
                    getCustomersFromApi(),
                    BiFunction<List<Customer>, List<Customer>, List<Customer>> { dbResult, apiResult ->
                        combineLists(dbResult, apiResult)
                    }
            )
        } else {
            getCustomersFromDb()
        }
    }

    private fun combineLists(dbList: List<Customer>, apiList: List<Customer>): List<Customer> {
        val combinedList = ArrayList<Customer>(dbList)
        combinedList.addAll(apiList)

        return combinedList
    }

    private fun getCustomersFromApi(): Observable<List<Customer>> {
        return customerApi.getCustomerList()
                .map { it -> it.data }
    }

    private fun getCustomersFromDb(): Observable<List<Customer>> {
        return customerDao.getCustomerList()
                .toObservable()
    }

    fun addCustomerToDb(customer: Customer) {
        AddCustomerAsyncTask(customerDao).execute(customer)
    }

    fun deleteCustomerInDb(customer: Customer) {
        DeleteCustomerAsyncTask(customerDao).execute(customer)
    }

    fun addCustomer(customer: Customer): Observable<Customer> {
        return customerApi.addCustomer(customer)
                .map { it.data }
    }

    fun getManufacturers(): Observable<List<Manufacturer>> {
        return customerApi.getManufacturers()
                .map { it.data }
    }

    fun getMeterTypes(): Observable<List<MeterType>> {
        return customerApi.getMeterTypes()
                .map { it.data }
    }

    fun getTariffs(): Observable<List<Tariff>> {
        return customerApi.getTariffs()
                .map { it.data }
    }

    fun getCities(): Observable<List<City>> {
        return customerApi.getCities()
                .map { it.data }
    }

    private class AddCustomerAsyncTask internal constructor(private val mAsyncTaskDao: CustomerDao) : AsyncTask<Customer, Void, Void>() {

        override fun doInBackground(vararg params: Customer): Void? {
            mAsyncTaskDao.insertCustomer(params[0])

            return null
        }
    }

    private class UpdateCustomerAsyncTask internal constructor(private val mAsyncTaskDao: CustomerDao) : AsyncTask<Customer, Void, Void>() {

        override fun doInBackground(vararg params: Customer): Void? {
            mAsyncTaskDao.updateCustomer(params[0])

            return null
        }
    }

    private class DeleteCustomerAsyncTask internal constructor(private val mAsyncTaskDao: CustomerDao) : AsyncTask<Customer, Void, Void>() {

        override fun doInBackground(vararg params: Customer): Void? {
            mAsyncTaskDao.deleteCustomer(params[0])

            return null
        }
    }
}