package com.example.lostandfound.presentation.sign_in

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePicture: String?
)

//Object for our firestore db, dealing with user data.
//If we used user we could capture the UserData data class
//however there is no need to since we chose not to store the
//user's profile picture
object DataToDB{
    const val USERS = "users"
    const val USER = "user"
    const val TAG = "LAF"
    const val UID = "uid"
    const val USERNAME = "username"
    const val CONVERSATIONS = "conversation_list"
    const val IS_CURRENT_USER = "is_current_user"
}
