package com.heremanikandan.scriptifyevents

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class ScriptifyEventsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseInit", "✅ Firebase successfully initialized")
        } else {
            Log.d("FirebaseInit", "✅ Firebase was already initialized")
        }
    }
}
