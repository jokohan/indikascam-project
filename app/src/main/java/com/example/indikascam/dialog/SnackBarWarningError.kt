package com.example.indikascam.dialog

import android.app.Activity
import com.google.android.material.snackbar.Snackbar

class SnackBarWarningError {
    fun showSnackBar(message: String?, activity: Activity?){
        if (activity !=null  && message != null) {
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG
            ).show()
        }
    }
}