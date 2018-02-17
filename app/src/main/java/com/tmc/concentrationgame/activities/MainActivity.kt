package com.tmc.concentrationgame.activities

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.databases.AppDatabase
import com.tmc.concentrationgame.fragments.DifficultyFragment
import com.tmc.concentrationgame.fragments.HighscoreFragment
import com.tmc.concentrationgame.fragments.HomeFragment
import com.tmc.concentrationgame.fragments.SinglePlayerFragment
import com.tmc.concentrationgame.interfaces.HighscoresInterface
import com.tmc.concentrationgame.methods.GetHighscores
import com.tmc.concentrationgame.models.UserModel
import com.tmc.concentrationgame.utilities.Parameters


class MainActivity : AppCompatActivity(), HighscoresInterface {

    private var db: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getAppDatabase(this)
        displayHomeMenuFragment()
    }

    private fun displayHomeMenuFragment() {
        val fragmentManager = supportFragmentManager
        val fragment = HomeFragment()
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, Parameters.HOME_FRAGMENT)
                .commitAllowingStateLoss()
    }
    fun goToDifficultyFragment(singlePlayer: Boolean?) {
        val fragmentManager = supportFragmentManager
        val fragment = DifficultyFragment.newInstance(singlePlayer)
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, Parameters.DIFFICULTY_SELECTION_FRAGMENT)
                .addToBackStack(Parameters.DIFFICULTY_SELECTION_FRAGMENT)
                .commitAllowingStateLoss()
    }

    fun retreiveHighScores() {
        val getHighscores = GetHighscores()
        getHighscores.setHighscoresInterface(this)
        getHighscores.execute(db!!.userDao())
    }

    fun goToHighScores(userModels: Array<UserModel>) {
        val fragmentManager = supportFragmentManager
        val fragment = HighscoreFragment.newInstance(userModels)
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, Parameters.HIGHSCORE_FRAGMENT)
                .addToBackStack(Parameters.HIGHSCORE_FRAGMENT)
                .commitAllowingStateLoss()
    }

    fun goToSingleOrMultiplayerGame(level: Parameters.Levels, isSinglePlayer: Boolean) {
        if (isSinglePlayer)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_layout, SinglePlayerFragment.newInstance(level), Parameters.SINGLE_PLAYER_FRAGMENT)
                    .addToBackStack(Parameters.SINGLE_PLAYER_FRAGMENT)
                    .commitAllowingStateLoss()
        else
        {}
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount < 1) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resources.getString(R.string.are_you_sure_exit))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, id -> super@MainActivity.onBackPressed() }
                    .setNegativeButton(resources.getString(R.string.no)) { dialog, id -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        } else {
            super.onBackPressed()
        }
    }

    fun postScoreToLeaderboard(score: Int, name: String) {
        val userModel = UserModel()
        userModel.name = name
        userModel.score = score
        insertHighScore(userModel)

    }

    fun insertHighScore(user: UserModel) {
        db!!.userDao().insertUser(user)
    }


    override fun onTaskEndWithResult(userModels: Array<UserModel>) {
        goToHighScores(userModels)
    }
}
