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
import com.example.lostandfound.model.Conversation
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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import com.google.firebase.firestore.local.ReferenceSet
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import io.grpc.Context.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch


class LafViewModel: ViewModel(){

    // Conversation
    private val _conversation = MutableLiveData<Map<String, Any>>()
    val conversation: LiveData<Map<String, Any>> = _conversation
    // Conversations
    private val _conversations = MutableLiveData(emptyList<Map<String, Any>>().toMutableList())
    val conversations: LiveData<MutableList<Map<String, Any>>> = _conversations

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

    // LOCATION HANDLERS (Predefined locations saved in firebase that are used in dropdown selections)

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


    // FOUND PAGE HANDLERS

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
            Log.d("IMG Upload", "Success")
        }.addOnFailureListener {
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
    /**
     * Get all found posts
     */
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
    /**
     * Update list after pulling from firestore
     */
    private fun updateFoundPosts(list: MutableList<Map<String, Any>>) {
        _foundposts.value = list.asReversed()
    }


    // MESSAGE HANDLERS

    init {
//        getMessages()
//        getConversations()
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun addMessage() {
        val conversation: MutableMap<String, Any> = (_conversation.value ?: throw IllegalArgumentException("convo empty")).toMutableMap()
        val message: String = _message.value ?: throw IllegalArgumentException("message empty")
        if (message.isNotEmpty()) {
            var docRef = Firebase.firestore.collection(LAFMessage.MESSAGES).document()
            docRef.set(
                hashMapOf(
                    LAFMessage.MESSAGE to message,
                    LAFMessage.SENT_BY to Firebase.auth.currentUser?.uid,
                    LAFMessage.SENT_ON to System.currentTimeMillis()
                )
            )
                .addOnSuccessListener {
                    var messages = conversation[Conversation.MESSAGES] as MutableList<DocumentReference>
                    messages.add(docRef)
                    conversation[Conversation.MESSAGES] = messages
                    updateConversation(conversation)
                    createConversation()
                    _message.value = ""
            }
        }
    }

    //gets the message from firebase
    private fun getMessages(convoIndex: Int) {
        var convo: Map<String, Any>? = _conversations.value?.get(convoIndex)
        if (convo != null){
            Log.d("MessageGET", "Convo with index $convoIndex is not null")
            val docRef: List<DocumentReference> = convo[Conversation.MESSAGES] as List<DocumentReference>
            val list = emptyList<Map<String, Any>>().toMutableList()
            for(ref in docRef){
                ref
                    .addSnapshotListener{ value, e ->
                        if (e != null) {
                            Log.w(LAFMessage.TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (value != null){
                            Log.d("MessageGET", "Message document retrieved")
                            Log.d("MessageGET", "value: $value")
                            var message: Map<String, Any>? = value.data
                            if (message != null){
                                Log.d("MessageGET", "Message document not null. Adding to list")
                                list.add(message)
                                updateMessages(list)
                            }else{
                                Log.d("MessageGET", "Message was null :(")
                            }
                        }
                    }
            }

        }else{
            Log.d("MessageGET", "Convo with index $convoIndex is null")
        }
//
    }
    //Update the list after getting the details from firestore
    private fun updateMessages(list: MutableList<Map<String, Any>>) {
        _messages.value = list.asReversed()
        Log.d("MessageGET", "messages updated: "+_messages.value.toString())
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

    // Conversation handler

    init {
        getConversations()
    }

    /**
     * Updates the post during input
     */
    fun updateConversation(conversation: Map<String, Any>){
        _conversation.value = conversation
    }

    /**
     * Handles creation of a new found post.
     * Send information of  new post to firebase
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createConversation(
    ){
        val conversation: Map<String, Any> = _conversation.value ?: throw IllegalArgumentException("convo empty")
        if (conversation.isNotEmpty()) {
            var docRef = Firebase.firestore.collection(Conversation.CONVERSATIONS).document().set(
                hashMapOf(
                    Conversation.MESSAGES to conversation[Conversation.MESSAGES],
                    Conversation.USER1 to Firebase.auth.currentUser?.uid,
                    Conversation.USER2 to conversation[Conversation.USER2]
                )
            )
            docRef.addOnSuccessListener {
                var user2Ref = conversation[Conversation.USER2] as DocumentReference
                user2Ref.update(
                    DataToDB.CONVERSATIONS, FieldValue.arrayUnion(docRef)
                )
                var user1Ref = conversation[Conversation.USER1] as DocumentReference
                user1Ref.update(
                    DataToDB.CONVERSATIONS, FieldValue.arrayUnion(docRef)
                )
                _conversation.value = emptyMap()
            }
        }
    }

    //gets the posts from firebase
    private fun getConversations() {
        runBlocking {
            launch(Dispatchers.IO){
                Firebase.firestore.collection(DataToDB.USERS)
                    .document(Firebase.auth.currentUser?.uid.toString())
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Log.w(Conversation.TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (value != null) {
                            var list = mutableListOf<Map<String, Any>>()
                            var user_info: Map<String, Any>? = value.data
                            Log.d("Init Debug", user_info.toString())
                            Log.d("Init Debug", "value: " +value.toString())
                            if(user_info != null){
                                Log.d("Init Debug","Current user info loaded")
                                var convo_list = user_info[DataToDB.CONVERSATIONS]
                                if(convo_list !=null){
                                    val convoListAsDocRefs = convo_list as List<DocumentReference>
                                    Log.d("Init Debug",convoListAsDocRefs.toString())
                                    for(convo in convoListAsDocRefs){
                                        convo
                                            .addSnapshotListener{ value , e ->
                                                if (e != null) {
                                                    Log.w(FoundPost.TAG, "Listen failed.", e)
                                                    return@addSnapshotListener
                                                }
                                                if (value != null){
                                                    Log.d("Init Debug","Conversation Loaded")
                                                    var data = value.data
                                                    Log.d("Init Debug",data.toString())
                                                    list.add(data!!)
                                                    Log.d("Init Debug","list: "+list.toString())
                                                    updateConversations(list)
                                                }
                                            }

                                    }

                                }else{
                                    Log.d("Init Debug", "convo_list is empty")
                                }
                            }else{
                                Log.d("Init Debug","Current user info is empty")
                            }
                        }else{
                            Log.d("Init Debug","value is  empty")
                        }
                    }
            }
        }

    }

    private fun updateConversations(list: MutableList<Map<String, Any>>) {
        _conversations.value = list.asReversed()
        Log.d("Init Debug", "conversation updated init: "+_conversations.value.toString() + list.asReversed().toString())
        getMessages(0)
    }


    private val _username = MutableStateFlow("User does not exist")
    val username: StateFlow<String> = _username
    fun getUsername(docRef: DocumentReference): String{


//        runBlocking {
//            launch(Dispatchers.IO){
//                        val latch = CountDownLatch(1)
                docRef
                    .addSnapshotListener{value, e ->
                        if (e != null) {
                            Log.w(Conversation.TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (value != null) {
                            var userInfo: Map<String, Any>? = value.data
                            Log.d("GETUsername", "UserInfo retrieved value: ${value.toString()}")
                            Log.d("GETUsername", "UserInfo retrieved: $userInfo")
                            if(userInfo != null){
                                _username.value = userInfo[DataToDB.USERNAME] as String
                                Log.d("GETUsername", "Username actually retrieved: $username")
//                        latch.countDown()
                            }else{
                                Log.d("GETUsername", "Username is null:  $username")
                            }
                        }else{
                            Log.d("GETUsername", "user info is null:  $username")
                        }
                    }
//        latch.await()
                Log.d("GETUsername", "Username retrieved: $username")
//            }
//        }
        return _username.toString()
    }
}
