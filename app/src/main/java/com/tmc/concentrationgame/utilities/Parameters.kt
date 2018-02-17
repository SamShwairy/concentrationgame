package com.tmc.concentrationgame.utilities

/**
 * Created by sammy on 2/17/2018.
 */
interface Parameters {
    enum class Levels {
        EASY,
        MEDIUM,
        HARD
    }
    //    s	small square 75x75
    //    q	large square 150x150
    //    t	thumbnail, 100 on longest side
    //    m	small, 240 on longest side
    //    n	small, 320 on longest side
    //    medium, 500 on longest side
    //    z	medium 640, 640 on longest side
    //    c	medium 800, 800 on longest side†
    //    b	large, 1024 on longest side*
    //    h	large 1600, 1600 on longest side†
    //    k	large 2048, 2048 on longest side†
    //    o	original image, either a jpg, gif or png, depending on source format

    enum class ImageSizes {
        s,
        q,
        t,
        m,
        n,
        z

    }

    companion object {

        //Identifiers
        val HOME_FRAGMENT = "HOME_FRAGMENT"
        val SINGLE_OR_MULTIPLE = "SINGLE_OR_MULTIPLE"
        val USERS = "USERS"
        val HIGHSCORE_FRAGMENT = "HIGHSCORE_FRAGMENT"
        val DIFFICULTY_SELECTION_FRAGMENT = "DIFFICULTY_SELECTION_FRAGMENT"
        val LEVEL_IDENTIFIER = "LEVEL_IDENTIFIER"
        val SINGLE_PLAYER_FRAGMENT = "SINGLE_PLAYER_FRAGMENT"
        val MULTI_PLAYER_FRAGMENT = "MULTI_PLAYER_FRAGMENT"
        val DEFAULT_NUMBER = 16
        val SAFE_NUMBER = 25
        val PHOTOS = "PHOTOS"

        //Database
        const val DATABASE = "users"

        //API
        const val CONTENT_TYPE = "content_type"
        const val PER_PAGE = "per_page"
        const val QUERY_SEARCH_KITTEN_WITH_API = "?method=flickr.photos.search&tags=kittens&api_key=11ba8223eab58b07f4f9bbdf6cea5e50&format=json&nojsoncallback=?&is_getty=true"
        //In order to specify content type of search query for the flickr api
        val ONLY_PHOTOS = 1
    }
}