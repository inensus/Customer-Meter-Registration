package com.inensus.android.view.fragment

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.inensus.android.BuildConfig
import com.inensus.android.InensusApplication
import com.inensus.android.R
import com.inensus.android.model.Customer
import com.inensus.android.viewmodel.CustomerViewModel
import com.inensus.android.viewmodel.ViewModelFactory
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import java.net.UnknownHostException

private var isAlertVisible: Boolean = false

abstract class BaseFragment : Fragment() {
    lateinit var mCustomerViewModel: CustomerViewModel
    private lateinit var mViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModelFactory = ViewModelFactory(activity?.application as InensusApplication)
        mCustomerViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CustomerViewModel::class.java)
    }

    fun showErrorMessage(throwable: Throwable?, customer: Customer? = null, listener: DialogClickListener? = null) {
        if (context != null) {
            var errorMessage = ""

            if (customer != null) {
                errorMessage = "Customer Information:\n- Name Surname: " + customer.name + " " + customer.surname + "\n- Serial Number: " + customer.serialNumber + "\n\n"
            }

            try {
                if (throwable is UnknownHostException) {
                    errorMessage = "Server Url is not valid. Please configure and restart the Application."
                } else {
                    val error = (throwable as HttpException).response()
                    val json = JSONObject(error?.errorBody()?.string())
                    val errorMessages = JSONObject(json.getString("data")).getJSONObject("message")
                    val keys = errorMessages.keys()

                    errorMessage += "Error Details:\n"

                    while (keys.hasNext()) {
                        val key = keys.next() as String

                        if (errorMessages.get(key) is JSONArray) {
                            errorMessage = errorMessage + "- " + (errorMessages.get(key) as JSONArray).get(0) + "\n"
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = if (BuildConfig.DEBUG && e.message != null) e.message!! else "Internal Server Error"
            }

            val alert = AlertDialog.Builder(context!!)
                    .setTitle(getString(R.string.error_message_title))
                    .setMessage(errorMessage)
                    .setPositiveButton("OK") { _, _ ->
                        listener?.onDialogClick()
                    }
                    .setOnDismissListener {
                        isAlertVisible = false
                    }
                    .setCancelable(false)
                    .create()

            if (!isAlertVisible) {
                isAlertVisible = true
                alert.show()
            }
        }
    }

    interface DialogClickListener {
        fun onDialogClick()
    }
}