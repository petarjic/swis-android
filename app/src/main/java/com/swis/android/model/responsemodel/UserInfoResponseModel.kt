package com.swis.android.model.responsemodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserInfoResponseModel():Parcelable {

    @SerializedName("responseCode")
    var responseCode: String? = null

    @SerializedName("responseMessage")
    var responseMessage: String? = null

    @SerializedName("nextPage")
    var nextPage: String? = null

    @SerializedName("recommendedUser")
    var recommendedUser: ArrayList<UserInfo>? = null

    constructor(parcel: Parcel) : this() {
        responseCode = parcel.readString()
        responseMessage = parcel.readString()
        nextPage = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
        parcel.writeString(nextPage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfoResponseModel> {
        override fun createFromParcel(parcel: Parcel): UserInfoResponseModel {
            return UserInfoResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<UserInfoResponseModel?> {
            return arrayOfNulls(size)
        }
    }


}
