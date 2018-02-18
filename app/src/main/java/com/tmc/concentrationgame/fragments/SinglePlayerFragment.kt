package com.tmc.concentrationgame.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.activities.MainActivity
import com.tmc.concentrationgame.interfaces.OnToggledListener
import com.tmc.concentrationgame.methods.Methods
import com.tmc.concentrationgame.models.FlickrPhoto
import com.tmc.concentrationgame.utilities.Parameters
import com.tmc.concentrationgame.views.MyView
import java.util.*

/**
 * Created by sammy on 2/17/2018.
 */

class SinglePlayerFragment : Fragment(), OnToggledListener {

    private var level: Parameters.Levels? = null
    private val handler = Handler()
    private var myGridLayout: GridLayout? = null
    private var scoreTextView: TextView? = null
    private var myViews: ArrayList<MyView>? = ArrayList()
    private var firstImageId: String? = null
    private var secondImageId: String? = null
    private var flickrPhotos: ArrayList<FlickrPhoto>? = null
    private var countDownTimer: CountDownTimer? = null
    private var progressBar: ProgressBar? = null
    private var timeTextView: TextView? = null
    private var startGame: Runnable? = null
    private var isCompleted = false

    private var numberOfImagesRequired: Int = 0
    private var numOfCol: Int = 0
    private val numOfRow = 4
    private val MARGIN = 4
    private var gridWidth: Int = 0
    private var score = 0
    private var seconds: Long = 0

    private var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            level = arguments.getSerializable(Parameters.LEVEL_IDENTIFIER) as Parameters.Levels
            flickrPhotos = arguments.getParcelableArrayList(Parameters.PHOTOS)
        }
        //Number of images required based on the difficulty
        numberOfImagesRequired = Methods.getNumberImagesRequired(level)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_single_player, container, false)
        progressBar = view.findViewById(R.id.progressBar)

        myGridLayout = view.findViewById(R.id.mygrid)
        scoreTextView = view.findViewById(R.id.score)
        timeTextView = view.findViewById(R.id.timeTextView)
        scoreTextView!!.text = resources.getString(R.string.score) + score.toString()

        Toast.makeText(activity, resources.getString(R.string.game_will_start), Toast.LENGTH_SHORT).show()

        return view
    }
    //Interface fired, when two interfaces are fired we compare the ID's of the images
    override fun OnToggled(v: MyView) {

        //get the id string
        val idString = v.imageId
        if (!TextUtils.isEmpty(firstImageId)) {
            for (i in myViews!!.indices) {
                myViews!![i].setTouchDisabled()
            }
            secondImageId = idString
            compare(firstImageId, secondImageId!!)
            firstImageId = ""
            secondImageId = "_"
        } else {
            firstImageId = idString
        }


    }
    //Comparing the IDs of the images if they match we add to the score. If the score == to number of photos then
    private fun compare(firstImageId: String?, secondImageId: String) {
        if (firstImageId == secondImageId) {
            for (i in flickrPhotos!!.indices) {
                if (flickrPhotos!![i].photoId == firstImageId) {
                    flickrPhotos!![i].completed = true
                    myViews!![i].setCompleted()
                }
            }
            score++
            scoreTextView!!.text = resources.getString(R.string.score) + score.toString()
            if (score == numberOfImagesRequired) {
                showEndGameDialog()
            }
        } else {
            //Closing opened images after failing
            for (i in myViews!!.indices) {
                myViews!![i].closeOpenedImages()
                myViews!![i].setTouchDisabled()
            }
        }
        //Reenabling the touch
        handler.postDelayed({
            for (i in myViews!!.indices) {
                myViews!![i].setTouchEnabled()
            }
        }, context.resources.getInteger(R.integer.anim_length).toLong())
    }
    //Creating the timer
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        countDownTimer = startTimer(resources.getInteger(R.integer.timer))

    }
    //Displaying the images
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridWidth = resources.getDimension(R.dimen.unit_92).toInt()

        displayGame(flickrPhotos)
    }

    //Duplicating the images, shuffling them and inserting them into the grid, adding onGlobal LayoutListener to modify the layout
    private fun displayGame(flickrPhotos: ArrayList<FlickrPhoto>?) {
        flickrPhotos!!.addAll(flickrPhotos)

        val seed = System.nanoTime()
        Collections.shuffle(flickrPhotos, Random(seed))


        numOfCol = flickrPhotos.size / 4
        myGridLayout!!.columnCount = numOfCol
        myGridLayout!!.rowCount = numOfRow
        myViews = ArrayList(numOfCol * numOfRow)
        for (yPos in 0 until numOfRow) {
            for (xPos in 0 until numOfCol) {
                val tView = MyView(activity, flickrPhotos[yPos * numOfCol + xPos], handler)
                tView.setOnToggledListener(this)

                myViews!!.add(tView)
                myGridLayout!!.addView(tView)
                handler.postDelayed(tView.hideBackImage, resources.getInteger(R.integer.anim_length_half).toLong())
                startGame = Runnable {
                    tView.flipCard()
                    if (isAdded) {
                        countDownTimer!!.start()
                    }
                }
                handler.postDelayed(startGame, resources.getInteger(R.integer.loading_time).toLong())
            }
        }
        onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            var pWidth = myGridLayout!!.width
            val pHeight = myGridLayout!!.height

            if (level === Parameters.Levels.EASY) {
                pWidth = gridWidth * 4 + 20
            } else if (level === Parameters.Levels.MEDIUM) {
                pWidth = gridWidth * 6 + 30
            }


            val w = pWidth / numOfCol
            val h = pHeight / numOfRow

            for (yPos in 0 until numOfRow) {
                for (xPos in 0 until numOfCol) {
                    val params = myViews!![yPos * numOfCol + xPos].layoutParams as GridLayout.LayoutParams
                    params.width = w - 2 * MARGIN
                    params.height = h - 2 * MARGIN
                    params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN)
                    if (myViews != null && !myViews!!.isEmpty())
                        myViews!![yPos * numOfCol + xPos].layoutParams = params
                }
            }
        }
        myGridLayout!!.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)


    }
    //Game ended dialog
    private fun showEndGameDialog() {
        if (countDownTimer != null)
            countDownTimer!!.cancel()

        if (activity != null) {
            score += seconds.toInt()
            (activity as MainActivity).showNameDialog(score)
        }
    }

    //Countdown timer initialization
    private fun startTimer(minuti: Int): CountDownTimer {
        countDownTimer = object : CountDownTimer((60 * minuti * 1000).toLong(), 1000) {

            override fun onTick(leftTimeInMilliseconds: Long) {
                seconds = leftTimeInMilliseconds / 1000
                progressBar!!.progress = seconds.toInt()
                timeTextView!!.text = String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60)
            }

            override fun onFinish() {
                if (!isCompleted) {
                    if (activity != null)
                        (activity as MainActivity).showPlayAgainDialog(false)
                    timeTextView!!.text = ""
                    countDownTimer!!.cancel()
                    isCompleted = true
                }

            }
        }
        return countDownTimer as CountDownTimer
    }


    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null)
            countDownTimer!!.cancel()
        handler.removeCallbacks(startGame)
    }

    companion object {

        fun newInstance(level: Parameters.Levels, flickrPhotos: ArrayList<FlickrPhoto>): SinglePlayerFragment {
            val fragment = SinglePlayerFragment()
            val args = Bundle()
            args.putSerializable(Parameters.LEVEL_IDENTIFIER, level)
            args.putParcelableArrayList(Parameters.PHOTOS, flickrPhotos)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
