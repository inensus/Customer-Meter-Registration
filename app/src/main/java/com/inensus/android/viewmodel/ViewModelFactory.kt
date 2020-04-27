package com.inensus.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inensus.android.InensusApplication
import com.inensus.android.network.api.CustomerApi
import com.inensus.android.network.http.RetrofitClient
import com.inensus.android.repository.CustomerRepository
import com.inensus.android.room.InensusDatabase

/**
 * Factory for ViewModels
 */
class ViewModelFactory(private val application: InensusApplication) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            return CustomerViewModel(CustomerRepository(
                    application,
                    InensusDatabase.getDatabase(application)!!.customerDao(),
                    RetrofitClient.instance.retrofit.create(CustomerApi::class.java))) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
