package com.example.chatuser.other

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PreferenceManger(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    @SuppressLint("CommitPrefEdits")
    fun putBoolean(key: String, value: Boolean) =
        sharedPreferences.edit().putBoolean(key, value).apply()

    fun getBoolean(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    fun putString(key: String, value: String) =
        sharedPreferences.edit().putString(key, value).apply()

    fun getString(key: String): String? = sharedPreferences.getString(key, null)

    fun clear() = sharedPreferences.edit().clear().apply()


}