package com.heremanikandan.scriptifyevents.utils.connections;

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.heremanikandan.scriptifyevents.auth.AuthManager
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager

class Permission(private val context: Context) {

    private val sharedPrefManager = SharedPrefManager(context)

    fun hasPermissionForUser(userUid: String, key: String): Boolean {
        val permissions = sharedPrefManager.getPermissionsForUser(userUid)
        return permissions[key] == true
    }

    fun requestPermissionIfMissing(
        userUid: String,
        key: String,
        activityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        onGranted: () -> Unit,
        onLater: () -> Unit
    ) {
        if (hasPermissionForUser(userUid, key)) {
            onGranted()
        } else {
            val authManager = AuthManager(context)
            authManager.requestAdditionalPermissionsIfNeeded(context, activityResultLauncher)
            onLater()
        }
    }
}
