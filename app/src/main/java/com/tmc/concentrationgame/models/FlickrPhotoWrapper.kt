package com.tmc.concentrationgame.models

import java.util.*

/**
 * Created by sammy on 2/17/2018.
 */

class FlickrPhotoWrapper(var photo: ArrayList<FlickrPhoto>) {

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
}