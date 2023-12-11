package com.example.lostandfound.presentation.sign_in

//Supporting data class for our google sign in
data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? =null
)