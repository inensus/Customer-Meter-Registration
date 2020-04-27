package com.inensus.android.network.http

import com.inensus.android.BuildConfig
import com.inensus.android.util.SharedPreferencesWrapper
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Client implementation for Square's Retrofit.
 */
class RetrofitClient
/**
 * Retrofit Connection Builder
 */
private constructor() {
    /**
     * Returns Retrofit instance
     *
     * @return Retrofit instance
     */
    val retrofit: Retrofit

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(logging)
                .build()

        retrofit = try {
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(SharedPreferencesWrapper.getInstance().baseUrl!!)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build()
        } catch (e: Exception) {
            SharedPreferencesWrapper.getInstance().baseUrl = SharedPreferencesWrapper.DEFAULT_BASE_URL

            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(SharedPreferencesWrapper.DEFAULT_BASE_URL)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build()
        }
    }

    companion object {

        private const val DEFAULT_CONNECT_TIMEOUT: Long = 30000
        private const val DEFAULT_READ_TIMEOUT: Long = 30000

        /**
         * Returns Our RetrofitClient instance
         *
         * @return Our RetrofitClient instance
         */
        val instance = RetrofitClient()
    }
}