package com.heremanikandan.scriptifyevents.auth

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.sheets.v4.SheetsScopes

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.slides.v1.SlidesScopes
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager: CredentialManager = CredentialManager.create(context)
    private val sharedPrefManager = SharedPrefManager(context)
    private  val GOOGLE_SIGN_IN_REQUEST_CODE by lazy { 0 }01
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


    fun checkUserExists(email: String, onResult: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        onResult(false) // New user
                    } else {
                        onResult(true) // Existing user
                    }
                } else {
                    onResult(false) // Handle error case
                }
            }
    }

    fun generateOTP(): String {
        return (100000..999999).random().toString() // 6-digit OTP
    }

//    fun storeOtpInFirebase(email: String, otp: String) {
//        val database = FirebaseDatabase.getInstance().reference
//        val otpRef = database.child("otp_verifications").child(email.replace(".", ","))
//
//        val otpData = mapOf(
//            "otp" to otp,
//            "timestamp" to System.currentTimeMillis()
//        )
//    println(otpData)
//        //otpRef.setValue(otpData)
//        otpRef.setValue(otpData)
//    }
    fun storeOtpInFirebase(email: String, otp: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val otpRef = database.child("otp_verifications").child(email.replace(".", ","))

        val otpData = mapOf(
            "otp" to otp,
            "timestamp" to System.currentTimeMillis()
        )

        otpRef.setValue(otpData)
            .addOnSuccessListener {
                // OTP stored successfully
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Failed to store OTP
                onFailure(exception)
            }
    }

    fun verifyOtp(email: String, enteredOtp: String, callback: (String?, String?) -> Unit) {
        val TAG = "OTP VERIFICATION"
        val database = FirebaseDatabase.getInstance().getReference("otp_verifications").child(email.replace(".", ","))

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val storedOtp = snapshot.child("otp").getValue(String::class.java) // Get the OTP field
                Log.d(TAG, "verifyOtp: $storedOtp -- entered otp: $enteredOtp")
                if (storedOtp != null && storedOtp == enteredOtp) {
                    Log.d(TAG, "OTP VERIFIED")
                    callback("OTP Verified Successfully", null)
                } else {
                    Log.d(TAG, "INVALID OTP")
                    callback(null, "Invalid OTP. Please try again.")
                }
            } else {
                Log.d(TAG, "No OTP found for this email")
                callback(null, "OTP not found. Please request a new OTP.")
            }
        }.addOnFailureListener { exception ->
            callback(null, "Error verifying OTP: ${exception.message}")
            Log.d(TAG, "ERROR VERIFYING OTP", exception)
        }
    }

    /*  TODO check the file permission for calendar and slides */
    fun requestGooglePermissions(email: String, navController: NavController) {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope(Scopes.DRIVE_FILE),
                Scope(Scopes.DRIVE_APPFOLDER),
                Scope(GmailScopes.GMAIL_READONLY),
                Scope(GmailScopes.GMAIL_COMPOSE),
                Scope(CalendarScopes.CALENDAR),
                Scope(SheetsScopes.SPREADSHEETS),
                Scope(SlidesScopes.PRESENTATIONS)
            )
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        (context as Activity).startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }


    fun checkEmailExists(inputEmail: String, onResult: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(inputEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    onResult(!signInMethods.isNullOrEmpty()) // Returns `true` if email exists, `false` if not
                } else {
                    onResult(false) // Consider treating errors as non-existent emails
                }
            }
    }

    fun verifyOtp(email: String, enteredOtp: String, onResult: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val otpRef = database.child("otp_verifications").child(email.replace(".", ","))

        otpRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storedOtp = snapshot.child("otp").value?.toString()
                val timestamp = snapshot.child("timestamp").value as? Long
                val currentTime = System.currentTimeMillis()

                if (storedOtp == enteredOtp && timestamp != null && (currentTime - timestamp) <= 2 * 60 * 1000) {
                    onResult(true) // OTP is correct and within 2-minute validity
                } else {
                    onResult(false) // Invalid or expired OTP
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false)
            }
        })
    }
}
