package com.tmc.concentrationgame.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.GridLayout
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

class MultiplayerFragment : Fragment(), OnToggledListener {

    private var firstPlayerScoreTextView: TextView? = null
    private var secondPlayerScoreTextView: TextView? = null
    private var playerOneTurn: Boolean? = true
    private var level: Parameters.Levels? = null
    private val handler = Handler()
    private var myGridLayout: GridLayout? = null
    private var myViews: ArrayList<MyView>? = ArrayList()
    private var firstImageId: String? = null
    private var secondImageId: String? = null
    private var flickrPhotos: ArrayList<FlickrPhoto>? = ArrayList()
    private var startGame: Runnable? = null

    private var numberOfImagesRequired: Int = 0
    private var numOfCol: Int = 0
    private val numOfRow = 4
    private val MARGIN = 4
    private var gridWidth: Int = 0
    private var firstPlayerScore = 0
    private var secondPlayerScore = 0

    private var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            level = arguments.getSerializable(Parameters.LEVEL_IDENTIFIER) as Parameters.Levels
            flickrPhotos = arguments.getParcelableArrayList(Parameters.PHOTOS)
        }
        //Number of images required based on difficulty
        numberOfImagesRequired = Methods.getNumberImagesRequired(level)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_multiplayer, container, false)
        myGridLayout = view.findViewById<View>(R.id.mygrid) as GridLayout
        firstPlayerScoreTextView = view.findViewById<View>(R.id.firstPlayerScore) as TextView
        secondPlayerScoreTextView = view.findViewById<View>(R.id.secondPlayerScore) as TextView
        secondPlayerScoreTextView!!.text = resources.getString(R.string.secondPlayerScore) + secondPlayerScore.toString()
        firstPlayerScoreTextView!!.text = resources.getString(R.string.firstPlayerScore) + firstPlayerScore.toString()

        Toast.makeText(activity, resources.getString(R.string.game_will_start), Toast.LENGTH_SHORT).show()
        return view
    }
    //Interface fired, when two are fired we compare the IDS
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
            secondImageId = ""
        } else {
            firstImageId = idString
        }


    }
    //Comparing the IDs of the images if they match we add the score, if they dont we close the opened images
    private fun compare(firstImageId: String?, secondImageId: String) {
        if (firstImageId == secondImageId) {
            for (i in flickrPhotos!!.indices) {
                if (flickrPhotos!![i].photoId == firstImageId) {
                    flickrPhotos!![i].completed = true
                    myViews!![i].setCompleted()
                }
            }

            if (playerOneTurn!!) {
                firstPlayerScore ++
                firstPlayerScoreTextView!!.text = resources.getString(R.string.firstPlayerScore) + firstPlayerScore.toString()

            } else {
                secondPlayerScore ++
                secondPlayerScoreTextView!!.text = resources.getString(R.string.secondPlayerScore) + secondPlayerScore.toString()
            }
            if (firstPlayerScore + secondPlayerScore == numberOfImagesRequired) {
                if (firstPlayerScore > secondPlayerScore) {
                    Toast.makeText(activity, resources.getString(R.string.player_one_won), Toast.LENGTH_SHORT).show()
                    showEndGameDialog(true)
                } else if (firstPlayerScore < secondPlayerScore) {
                    Toast.makeText(activity, resources.getString(R.string.player_two_won), Toast.LENGTH_SHORT).show()
                    showEndGameDialog(false)
                } else if (firstPlayerScore == secondPlayerScore) {
                    Toast.makeText(activity, resources.getString(R.string.tie), Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).showPlayAgainDialog(false)
                }
            }
        } else {
            //Close opened images and disable touch
            for (i in myViews!!.indices) {
                myViews!![i].closeOpenedImages()
                myViews!![i].setTouchDisabled()
            }
            //show player turn
            playerOneTurn = !playerOneTurn!!
            showPlayerTurn(playerOneTurn!!)
        }
        //reenable touch
        handler.postDelayed({
            for (i in myViews!!.indices) {
                myViews!![i].setTouchEnabled()
            }
        }, context.resources.getInteger(R.integer.anim_length).toLong())
    }

    private fun showPlayerTurn(playerOneTurn: Boolean) {
        if (playerOneTurn) {
            Toast.makeText(activity, resources.getString(R.string.player_one_turn), Toast.LENGTH_SHORT).show()
            firstPlayerScoreTextView!!.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
            secondPlayerScoreTextView!!.setTextColor(ContextCompat.getColor(activity, R.color.black))
        } else {
            Toast.makeText(activity, resources.getString(R.string.player_two_turn), Toast.LENGTH_SHORT).show()
            firstPlayerScoreTextView!!.setTextColor(ContextCompat.getColor(activity, R.color.black))
            secondPlayerScoreTextView!!.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
        }

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridWidth = resources.getDimension(R.dimen.unit_92).toInt()
        displayGame(flickrPhotos!!)
    }

    //Duplicating the images, shuffling them and inserting them into the grid, adding onGlobal LayoutListener to modify the layout
    private fun displayGame(flickrPhotos: ArrayList<FlickrPhoto>) {
        this.flickrPhotos = flickrPhotos

        flickrPhotos.addAll(flickrPhotos)

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
                startGame = Runnable { tView.flipCard() }
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
    //End of game dialog
    private fun showEndGameDialog(firstPlayerWon: Boolean) {
        if (activity != null) {
            if (firstPlayerWon)
                (activity as MainActivity).showNameDialog(firstPlayerScore)
            else
                (activity as MainActivity).showNameDialog(secondPlayerScore)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacks(startGame)
    }

    companion object {

        fun newInstance(level: Parameters.Levels, flickrPhotos: ArrayList<FlickrPhoto>): MultiplayerFragment {
            val fragment = MultiplayerFragment()
            val args = Bundle()
            args.putSerializable(Parameters.LEVEL_IDENTIFIER, level)
            args.putParcelableArrayList(Parameters.PHOTOS, flickrPhotos)
            fragment.arguments = args
            return fragment
        }
    }

}
