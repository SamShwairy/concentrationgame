package com.tmc.concentrationgame.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.adapters.HighscoreAdapter
import com.tmc.concentrationgame.models.UserModel
import com.tmc.concentrationgame.utilities.Parameters

/**
 * Created by sammy on 2/17/2018.
 */
class HighscoreFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var users: Array<UserModel>? = null
    private var highScoreAdapter: HighscoreAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            users = arguments.getSerializable(Parameters.USERS) as Array<UserModel>
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_highscore, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateList()
    }

    private fun populateList() {

        highScoreAdapter = HighscoreAdapter(users, activity)
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = highScoreAdapter
    }

    companion object {

        fun newInstance(users: Array<UserModel>): HighscoreFragment {
            val fragment = HighscoreFragment()
            val args = Bundle()
            args.putSerializable(Parameters.USERS, users)
            fragment.arguments = args
            return fragment
        }
    }
}
