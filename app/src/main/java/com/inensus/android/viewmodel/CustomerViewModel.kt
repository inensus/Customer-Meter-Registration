package com.inensus.android.viewmodel

import androidx.lifecycle.ViewModel
import com.inensus.android.model.*
import com.inensus.android.repository.CustomerRepository
import io.reactivex.Observable

class CustomerViewModel internal constructor(private val repository: CustomerRepository) : ViewModel() {

    fun getCustomers(): Observable<List<Customer>>? {
        return repository.getCustomers()
    }

    fun addCustomerToDb(customer: Customer) {
        repository.addCustomerToDb(customer)
    }

    fun deleteCustomerInDb(customer: Customer) {
        repository.deleteCustomerInDb(customer)
    }

    fun addCustomer(customer: Customer): Observable<Customer> {
        return repository.addCustomer(customer)
    }

    fun getManufacturers(): Observable<List<Manufacturer>>? {
        return repository.getManufacturers()
    }

    fun getMeterTypes(): Observable<List<MeterType>>? {
        return repository.getMeterTypes()
    }

    fun getTariffs(): Observable<List<Tariff>>? {
        return repository.getTariffs()
    }

    fun getCities(): Observable<List<City>>? {
        return repository.getCities()
    }
}