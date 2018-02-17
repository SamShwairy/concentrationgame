package com.tmc.concentrationgame.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.activities.MainActivity
import com.tmc.concentrationgame.utilities.Parameters

/**
 * Created by sammy on 2/17/2018.
 */

class DifficultyFragment : Fragment(), View.OnClickListener {

    private var easyButton: Button? = null
    private var mediumButton: Button? = null
    private var hardButton: Button? = null

    private var isSinglePlayer: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            isSinglePlayer = arguments.getBoolean(Parameters.SINGLE_OR_MULTIPLE)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val layout = inflater!!.inflate(R.layout.fragment_difficulty, container, false)

        easyButton = layout.findViewById(R.id.easyButton)
        mediumButton = layout.findViewById(R.id.mediumButton)
        hardButton = layout.findViewById(R.id.hardButton)

        easyButton!!.setOnClickListener(this)
        mediumButton!!.setOnClickListener(this)
        hardButton!!.setOnClickListener(this)

        return layout
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.easyButton -> (activity as MainActivity).goToSingleOrMultiplayerGame(Parameters.Levels.EASY, isSinglePlayer!!)
            R.id.mediumButton -> (activity as MainActivity).goToSingleOrMultiplayerGame(Parameters.Levels.MEDIUM, isSinglePlayer!!)
            R.id.hardButton -> (activity as MainActivity).goToSingleOrMultiplayerGame(Parameters.Levels.HARD, isSinglePlayer!!)
            else -> {
            }
        }
    }

    companion object {

        fun newInstance(isSinglePlayer: Boolean?): DifficultyFragment {
            val fragment = DifficultyFragment()
            val args = Bundle()
            args.putBoolean(Parameters.SINGLE_OR_MULTIPLE, isSinglePlayer!!)
            fragment.arguments = args
            return fragment
        }
    }

}
