package com.tmc.concentrationgame.activities

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.databases.AppDatabase
import com.tmc.concentrationgame.dialogs.EnterNameDialog
import com.tmc.concentrationgame.dialogs.PlayAgainDialog
import com.tmc.concentrationgame.fragments.*
import com.tmc.concentrationgame.interfaces.DialogEnterNameInterface
import com.tmc.concentrationgame.interfaces.DialogPlayAgainInterface
import com.tmc.concentrationgame.interfaces.HighscoresInterface
import com.tmc.concentrationgame.methods.GetHighscores
import com.tmc.concentrationgame.methods.Methods
import com.tmc.concentrationgame.models.FlickrJsonResponse
import com.tmc.concentrationgame.models.FlickrPhotoWrapper
import com.tmc.concentrationgame.models.UserModel
import com.tmc.concentrationgame.server.ApiUtils
import com.tmc.concentrationgame.server.RetrofitServices
import com.tmc.concentrationgame.utilities.Parameters
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class MainActivity : AppCompatActivity(), HighscoresInterface, DialogPlayAgainInterface, DialogEnterNameInterface {
    //Database instance
    private var db: AppDatabase? = null
    //Difficulty level
    private var level: Parameters.Levels? = null
    //Single Player or multiplayer
    private var isSinglePlayer: Boolean = false
    //Retrofit service to parse flickr api
    private var mService: RetrofitServices? = null
    //Flickr Response
    private var flickrPhotoWrapper: FlickrPhotoWrapper? = null

    private var progressBar: ProgressBar? = null
    private var progressRelative: RelativeLayout? = null
    //Number of downloaded images
    private var downloadedImages: Int = 0
    //Number of Failed images

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        progressRelative = findViewById<View>(R.id.progress_relative) as RelativeLayout
        //Starting the api call
        mService = ApiUtils.soService
        //Downloading the images
        getPhotos()
        //Database instance
        db = AppDatabase.getAppDatabase(this)
        displayHomeMenuFragment()
    }
    //Home menu fragment
    private fun displayHomeMenuFragment() {
        val fragmentManager = supportFragmentManager
        val fragment = HomeFragment()
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, Parameters.HOME_FRAGMENT)
                .commit()
    }
    //Single player or multiplayer, also choosing the right amount of photos
    fun goToSingleOrMultiplayerGame(levels: Parameters.Levels, isSinglePlayer: Boolean) {
        this.level = levels
        this.isSinglePlayer = isSinglePlayer
        if (isSinglePlayer)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_layout, SinglePlayerFragment.newInstance(level!!, ArrayList(flickrPhotoWrapper?.photo?.subList(0, Methods.getNumberImagesRequired(levels)))), Parameters.SINGLE_PLAYER_FRAGMENT)
                    .addToBackStack(Parameters.SINGLE_PLAYER_FRAGMENT)
                    .commitAllowingStateLoss()
        else
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_layout, MultiplayerFragment.newInstance(level!!, ArrayList(flickrPhotoWrapper?.photo?.subList(0, Methods.getNumberImagesRequired(levels)))), Parameters.MULTI_PLAYER_FRAGMENT)
                    .addToBackStack(Parameters.MULTI_PLAYER_FRAGMENT)
                    .commitAllowingStateLoss()
    }
    //Screen with the difficulties
    fun goToDifficultyFragment(singlePlayer: Boolean?) {
        //In case photos where not downloaded it will redownload.
        val fragmentManager = supportFragmentManager
        val fragment = DifficultyFragment.newInstance(singlePlayer)
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, Parameters.DIFFICULTY_SELECTION_FRAGMENT)
                .addToBackStack(Parameters.DIFFICULTY_SELECTION_FRAGMENT)
                .commitAllowingStateLoss()
    }
    //Going to the high scores screen
    fun goToHighScores(userModels: Array<UserModel>) {
        val fragmentManager = supportFragmentManager
        val fragment = HighscoreFragment.newInstance(userModels)
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, Parameters.HIGHSCORE_FRAGMENT)
                .addToBackStack(Parameters.HIGHSCORE_FRAGMENT)
                .commitAllowingStateLoss()
    }
    //Reading the highscore from the database
    fun retreiveHighScores() {
        val getHighscores = GetHighscores()
        getHighscores.setHighscoresInterface(this)
        getHighscores.execute(db!!.userDao())
    }
    //Overriding on back pressed
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount < 1) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resources.getString(R.string.are_you_sure_exit))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ -> super@MainActivity.onBackPressed() }
                    .setNegativeButton(resources.getString(R.string.no)) { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        } else {
            super.onBackPressed()
        }
    }
    //Constructing the score
    private fun postScoreToLeaderboard(score: Int, name: String) {
        val userModel = UserModel()
        userModel.name = name
        userModel.score = score
        insertHighScore(userModel)

        resetList()
        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.popBackStack()
        }
    }


    //Insertion into the database
    private fun insertHighScore(user: UserModel) {
        db!!.userDao().insertUser(user)
    }
    //Showing the play again dialog
    fun showPlayAgainDialog(isWon: Boolean) {
        PlayAgainDialog(this, isWon, this)
    }
    //Showing enter name dialog
    fun showNameDialog(score: Int) {
        EnterNameDialog(this, score, this)
    }
    //After entering the name -> The interface is fired
    override fun enterName(name: String?, score: Int) {
        if (name != null) {
            postScoreToLeaderboard(score, name)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putParcelable(Parameters.PHOTOS, flickrPhotoWrapper)
            outState.putSerializable(Parameters.LEVEL_IDENTIFIER, level)
            outState.putBoolean(Parameters.SINGLE_OR_MULTIPLE, isSinglePlayer);
        }
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        flickrPhotoWrapper = savedInstanceState?.getParcelable(Parameters.PHOTOS)
        level = savedInstanceState?.getSerializable(Parameters.LEVEL_IDENTIFIER) as Parameters.Levels?
        isSinglePlayer = savedInstanceState?.getBoolean(Parameters.SINGLE_OR_MULTIPLE)!!
    }
    //Repeat the game -> Interface firing
    override fun clickYes() {
        resetList()
        supportFragmentManager.popBackStack();
        level?.let { goToSingleOrMultiplayerGame(it, isSinglePlayer) }

    }
    //Not wanting to play again ->Interface firing
    override fun clickNo() {
        supportFragmentManager.popBackStack();
        supportFragmentManager.popBackStack();
        resetList()
    }
    //Reseting the list in order to reset the statuses for opening and closing images
    fun resetList(){
        for (flickerPhotoImages in flickrPhotoWrapper?.photo!!) {
            flickerPhotoImages.completed = false
        }
    }
    //Subscribing on the api call
    fun getPhotos() {
        mService!!.getPhotos(Parameters.SAFE_NUMBER, Parameters.ONLY_PHOTOS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .retry(100) .subscribe(object : Subscriber<FlickrJsonResponse>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onNext(flickrPhotoWrapper: FlickrJsonResponse) {
                        if (flickrPhotoWrapper.photos.photo().size >= Parameters.DEFAULT_NUMBER){
                            getImages(flickrPhotoWrapper.photos)
                        }
                    }
                })
    }

    //Downloading te images
    private fun getImages(flickrPhotoWrapper: FlickrPhotoWrapper) {
        Methods.downloadImages(this, flickrPhotoWrapper.photo, Parameters.ImageSizes.q, Parameters.Levels.HARD)
        this.flickrPhotoWrapper = flickrPhotoWrapper
        progressBar?.visibility = View.GONE
        progressRelative?.visibility = View.GONE
    }
    //Interface fired
    override fun onTaskEndWithResult(userModels: Array<UserModel>) {
        goToHighScores(userModels)
    }



}
