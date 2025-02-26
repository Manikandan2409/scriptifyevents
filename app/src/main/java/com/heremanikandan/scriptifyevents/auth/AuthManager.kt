package com.heremanikandan.scriptifyevents.auth

import android.content.Context
import android.util.Log
import androidx.credentials.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.*

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager: CredentialManager = CredentialManager.create(context)
    private val sharedPrefManager = SharedPrefManager(context)

    suspend fun signInWithGoogle(): Boolean {
        return try {
            val nonce = UUID.randomUUID().toString()
            val digest = MessageDigest.getInstance("SHA-256").digest(nonce.toByteArray())
            val hashedNonce = digest.joinToString("") { "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(com.heremanikandan.scriptifyevents.R.string.default_web_client_id))
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
                sharedPrefManager.saveUser(user?.displayName, user?.email)

                Log.d("Auth", "User Signed In: ${user?.email}")
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In Error: ${e.message}")
            false
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            sharedPrefManager.saveUser(auth.currentUser?.displayName, email)
            true
        } catch (e: Exception) {
            Log.e("Auth", "Email Sign-In Error: ${e.message}")
            false
        }
    }

    fun signOut() {
        auth.signOut()
        sharedPrefManager.clearUserData()
    }
}
