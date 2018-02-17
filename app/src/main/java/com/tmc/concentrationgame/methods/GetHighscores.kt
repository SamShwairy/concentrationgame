package com.tmc.concentrationgame.methods

import android.os.AsyncTask
import com.tmc.concentrationgame.interfaces.HighscoresInterface
import com.tmc.concentrationgame.interfaces.UserDao
import com.tmc.concentrationgame.models.UserModel

/**
 * Created by sammy on 2/17/2018.
 */
class GetHighscores : AsyncTask<UserDao, Void, Array<UserModel>>() {
    private var highscoresInterface: HighscoresInterface? = null

    fun setHighscoresInterface(highscoresInterface: HighscoresInterface) {
        this.highscoresInterface = highscoresInterface
    }

    override fun doInBackground(vararg params: UserDao): Array<UserModel> {
        return params[0].loadAllUsers()
    }

    override fun onPostExecute(users: Array<UserModel>) {
        if (highscoresInterface != null)
            highscoresInterface!!.onTaskEndWithResult(users)
    }
}
