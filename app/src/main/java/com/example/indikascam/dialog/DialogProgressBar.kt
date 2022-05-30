package com.example.indikascam.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import com.example.indikascam.R

class DialogProgressBar {

    companion object{
        fun progressDialog(context: Context): Dialog{
            val progressDialog = Dialog(context)
            progressDialog.setContentView(R.layout.dialog_progress_bar)
            progressDialog.setCancelable(false)
            return progressDialog
        }
    }
}