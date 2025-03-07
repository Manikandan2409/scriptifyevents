
package com.heremanikandan.scriptifyevents.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.heremanikandan.scriptifyevents.R
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
    }

    /**
     * Sign Up or Sign In with Google and Request Extra Permissions
     */
    suspend fun signInWithGoogle(): Boolean {
        return try {
            val nonce = UUID.randomUUID().toString()
            val digest = MessageDigest.getInstance("SHA-256").digest(nonce.toByteArray())
            val hashedNonce = digest.joinToString("") { "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // New users only
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setNonce(hashedNonce)
                .setAutoSelectEnabled(false)
                .setAutoSelectEnabled(true)
//                .setAdditionalScopes( // ✅ Use this for requesting extra permissions
//                    listOf(
//                        Scope(Scopes.DRIVE_FILE),
//                        Scope(GmailScopes.GMAIL_READONLY),
//                        Scope(GmailScopes.GMAIL_COMPOSE),
//                        Scope(CalendarScopes.CALENDAR),
//                        Scope(SheetsScopes.SPREADSHEETS),
//                        Scope(SlidesScopes.PRESENTATIONS)
//                    )
//                )
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

                sharedPrefManager.saveUser(uid = user?.uid,
                    name = user?.displayName,
                    email = user?.email,
                    photoUrl = user?.photoUrl)

                // Store Permissions in Firebase
                storeUserPermissions(user?.uid, user?.email)

                Log.d("Auth", "User Signed In: ${user?.email}")
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In Error: ${e.message}")
            false
        }
    }

    /**
     * Store granted permissions in Firebase
     */
    private fun storeUserPermissions(uid: String?, email: String?) {
        uid?.let {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(it)
            val userData = mapOf(
                "email" to email,
                "permissions" to listOf(
                    "drive_file",
                    "gmail_read",
                    "gmail_compose",
                    "calendar",
                    "sheets",
                    "slides"
                )
            )
            userRef.setValue(userData)
        }
    }

    /**
     * Sign in with Credential Manager (Returning Users)
     */
    suspend fun signInWithCredentialManager(): Boolean {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true) // Existing users only
                .setServerClientId(context.getString(com.heremanikandan.scriptifyevents.R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
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
                auth.signInWithCredential(authCredential).await()

                Log.d("Auth", "User Authorized: ${auth.currentUser?.email}")
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("Auth", "Credential Manager Sign-In Error: ${e.message}")
            false
        }
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
