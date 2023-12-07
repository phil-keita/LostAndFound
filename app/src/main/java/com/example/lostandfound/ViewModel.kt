package com.example.lostandfound

import androidx.lifecycle.ViewModel
import com.example.lostandfound.presentation.sign_in.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.lostandfound.presentation.sign_in.SignInState
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


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
}