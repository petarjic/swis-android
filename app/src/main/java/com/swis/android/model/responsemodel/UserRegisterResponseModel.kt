package com.swis.android.model.responsemodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserRegisterResponseModel():Parcelable {

    @SerializedName("responseCode")
    var responseCode: String? = null

    @SerializedName("responseMessage")
    var responseMessage: String? = null

    @SerializedName("token_type")
    var token_type: String? = null

    @SerializedName("user")
    var user: UserInfo? = null

    constructor(parcel: Parcel) : this() {
        responseCode = parcel.readString()
        responseMessage = parcel.readString()
        token_type = parcel.readString()
        user = parcel.readParcelable(UserInfo::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
        parcel.writeString(token_type)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserRegisterResponseModel> {
        override fun createFromParcel(parcel: Parcel): UserRegisterResponseModel {
            return UserRegisterResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<UserRegisterResponseModel?> {
            return arrayOfNulls(size)
        }
    }


}