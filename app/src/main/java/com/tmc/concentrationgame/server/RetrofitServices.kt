package com.tmc.concentrationgame.server

import com.tmc.concentrationgame.models.FlickrJsonResponse
import com.tmc.concentrationgame.utilities.Parameters
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by sammy on 2/17/2018.
 */
interface RetrofitServices {

    @GET(Parameters.QUERY_SEARCH_KITTEN_WITH_API + "")
    fun getPhotos(@Query(Parameters.PER_PAGE) level: Int, @Query(Parameters.CONTENT_TYPE) onlyPhotos: Int): Observable<FlickrJsonResponse>
}
