package com.tmc.concentrationgame.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.models.UserModel

/**
 * Created by sammy on 2/17/2018.
 */

class HighscoreAdapter(private val users: Array<UserModel>?, private val context: Context) : RecyclerView.Adapter<HighscoreAdapter.ViewHolder>() {
    //View Holder
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val name: TextView = v.findViewById<View>(R.id.name) as TextView
        val score: TextView = v.findViewById<View>(R.id.score) as TextView
        val userId: TextView = v.findViewById<View>(R.id.user_id) as TextView

    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): HighscoreAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_highscore, parent, false)

        return HighscoreAdapter.ViewHolder(v)
    }
    //First row is the header
    override fun onBindViewHolder(holder: HighscoreAdapter.ViewHolder, position: Int) {
        if (position == 0) {
            holder.userId.text = context.resources.getString(R.string.rank)
            holder.name.text = context.resources.getString(R.string.name)
            holder.score.text = context.resources.getString(R.string.highscore)
        } else {
            holder.userId.text = position.toString()
            holder.name.text = users?.get(position - 1)?.name ?: ""
            holder.score.text = users?.get(position - 1)?.score.toString()
        }
    }

    //Adding one for the header
    override fun getItemCount(): Int {
        if (users != null) {
            return users.size + 1
        }
        return 0
    }

}