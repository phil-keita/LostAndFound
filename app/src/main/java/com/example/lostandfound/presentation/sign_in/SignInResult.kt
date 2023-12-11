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

object DataToDB{
    const val USERS = "users"
    const val USER = "user"
    const val TAG = "LAF"
    const val UID = "uid"
    const val USERNAME = "username"
    const val IS_CURRENT_USER = "is_current_user"
}
