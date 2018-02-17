package com.tmc.concentrationgame.dialogs

import android.content.Context
import android.support.v7.app.AlertDialog
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.interfaces.DialogPlayAgainInterface


/**
 * Created by sammy on 2/17/2018.
 */

class PlayAgainDialog(context: Context, isWon: Boolean?, dialogPlayAgainInterface: DialogPlayAgainInterface) {
    init {
        var title = context.resources.getString(R.string.time_is_up)
        if (isWon!!)
            title = context.resources.getString(R.string.you_win)
        val builder = AlertDialog.Builder(context)
        builder.setMessage(title)
                .setCancelable(false)
                .setPositiveButton(context.resources.getString(R.string.yes)) { dialog, id -> dialogPlayAgainInterface.clickYes() }
                .setNegativeButton(context.resources.getString(R.string.no)) { dialog, id -> dialogPlayAgainInterface.clickNo() }
        val alert = builder.create()
        alert.show()
    }
}
