package com.inensus.android.network.api

import com.inensus.android.model.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CustomerApi {

    @GET("people/all")
    fun getCustomerList(): Observable<BaseListResponse<Customer>>

    @GET("manufacturers")
    fun getManufacturers(): Observable<BaseListResponse<Manufacturer>>

    @GET("meters/types")
    fun getMeterTypes(): Observable<BaseListResponse<MeterType>>

    @GET("tariffs")
    fun getTariffs(): Observable<BaseListResponse<Tariff>>

    @GET("cities")
    fun getCities(): Observable<BaseListResponse<City>>

    @POST("androidApp")
    fun addCustomer(@Body customer: Customer): Observable<BaseResponse<Customer>>
}