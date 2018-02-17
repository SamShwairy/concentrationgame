package com.tmc.concentrationgame.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by sammy on 2/17/2018.
 */

class FlickrPhotoWrapper(var photo: ArrayList<FlickrPhoto>) : Parcelable {
    fun photo(): List<FlickrPhoto> {
        return photo
    }

    fun setPhotoList(photo: ArrayList<FlickrPhoto>) {
        this.photo = photo
    }

    override fun toString(): String {
        return "FlickrPhotoWrapper{" +
                "photoList=" + photo +
                '}'
    }

    constructor(source: Parcel) : this(
            source.createTypedArrayList(FlickrPhoto.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(photo)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FlickrPhotoWrapper> = object : Parcelable.Creator<FlickrPhotoWrapper> {
            override fun createFromParcel(source: Parcel): FlickrPhotoWrapper = FlickrPhotoWrapper(source)
            override fun newArray(size: Int): Array<FlickrPhotoWrapper?> = arrayOfNulls(size)
        }
    }
}