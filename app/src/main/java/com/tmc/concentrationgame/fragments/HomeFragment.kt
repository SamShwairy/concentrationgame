package com.tmc.concentrationgame.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.activities.MainActivity

/**
 * Created by sammy on 2/17/2018.
 */
class HomeFragment : Fragment(), View.OnClickListener {

    private var singlePlayerButton: Button? = null
    private var multiPlayerButton: Button? = null
    private var highscores: Button? = null
    private var exitButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = inflater!!.inflate(R.layout.fragment_home, container, false)

        singlePlayerButton = layout.findViewById(R.id.singlePlayerButton)
        multiPlayerButton = layout.findViewById(R.id.multiPlayerButton)
        highscores = layout.findViewById(R.id.highscores)
        exitButton = layout.findViewById(R.id.exitButton)


        singlePlayerButton!!.setOnClickListener(this)
        multiPlayerButton!!.setOnClickListener(this)
        highscores!!.setOnClickListener(this)
        exitButton!!.setOnClickListener(this)


        return layout
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.singlePlayerButton -> if (activity != null)
                (activity as MainActivity).goToDifficultyFragment(true)
            R.id.multiPlayerButton -> if (activity != null)
                (activity as MainActivity).goToDifficultyFragment(false)
            R.id.highscores -> if (activity != null)
                (activity as MainActivity).retreiveHighScores()

            R.id.exitButton -> if (activity != null)
                activity.onBackPressed()
            else -> {
            }
        }
    }

}