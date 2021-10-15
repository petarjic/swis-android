package com.swis.android.model.responsemodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class SendOtpApiResponse():Parcelable{

    @SerializedName("responseCode")
    var responseCode: String? = null

    @SerializedName("responseMessage")
    var responseMessage: String? = null

    @SerializedName("otp")
    var otp: String? = null

    constructor(parcel: Parcel) : this() {
        responseCode = parcel.readString()
        responseMessage = parcel.readString()
        otp = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
        parcel.writeString(otp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SendOtpApiResponse> {
        override fun createFromParcel(parcel: Parcel): SendOtpApiResponse {
            return SendOtpApiResponse(parcel)
        }

        override fun newArray(size: Int): Array<SendOtpApiResponse?> {
            return arrayOfNulls(size)
        }
    }
}
