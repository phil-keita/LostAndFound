package com.example.lostandfound

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lostandfound.model.FoundPost
import com.example.lostandfound.model.LAFMessage
import com.example.lostandfound.model.Location
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
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.local.ReferenceSet
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import io.grpc.Context.Storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.CancellationException


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

    //Locations handler (Predefined locations saved in firebase that are used in dropdown selections)
    private val _locations = MutableLiveData<List<Map<String, Any>>>(emptyList())
    val locations: LiveData<List<Map<String, Any>>> = _locations
    init{
        getLocations()
    }
    /**
     * Gets all hard coded locations from firebase
     * For the dropdown location selection
     */
    private fun getLocations() {
        Firebase.firestore.collection(Location.LOCATIONS)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(Location.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val list = mutableListOf<Map<String, Any>>()

                if (value != null) {
                    for (doc in value) {
                        val data = doc.data
                        list.add(data)
                    }
                }
                updateLocations(list)
            }
    }
    /**
     * Updates the list of predefined locations
     */
    private fun updateLocations(list: MutableList<Map<String, Any>>) {
        _locations.value = list.asReversed()
    }

    // Found Page Handlers

    // Found Post
    private val _foundpost = MutableLiveData<Map<String, Any>>()
    val foundpost: LiveData<Map<String, Any>> = _foundpost
    // Found Posts
    private val _foundposts = MutableLiveData<List<Map<String, Any>>>(emptyList())
    val foundposts: LiveData<List<Map<String, Any>>> = _foundposts

    init {
        getFoundPosts()
    }

    /**
     * Updates the post during input
     */
    fun updateFoundPost(foundpost: Map<String, Any>){
        _foundpost.value = foundpost
    }
    fun updateFoundPostImage(imgBitmap: Bitmap): String{
        val storage = Firebase.storage
        val baos = ByteArrayOutputStream()
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        // Get a reference to the Firebase Storage root directory
        val storageRef = storage.reference
        //Creating unique image name
        var unique = UUID.randomUUID()
        // Storage path
        var imgPath = "images%2F$unique.jpg?alt=media"
        val imageRef = storageRef.child("images/$unique.jpg")
        // Uploading
        val uploadTask = imageRef.putBytes(data)
        //Success or failure
        uploadTask.addOnSuccessListener {
            // Handle successful upload
            Log.d("IMG Upload", "Succeed: ")
        }.addOnFailureListener {
            //TODO: Add failure toast
            Log.d("IMGUpload", "Failed")
        }
        return imgPath
    }

    /**
     * Handles creation of a new found post.
     * Send information of  new post to firebase
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createFoundPost(
    ){
        val foundpost: Map<String, Any> = _foundpost.value ?: throw IllegalArgumentException("post empty")
        if (foundpost.isNotEmpty()) {
            Firebase.firestore.collection(FoundPost.FOUNDPOSTS).document().set(
                hashMapOf(
                    FoundPost.ITEM to foundpost[FoundPost.ITEM],
                    FoundPost.POST_BY to Firebase.auth.currentUser?.uid,
                    FoundPost.SENT_ON to System.currentTimeMillis(),
                    FoundPost.ADDITIONAL_INFO to foundpost[FoundPost.ADDITIONAL_INFO],
                    FoundPost.LOCATION to foundpost[FoundPost.LOCATION],
                    FoundPost.LOCATION_NAME to foundpost[FoundPost.LOCATION_NAME],
                    FoundPost.IMG_SRC to foundpost[FoundPost.IMG_SRC]
                )
            ).addOnSuccessListener {
                _foundpost.value = emptyMap()
            }
        }
    }

    //gets the posts from firebase
    private fun getFoundPosts() {
        Firebase.firestore.collection(FoundPost.FOUNDPOSTS)
            .orderBy(FoundPost.SENT_ON)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(FoundPost.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val list = mutableListOf<Map<String, Any>>()

                if (value != null) {
                    for (doc in value) {
                        val data = doc.data
                        data[FoundPost.IS_CURRENT_USER] =
                            Firebase.auth.currentUser?.uid.toString() == data[FoundPost.POST_BY].toString()

                        list.add(data)
                    }
                }

                updateFoundPosts(list)
            }
    }

    //Update the list after getting the details from firestore
    private fun updateFoundPosts(list: MutableList<Map<String, Any>>) {
        _foundposts.value = list.asReversed()
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

//    /**
//     * Returns an image from firebase
//     */
//    fun getImage(ref: String):File{
//        val storage = Firebase.storage
//        val storageRef: StorageReference = storage.reference.child(ref)
//        var localFile = File.createTempFile("test",".jpeg")
////        var localFile = File("/res/drawable")
////        , File("/res/drawable")
//        storageRef.getFile(localFile)
//            .addOnSuccessListener {
//                Log.d("ImgDownload", "Download Success: ")
//            }
//            .addOnFailureListener{ exception ->
//                Log.e("FirebaseStorage", "Error downloading image", exception)
//            }
//        return localFile
//    }

}
