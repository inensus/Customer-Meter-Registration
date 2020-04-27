package com.inensus.android.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class BaseListResponse<T>(@SerializedName("data") var data: List<T>)

data class BaseResponse<T>(@SerializedName("data") var data: T)

@Entity(tableName = "Customers")
data class Customer(@SerializedName("id") @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Int? = null,
                    @SerializedName("name") @field:ColumnInfo(name = "name") var name: String? = null,
                    @SerializedName("surname") @field:ColumnInfo(name = "surname") var surname: String? = null,
                    @SerializedName("phone") @field:ColumnInfo(name = "phone") var phone: String? = null,
                    @SerializedName("tariff_id") @field:ColumnInfo(name = "tariff") var tariff: Int? = null,
                    @SerializedName("city_id") @field:ColumnInfo(name = "city") var city: Int? = null,
                    @SerializedName("geo_points") @field:ColumnInfo(name = "geo_points") var geoPoints: String? = null,
                    @SerializedName("serial_number") @field:ColumnInfo(name = "serial_number") var serialNumber: String? = null,
                    @SerializedName("manufacturer") @field:ColumnInfo(name = "manufacturer") var manufacturer: Int? = null,
                    @SerializedName("meter_type") @field:ColumnInfo(name = "meter_type") var meterType: Int? = null,
                    @SerializedName("local") @field:ColumnInfo(name = "isLocal") var isLocal: Boolean = false)

data class Manufacturer(@SerializedName("id") var id: Int? = null,
                        @SerializedName("name") var name: String? = null) {
    override fun toString(): String {
        return name!!
    }
}

data class MeterType(@SerializedName("id") var id: Int? = null,
                     @SerializedName("online") var online: Int? = null,
                     @SerializedName("phase") var phase: Int? = null,
                     @SerializedName("max_current") var maxCurrent: Int? = null) {
    override fun toString(): String {
        return if (id!! < 0) "Meter Type" else maxCurrent.toString() + "A " + phase + "P" + " " + if (online == 1) "Online" else "Offline"
    }
}

data class Tariff(@SerializedName("id") var id: Int? = null,
                  @SerializedName("name") var name: String? = null,
                  @SerializedName("price") var price: Int? = null,
                  @SerializedName("currency") var currency: String? = null) {
    override fun toString(): String {
        return if ("Tariff" == name) "Tariff" else name + " (" + price?.div(100) + " " + currency + ")"
    }
}

data class City(@SerializedName("id") var id: Int? = null,
                  @SerializedName("name") var name: String? = null,
                  @SerializedName("country_id") var countryId: Int? = null) {
    override fun toString(): String {
        return name!!
    }
}
