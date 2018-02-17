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
    companion object {

        //Identifiers
        val HOME_FRAGMENT = "HOME_FRAGMENT"
        val SINGLE_OR_MULTIPLE = "SINGLE_OR_MULTIPLE"
        val USERS = "USERS"
        val HIGHSCORE_FRAGMENT = "HIGHSCORE_FRAGMENT"
        val DIFFICULTY_SELECTION_FRAGMENT = "DIFFICULTY_SELECTION_FRAGMENT"

        //Database
        const val DATABASE = "DATABASE"

        //API
        const val CONTENT_TYPE = "content_type"
        const val PER_PAGE = "per_page"
        const val QUERY_SEARCH_KITTEN_WITH_API = "?method=flickr.photos.search&tags=kittens&api_key=11ba8223eab58b07f4f9bbdf6cea5e50&format=json&nojsoncallback=?"
    }
}