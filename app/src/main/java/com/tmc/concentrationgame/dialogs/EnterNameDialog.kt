package com.tmc.concentrationgame.dialogs

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.interfaces.DialogEnterNameInterface

/**
 * Created by sammy on 2/17/2018.
 */

class EnterNameDialog(context: Context, score: Int, dialogEnterNameInterface: DialogEnterNameInterface) {
    init {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_enter_your_name)

        val name = dialog.findViewById<View>(R.id.name) as EditText

        val save = dialog.findViewById<Button>(R.id.save)
        save.setOnClickListener {
            if (!TextUtils.isEmpty(name.text.toString()))
                dialogEnterNameInterface.enterName(name.text.toString(), score)
            dialog.dismiss()
        }

        dialog.show()
    }
}
