package com.inensus.android.util

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.view.View
import android.view.inputmethod.InputMethodManager

class Utils {
    companion object {

        fun checkConnectivity(context: Context) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

            val activeNetwork: NetworkInfo? = connectivityManager?.activeNetworkInfo

            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
                SharedPreferencesWrapper.getInstance().networkState = 1
            } else {
                SharedPreferencesWrapper.getInstance().networkState = 0
            }

            val builder = NetworkRequest.Builder()

            connectivityManager!!.registerNetworkCallback(
                    builder.build(),
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            SharedPreferencesWrapper.getInstance().networkState = 1
                        }

                        override fun onLost(network: Network) {
                            SharedPreferencesWrapper.getInstance().networkState = 0
                        }
                    }
            )
        }

        fun hideKeyBoard(view: View?) {
            if (view != null) {
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        fun convertDpToPixel(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }
    }
}