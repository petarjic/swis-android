package com.swis.android.model.requestmodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserLoginRequestModel():Parcelable {

    @SerializedName("email")
    var email: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("device_type")
    var device_type: String? = null

    @SerializedName("device_token")
    var device_token: String? = null

    @SerializedName("device_id")
    var device_id: String? = null

    constructor(parcel: Parcel) : this() {
        email = parcel.readString()
        password = parcel.readString()
        device_type = parcel.readString()
        device_token = parcel.readString()
        device_id = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(device_type)
        parcel.writeString(device_token)
        parcel.writeString(device_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserLoginRequestModel> {
        override fun createFromParcel(parcel: Parcel): UserLoginRequestModel {
            return UserLoginRequestModel(parcel)
        }

        override fun newArray(size: Int): Array<UserLoginRequestModel?> {
            return arrayOfNulls(size)
        }
    }

}
