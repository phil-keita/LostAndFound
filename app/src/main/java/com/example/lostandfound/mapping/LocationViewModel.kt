package com.example.lostandfound.mapping

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng

class LocationViewModel(
     private val context: Context
): ViewModel() {

    private var isLocationPermissionGranted: Boolean by mutableStateOf(false)
    private var isLocationServiceStarted: Boolean by mutableStateOf(false)
    var currentLocation by mutableStateOf(LatLng(41.155298, -80.079247))

    // user location
    private val userLocation: LocationLiveData = LocationLiveData(context)

    fun updateLocationPermission(isGranted: Boolean){
        isLocationPermissionGranted = isGranted
    }

    fun startLocationUpdates(){
        if(isLocationPermissionGranted){
            if(!isLocationServiceStarted)
                userLocation.startLocationUpdates()
            else
                println("VM: Location Service has been started!")
        }else{
            println("VM: Location permission is not Granted!")
        }
    }

    fun getLocationData(): LocationLiveData {
         return userLocation
    }
}

 class LocationViewModelFactory(private val context: Context):
    ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T = LocationViewModel(context) as T
 }