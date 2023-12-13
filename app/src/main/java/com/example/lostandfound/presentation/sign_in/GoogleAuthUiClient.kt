package com.example.lostandfound.presentation.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.lostandfound.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

/**
 * Handles one tap sign on with google accounts that have been used on the device
 * User data (username and uid[which is the same for our firestore database and authentication]),
 * is sent up to our firestore database
 */
class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
){
    //private auth
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender?{
        val result = try{
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException){
                throw e
            }
            null
        }
        return result?.pendingIntent?.intentSender
    }

    //Sign in with intent using google credentials
    suspend fun signInWithIntent(intent: Intent):SignInResult{
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val user = auth.signInWithCredential(googleCredential).await().user
        if (user != null) {
            updateUserData()
        }
        return try {

            val user = auth.signInWithCredential(googleCredential).await().user
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePicture = photoUrl?.toString(),
                        email = email
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception){ //error catching
            e.printStackTrace()
            if (e is CancellationException) {
                throw e
            }
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch (e:Exception){
            e.printStackTrace()
            if (e is CancellationException){
                throw e
            }
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePicture = photoUrl?.toString(),
            email = email?.toString()
        )
    }
    private fun buildSignInRequest(): BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    //user data update/add is done here instead of view model
    //better scope
    suspend fun updateUserData() {
        val user = getSignedInUser()
        if (user != null) {
            val userData = hashMapOf(
                DataToDB.UID to Firebase.auth.currentUser?.uid,
                DataToDB.USERNAME to Firebase.auth.currentUser?.displayName,
                DataToDB.EMAIL to Firebase.auth.currentUser?.email
            )

            try {
                Firebase.firestore.collection(DataToDB.USERS).document(user.userId).set(userData).await()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) {
                    throw e
                }
            }
        }
    }
}