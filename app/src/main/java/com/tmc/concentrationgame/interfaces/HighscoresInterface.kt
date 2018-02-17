package com.tmc.concentrationgame.interfaces

import com.tmc.concentrationgame.models.UserModel

/**
 * Created by sammy on 2/17/2018.
 */
interface HighscoresInterface {
    fun onTaskEndWithResult(userModels: Array<UserModel>)
}