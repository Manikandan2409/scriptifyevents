package com.heremanikandan.scriptifyevents.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class SharedPrefManager(context: Context) {
    private  val user_name="USER_NAME"
    private  val user_email ="USER_EMAIL"
    private  val user_uid ="USER_UID"
    private  val user_photo_url ="USER_PHOTO_URL"
    private  val user_pref = "USERPrefs"
    private  val isLoggedIn ="IS_LOGGED_IN"
    private val prefs: SharedPreferences = context.getSharedPreferences(user_pref, Context.MODE_PRIVATE)

    fun saveUser(uid:String?,name: String?, email: String?,photoUrl:Uri?) {
        with(prefs.edit()) {
            putString(user_uid,uid)
            putString(user_name, name)
            putString(user_email, email)
            putString(user_photo_url,photoUrl.toString())
            putBoolean(isLoggedIn, true)
            apply()
        }

    }

    fun isUserLoggedIn(): Boolean = prefs.getBoolean(isLoggedIn, false)

    fun getUserName(): String? = prefs.getString(user_name, null)
    fun getUserEmail(): String? = prefs.getString(user_email, null)
    fun getUserUid(): String? = prefs.getString(user_uid, null)
   fun getPhotoUri(context: Context): Uri? {
       val sharedPreferences = context.getSharedPreferences(user_pref, Context.MODE_PRIVATE)
       // Retrieve the string representation of the URI
       val uriString = sharedPreferences.getString(user_photo_url, null)
       // Convert it back to a URI, if not null
       return uriString?.let { Uri.parse(it) }
   }

    fun signOut() {
        with(prefs.edit()) {
            clear()
            apply()
        }
    }
}
