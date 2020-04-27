package com.inensus.android.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inensus.android.model.City
import com.inensus.android.model.Manufacturer
import com.inensus.android.model.MeterType
import com.inensus.android.model.Tariff

/**
 * Keeps a reference to the SharedPreference
 * Acts as a Singleton class
 */
class SharedPreferencesWrapper() {

    private lateinit var mSharedPreferences: SharedPreferences
    private var mListener: Listener? = null

    constructor(context: Context) : this() {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun setListener(listener: Listener) {
        mListener = listener
    }

    var baseUrl: String?
        get() = mSharedPreferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL)
        set(url) {
            mSharedPreferences.edit().putString(KEY_BASE_URL, url).apply()
        }

    var networkState: Int?
        get() = mSharedPreferences.getInt(KEY_NETWORK_STATE, 1)
        set(state) {
            if (state != null) {
                mSharedPreferences.edit().putInt(KEY_NETWORK_STATE, state).apply()
                mListener?.onSharedPreferencesValueChange()
            }
        }

    var manufacturers: String
        get() = mSharedPreferences.getString(KEY_MANUFACTURERS, "")!!
        set(manufacturers) {
            mSharedPreferences.edit().putString(KEY_MANUFACTURERS, manufacturers).apply()
            mListener?.onSharedPreferencesValueChange()
        }

    fun getManufacturerList(): MutableList<Manufacturer> {
        val type = object : TypeToken<List<Manufacturer>>() {}.type

        if (manufacturers.isEmpty()) {
            return ArrayList<Manufacturer>().toMutableList()
        }

        return Gson().fromJson<List<Manufacturer>>(manufacturers, type).toMutableList()
    }

    var meterTypes: String
        get() = mSharedPreferences.getString(KEY_METER_TYPES, "")!!
        set(manufacturers) {
            mSharedPreferences.edit().putString(KEY_METER_TYPES, manufacturers).apply()
            mListener?.onSharedPreferencesValueChange()
        }

    fun getMeterTypeList(): MutableList<MeterType> {
        val type = object : TypeToken<List<MeterType>>() {}.type

        if (meterTypes.isEmpty()) {
            return ArrayList<MeterType>().toMutableList()
        }

        return Gson().fromJson<List<MeterType>>(meterTypes, type).toMutableList()
    }

    var tariffs: String
        get() = mSharedPreferences.getString(KEY_TARIFFS, "")!!
        set(manufacturers) {
            mSharedPreferences.edit().putString(KEY_TARIFFS, manufacturers).apply()
            mListener?.onSharedPreferencesValueChange()
        }

    fun getTariffList(): MutableList<Tariff> {
        val type = object : TypeToken<List<Tariff>>() {}.type

        if (tariffs.isEmpty()) {
            return ArrayList<Tariff>().toMutableList()
        }

        return Gson().fromJson<List<Tariff>>(tariffs, type).toMutableList()
    }

    var cities: String
        get() = mSharedPreferences.getString(KEY_CITIES, "")!!
        set(cities) {
            mSharedPreferences.edit().putString(KEY_CITIES, cities).apply()
            mListener?.onSharedPreferencesValueChange()
        }

    fun getCityList(): MutableList<City> {
        val type = object : TypeToken<List<City>>() {}.type

        if (tariffs.isEmpty()) {
            return ArrayList<City>().toMutableList()
        }

        return Gson().fromJson<List<City>>(cities, type).toMutableList()
    }

    companion object {
        const val DEFAULT_BASE_URL = "http://demo.micropowermanager.com/api/"

        private const val SHARED_PREFERENCES_NAME = "inensus"
        private const val KEY_BASE_URL = "baseUrl"
        private const val KEY_NETWORK_STATE = "networkState"
        private const val KEY_MANUFACTURERS = "manufacturers"
        private const val KEY_METER_TYPES = "meterTypes"
        private const val KEY_TARIFFS = "tariffs"
        private const val KEY_CITIES = "cities"

        @Volatile
        private var instance: SharedPreferencesWrapper? = null

        fun getInstance(context: Context? = null): SharedPreferencesWrapper =
                SharedPreferencesWrapper.instance ?: synchronized(this) {
                    SharedPreferencesWrapper.instance
                            ?: buildSharedPreferencesWrapper(context!!).also { SharedPreferencesWrapper.instance = it }
                }

        private fun buildSharedPreferencesWrapper(context: Context) =
                SharedPreferencesWrapper(context)
    }

    interface Listener {
        fun onSharedPreferencesValueChange()
    }
}
