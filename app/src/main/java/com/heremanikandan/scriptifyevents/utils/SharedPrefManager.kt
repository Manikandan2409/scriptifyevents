package com.heremanikandan.scriptifyevents.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveUser(name: String?, email: String?) {
        with(prefs.edit()) {
            putString("USER_NAME", name)
            putString("USER_EMAIL", email)
            apply()
        }
    }

    fun getUserName(): String? = prefs.getString("USER_NAME", null)
    fun getUserEmail(): String? = prefs.getString("USER_EMAIL", null)

    fun clearUserData() {
        with(prefs.edit()) {
            clear()
            apply()
        }
    }
}
