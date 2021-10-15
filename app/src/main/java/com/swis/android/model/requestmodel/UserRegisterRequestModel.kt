/*
package com.swis.android.model.requestmodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserRegisterRequestModel() :Parcelable {



    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        username = parcel.readString()
        email = parcel.readString()
        password = parcel.readString()
        device_type = parcel.readString()
        device_token = parcel.readString()
        device_id = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(device_type)
        parcel.writeString(device_token)
        parcel.writeString(device_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserRegisterRequestModel> {
        override fun createFromParcel(parcel: Parcel): UserRegisterRequestModel {
            return UserRegisterRequestModel(parcel)
        }

        override fun newArray(size: Int): Array<UserRegisterRequestModel?> {
            return arrayOfNulls(size)
        }
    }
}*/
