package com.example.lostandfound

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lostandfound.model.LAFMessage
import com.example.lostandfound.presentation.sign_in.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.lostandfound.presentation.sign_in.SignInState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.internal.concurrent.formatDuration
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


//data class UIState(
//
//)

class LafViewModel: ViewModel(){
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update{it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        )}
    }

    fun resetState(){
        _state.update{SignInState()}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createFoundPost(
        item: String,
        locationName: String,
        location: LatLng?,
        additionalInfo: String,
        imgBitmap: ImageBitmap?
    ){
        val timeAgo: String = formatDuration(System.currentTimeMillis())
        Log.d("Debug","item: ${item} was added $timeAgo ago.")
        Log.d("Debug","Latitude: ${location?.latitude ?: 0.0}")

    }

    fun createLostPost(
        item: String,
        description: String,
        location: String?,
        date: Date?,
        time: Time?,
    ){

    }

    init {
        getMessages()
    }

    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private var _messages = MutableLiveData(emptyList<Map<String, Any>>().toMutableList())
    val messages: LiveData<MutableList<Map<String, Any>>> = _messages

    //updates the message during input
    fun updateMessage(message: String) {
        _message.value = message
    }

    //sends message to firebase
    fun addMessage() {
        val message: String = _message.value ?: throw IllegalArgumentException("message empty")
        if (message.isNotEmpty()) {
            Firebase.firestore.collection(LAFMessage.MESSAGES).document().set(
                hashMapOf(
                    LAFMessage.MESSAGE to message,
                    LAFMessage.SENT_BY to Firebase.auth.currentUser?.uid,
                    LAFMessage.SENT_ON to System.currentTimeMillis()
                )
            ).addOnSuccessListener {
                _message.value = ""
            }
        }
    }

    //gets the message from firebase
    private fun getMessages() {
        Firebase.firestore.collection(LAFMessage.MESSAGES)
            .orderBy(LAFMessage.SENT_ON)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(LAFMessage.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val list = emptyList<Map<String, Any>>().toMutableList()

                if (value != null) {
                    for (doc in value) {
                        val data = doc.data
                        data[LAFMessage.IS_CURRENT_USER] =
                            Firebase.auth.currentUser?.uid.toString() == data[LAFMessage.SENT_BY].toString()

                        list.add(data)
                    }
                }

                updateMessages(list)
            }
    }
    //Update the list after getting the details from firestore
    private fun updateMessages(list: MutableList<Map<String, Any>>) {
        _messages.value = list.asReversed()
    }


}