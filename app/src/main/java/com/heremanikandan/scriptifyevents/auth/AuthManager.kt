
package com.heremanikandan.scriptifyevents.auth

import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.slides.v1.SlidesScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.db.ScriptyManager
import com.heremanikandan.scriptifyevents.db.model.User
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager: CredentialManager = CredentialManager.create(context)
    private val sharedPrefManager = SharedPrefManager(context)

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 101
        const val REQUEST_AUTHORIZE = 1001
    }




    suspend fun signInWithGoogle(
        context: Context,
        activityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ): Boolean {
        return try {
            val nonce = UUID.randomUUID().toString()
            val digest = MessageDigest.getInstance("SHA-256").digest(nonce.toByteArray())
            val hashedNonce = digest.joinToString("") { "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Allow new users
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setNonce(hashedNonce)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(authCredential).await()

                val user = auth.currentUser

                sharedPrefManager.saveUser(
                    uid = user?.uid,
                    name = user?.displayName,
                    email = user?.email,
                    photoUrl = user?.photoUrl
                )
                val db = ScriptyManager.getInstance(context)
                val dbusers=db.userDao()
                val newUser =User(
                    uid = user?.uid.toString(),
                    name = user?.displayName.toString(),
                    email = user?.email.toString(),
                    disabled = false
                )
                dbusers.insertUser(newUser)

                Log.d("Auth", "User Signed In: ${user?.email}")

                // ✅ Step 2: Request Additional Scopes using Launcher
                requestAdditionalPermissionsIfNeeded(context, activityResultLauncher)
                Log.d("Auth", "RETURNING TRUE")
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In Error: ${e.message}")
            false
        }
    }




//    fun requestAdditionalPermissions(
//        context: Context,
//        activityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
//    ) {
//        val requestedScopes = listOf(
//            Scope(DriveScopes.DRIVE_FILE),
//            Scope(GmailScopes.GMAIL_READONLY),
//            Scope(GmailScopes.GMAIL_COMPOSE),
//            Scope(CalendarScopes.CALENDAR),
//            Scope(SheetsScopes.SPREADSHEETS),
//            Scope(SlidesScopes.PRESENTATIONS)
//        )
//
//        val authorizationRequest = AuthorizationRequest.builder()
//            .setRequestedScopes(requestedScopes)
//            .build()
//
//        Identity.getAuthorizationClient(context)
//            .authorize(authorizationRequest)
//            .addOnSuccessListener { authorizationResult ->
//                if (authorizationResult.hasResolution()) {
//                    val pendingIntent = authorizationResult.getPendingIntent()
//                    Log.d("Auth", "Requesting user approval")
//                    try {
//                        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent!!).build()
//                        activityResultLauncher.launch(intentSenderRequest) // ✅ Use registered launcher
//                    } catch (e: IntentSender.SendIntentException) {
//                        Log.e("Authorization", "Couldn't start Authorization UI: ${e.localizedMessage}")
//                    }
//                } else {
//                    Log.d("Auth", "Saving the permissions")
//                    saveUserPermissionsLocally()
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("Authorization", "Failed to authorize: ${e.localizedMessage}")
//            }
//    }


    fun requestAdditionalPermissionsIfNeeded(
        context: Context,
        activityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) {
        val TAG = "REQUEST_PERMISSIONS"
        Log.d(TAG,"Start of checkign the permission")
        val user = auth.currentUser ?: return
        val userUid = user.uid
        val currentPermissions = sharedPrefManager.getPermissionsForUser(userUid)

        val requiredScopes = mutableListOf<Scope>()

        if (!currentPermissions["drive"]!!) requiredScopes.add(Scope(DriveScopes.DRIVE_FILE))
        if (!currentPermissions["gmail"]!!) {
            requiredScopes.add(Scope(GmailScopes.GMAIL_READONLY))
            requiredScopes.add(Scope(GmailScopes.GMAIL_COMPOSE))
        }
        if (!currentPermissions["calendar"]!!) requiredScopes.add(Scope(CalendarScopes.CALENDAR))
        if (!currentPermissions["sheets"]!!) requiredScopes.add(Scope(SheetsScopes.SPREADSHEETS))
        if (!currentPermissions["slides"]!!) requiredScopes.add(Scope(SlidesScopes.PRESENTATIONS))

        Log.d(TAG,"SEETED ALL THE PERMISSION")
        if (requiredScopes.isEmpty()) {
            Log.d("Auth", "All permissions already granted for $userUid")
            return
        }

        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(requiredScopes)
            .build()

        Identity.getAuthorizationClient(context)
            .authorize(authorizationRequest)
            .addOnSuccessListener { authorizationResult ->
                if (authorizationResult.hasResolution()) {
                    val pendingIntent = authorizationResult.getPendingIntent()
                    Log.d(TAG,"going to invoke al the permissions")
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent!!).build()
                        activityResultLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("Authorization", "Couldn't start Authorization UI: ${e.localizedMessage}")
                    }
                } else {
                    val grantedScopes = requiredScopes.map { it.scopeUri }
                    saveUserPermissionsLocally(userUid, grantedScopes)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Authorization", "Failed to authorize: ${e.localizedMessage}")
            }
    }


    /**
     * Store granted permissions in Firebase
     */

    fun saveUserPermissionsLocally(userUid: String, grantedScopes: List<String>) {
        val permissions = mapOf(
            "drive" to grantedScopes.any { it.contains("drive") },
            "gmail" to grantedScopes.any { it.contains("gmail") },
            "calendar" to grantedScopes.any { it.contains("calendar") },
            "sheets" to grantedScopes.any { it.contains("spreadsheets") },
            "slides" to grantedScopes.any { it.contains("presentations") }
        )
        sharedPrefManager.savePermissionsForUser(userUid, permissions)
        Log.d("Permissions", "Saved locally for user $userUid: $permissions")
    }



    fun saveUserPermissions() {
        val user = auth.currentUser ?: return

        val permissions = mapOf(
            "drive" to true,
            "gmail" to true,
            "calendar" to true,
            "sheets" to true,
            "slides" to true
        )

//        FirebaseDatabase.getInstance().getReference("users/${user.uid}/permissions")
//            .setValue(permissions)
//            .addOnSuccessListener {
//                Log.d("Permissions", "User permissions saved successfully!")
//            }
//            .addOnFailureListener {
//                Log.e("Permissions", "Failed to save permissions: ${it.message}")
//            }
    }



    /**
     * Sign Up / Sign In with Email & Password
     */
    suspend fun signInWithEmail(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            sharedPrefManager.saveUser(uid = auth.currentUser?.uid, name = auth.currentUser?.displayName, email=email, photoUrl = auth.currentUser?.photoUrl)
            true
        } catch (e: Exception) {
            Log.e("Auth", "Email Sign-In Error: ${e.message}")
            false
        }
    }

    /**
     * Sign Out
     */
    fun signOut() {
        auth.signOut()
        sharedPrefManager.signOut()
    }

    /**
     * Check if Email Exists
     */
    fun checkUserExists(email: String, onResult: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    onResult(!signInMethods.isNullOrEmpty()) // true if email exists
                } else {
                    onResult(false)
                }
            }
    }

    /**
     * Generate OTP for Email Sign-Up
     */
    fun generateOTP(): String {
        return (100000..999999).random().toString()
    }

    /**
     * Store OTP in Firebase
     */
    fun storeOtpInFirebase(email: String, otp: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val otpRef = database.child("otp_verifications").child(email.replace(".", ","))

        val otpData = mapOf(
            "otp" to otp,
            "timestamp" to System.currentTimeMillis()
        )

        otpRef.setValue(otpData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Verify OTP
     */
    fun verifyOtp(email: String, enteredOtp: String, onResult: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val otpRef = database.child("otp_verifications").child(email.replace(".", ","))

        otpRef.get().addOnSuccessListener { snapshot ->
            val storedOtp = snapshot.child("otp").value?.toString()
            val timestamp = snapshot.child("timestamp").value as? Long
            val currentTime = System.currentTimeMillis()

            if (storedOtp == enteredOtp && timestamp != null && (currentTime - timestamp) <= 2 * 60 * 1000) {
                onResult(true) // OTP is correct and within 2-minute validity
            } else {
                onResult(false) // Invalid or expired OTP
            }
        }.addOnFailureListener { onResult(false) }
    }
}
