package com.example.lostandfound

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
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
}