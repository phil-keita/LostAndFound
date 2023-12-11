package com.example.lostandfound

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lostandfound.model.LAFMessage
import com.example.lostandfound.model.LostPost
import com.example.lostandfound.presentation.sign_in.DataToDB
import com.example.lostandfound.presentation.sign_in.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.lostandfound.presentation.sign_in.SignInState
import com.example.lostandfound.presentation.sign_in.UserData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.internal.concurrent.formatDuration
import java.sql.Date
import java.sql.Time
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException


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

    private val _user = MutableLiveData<Map<String, Any>>()
    val user: LiveData<Map<String, Any>> = _user

    private val _users = MutableLiveData<List<Map<String, Any>>>(emptyList())
    val users: LiveData<List<Map<String, Any>>> = _users

    init {
        getUserData()
    }

    //updates the post during input
    private fun updateUserData(users: MutableList<Map<String, Any>>) {
        _users.value = users
    }

    private fun getUserData() {
        Firebase.firestore.collection(DataToDB.USERS)
            .orderBy(DataToDB.UID)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(DataToDB.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val list = mutableListOf<Map<String, Any>>()

                if (value != null) {
                    for (doc in value) {
                        val data = doc.data
                        data[DataToDB.IS_CURRENT_USER] =
                            Firebase.auth.currentUser?.uid.toString() == data[DataToDB.UID].toString()

                        // If the current user's data is found, update _user.value
                        if (data[DataToDB.IS_CURRENT_USER] == true) {
                            _user.value = data
                        }

                        list.add(data)
                    }
                }

                updateUserData(list)
            }
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


    //Lost Page

    private val _lostpost = MutableLiveData<Map<String, Any>>()
    val lostpost: LiveData<Map<String, Any>> = _lostpost

    private val _lostposts = MutableLiveData<List<Map<String, Any>>>(emptyList())
    val lostposts: LiveData<List<Map<String, Any>>> = _lostposts

    init {
        getLostPosts()
    }

    //updates the post during input
    fun updateLostPost(lostpost: Map<String, Any>) {
        _lostpost.value = lostpost
    }

    //sends post to firebase
    fun addLostPost() {
        val lostpost: Map<String, Any> = _lostpost.value ?: throw IllegalArgumentException("post empty")
        if (lostpost.isNotEmpty()) {
            Firebase.firestore.collection(LostPost.LOSTPOSTS).document().set(
                hashMapOf(
                    LostPost.LOSTPOST to lostpost,
                    LostPost.POST_BY to Firebase.auth.currentUser?.uid,
                    LostPost.SENT_ON to System.currentTimeMillis(),
                    LostPost.ITEM to lostpost[LostPost.ITEM],
                    LostPost.DESCRIPTION to lostpost[LostPost.DESCRIPTION],
                    LostPost.LOCATION to lostpost[LostPost.LOCATION],
                    LostPost.TIMEFRAME to lostpost[LostPost.TIMEFRAME]
                )
            ).addOnSuccessListener {
                _lostpost.value = emptyMap()
            }
        }
    }




    //gets the posts from firebase
    private fun getLostPosts() {
        Firebase.firestore.collection(LostPost.LOSTPOSTS)
            .orderBy(LostPost.SENT_ON)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(LostPost.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val list = mutableListOf<Map<String, Any>>()

                if (value != null) {
                    for (doc in value) {
                        val data = doc.data
                        data[LostPost.IS_CURRENT_USER] =
                            Firebase.auth.currentUser?.uid.toString() == data[LostPost.POST_BY].toString()

                        list.add(data)
                    }
                }

                updateLostPosts(list)
            }
    }

    //Update the list after getting the details from firestore
    private fun updateLostPosts(list: MutableList<Map<String, Any>>) {
        _lostposts.value = list.asReversed()
    }

    suspend fun getUsernameByUid(uid: String): String? {
        val db = FirebaseFirestore.getInstance()
        var username: String? = null

        try {
            val docSnapshot = db.collection(DataToDB.USERS).document(uid).get().await()
            if (docSnapshot.exists()) {
                username = docSnapshot.getString(DataToDB.USERNAME)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) {
                throw e
            }
        }

        return username
    }

}
