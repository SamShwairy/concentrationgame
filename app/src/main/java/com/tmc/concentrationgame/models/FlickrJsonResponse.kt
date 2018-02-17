package com.tmc.concentrationgame.models

import com.google.gson.annotations.SerializedName

/**
 * Created by sammy on 2/17/2018.
 */

class FlickrJsonResponse(@field:SerializedName("photos")
                         var photos: FlickrPhotoWrapper) {

    override fun toString(): String {
        return "FlickrJsonResponse{" +
                "photos=" + photos +
                '}'
    }
}
