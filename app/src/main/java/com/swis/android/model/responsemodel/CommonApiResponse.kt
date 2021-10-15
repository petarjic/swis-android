package com.swis.android.model.responsemodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CommonApiResponse():Parcelable {

    @SerializedName("responseCode")
    var responseCode: String? = null

    @SerializedName("responseMessage")
    var responseMessage: String? = null

    constructor(parcel: Parcel) : this() {
        responseCode = parcel.readString()
        responseMessage = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommonApiResponse> {
        override fun createFromParcel(parcel: Parcel): CommonApiResponse {
            return CommonApiResponse(parcel)
        }

        override fun newArray(size: Int): Array<CommonApiResponse?> {
            return arrayOfNulls(size)
        }
    }


}