package com.tmc.concentrationgame.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by sammy on 2/17/2018.
 */

class FlickrPhoto : Parcelable {
    @SerializedName("id")
    var photoId: String


    @SerializedName("secret")
    var secret: String

    @SerializedName("server")
    var server: String

    @SerializedName("farm")
    var farm: String

    var photoUrl: String

    var completed: Boolean? = null

    var clickable: Boolean? = null

    constructor(photoId: String, secret: String, server: String, farm: String, photoUrl: String, isCompleted: Boolean?, clickable: Boolean?) {
        this.photoId = photoId
        this.secret = secret
        this.server = server
        this.farm = farm
        this.photoUrl = photoUrl
        this.completed = isCompleted
        this.clickable = clickable
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.photoId)
        dest.writeString(this.secret)
        dest.writeString(this.server)
        dest.writeString(this.farm)
        dest.writeString(this.photoUrl)
        dest.writeValue(this.completed)
        dest.writeValue(this.clickable)
    }

    protected constructor(`in`: Parcel) {
        this.photoId = `in`.readString()
        this.secret = `in`.readString()
        this.server = `in`.readString()
        this.farm = `in`.readString()
        this.photoUrl = `in`.readString()
        this.completed = `in`.readValue(Boolean::class.java.classLoader) as Boolean
        this.clickable = `in`.readValue(Boolean::class.java.classLoader) as Boolean
    }

    override fun toString(): String {
        return "FlickrPhoto{" +
                "photoId='" + photoId + '\'' +
                ", secret='" + secret + '\'' +
                ", server='" + server + '\'' +
                ", farm='" + farm + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", isCompleted=" + completed +
                ", clickable=" + clickable +
                '}'
    }

    companion object {

        val CREATOR: Parcelable.Creator<FlickrPhoto> = object : Parcelable.Creator<FlickrPhoto> {
            override fun createFromParcel(source: Parcel): FlickrPhoto {
                return FlickrPhoto(source)
            }

            override fun newArray(size: Int): Array<FlickrPhoto?> {
                return arrayOfNulls(size)
            }
        }
    }
}