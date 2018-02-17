package com.tmc.concentrationgame.server

/**
 * Created by sammy on 2/17/2018.
 */

object ApiUtils {

    val BASE_URL = "https://api.flickr.com/services/rest/"

    val soService: RetrofitServices?
        get() = RetrofitClient.getClient(BASE_URL)?.create(RetrofitServices::class.java)
}
