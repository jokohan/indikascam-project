package com.example.indikascam.sessionManager

import android.content.Context
import android.content.SharedPreferences
import com.example.indikascam.R

class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_TOKEN_EXPIRES = "user_token_expires"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveExpireToken(expiresIn: Long){
        val editor = prefs.edit()
        editor.putLong(USER_TOKEN_EXPIRES, expiresIn)
        editor.apply()
    }

    fun fetchExpireToken(): Long{
        return prefs.getLong(USER_TOKEN_EXPIRES, 0)
    }
}