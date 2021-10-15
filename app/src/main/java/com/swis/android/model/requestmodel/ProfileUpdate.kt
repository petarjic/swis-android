package com.swis.android.model.requestmodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ProfileUpdate():Parcelable {

    @SerializedName("name")
    var name: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("bio")
    var bio: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        dob = parcel.readString()
        gender = parcel.readString()
        bio = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(dob)
        parcel.writeString(gender)
        parcel.writeString(bio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileUpdate> {
        override fun createFromParcel(parcel: Parcel): ProfileUpdate {
            return ProfileUpdate(parcel)
        }

        override fun newArray(size: Int): Array<ProfileUpdate?> {
            return arrayOfNulls(size)
        }
    }

}
