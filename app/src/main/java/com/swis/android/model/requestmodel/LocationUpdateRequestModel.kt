package com.swis.android.model.requestmodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class LocationUpdateRequestModel() : Parcelable {

    @SerializedName("address")
    var address: String? = null
    @SerializedName("latitude")
    var latitude: Double? = null
    @SerializedName("longitude")
    var longitude: Double? = null

    constructor(parcel: Parcel) : this() {
        address = parcel.readString()
        latitude = parcel.readValue(Double::class.java.classLoader) as? Double
        longitude = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationUpdateRequestModel> {
        override fun createFromParcel(parcel: Parcel): LocationUpdateRequestModel {
            return LocationUpdateRequestModel(parcel)
        }

        override fun newArray(size: Int): Array<LocationUpdateRequestModel?> {
            return arrayOfNulls(size)
        }
    }


}