package com.example.lostandfound.presentation.sign_in


data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? =null
)