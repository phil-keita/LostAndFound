package com.example.lostandfound.mapping

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocScreenRequirePemission() {

    val context = LocalContext.current
    var location by remember{ mutableStateOf("Your Location") }

    // TODO: Create a permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), onResult = {isGranted ->
            if(isGranted){
                // Permission is granted, then update Location
                getCurrentLocation(context){lat, lgt ->
                    location = "Latitude: $lat, Longitude: $lgt"
                }
            }

        }
    )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Button(
                onClick = {
                    if(hasLocationPermission(context)){
                        // Permission already granted, update the location
                        getCurrentLocation(context){lat, lgt->
                            location = "Latitude: $lat, Longitude: $lgt"
                        }

                        //Same thing as
//                        getCurrentLocation(context, callback = {lat, lgt->
//                            location = "Latitude: $lat, Longitude: $lgt"
//                        })
                    }else{
                        // Request location permission
                        requestPermissionLauncher.launch(
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                }
            ){
                Text("Allow")
            }

            Button(onClick = { location = "" }) {
                Text("Clear")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(location)
        }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

private fun getCurrentLocation(context: Context, callback: (Double, Double)->Unit){
    // TODO:
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener{ location ->
        if(location!=null){
            callback(location.latitude, location.longitude)
        }
    }.addOnFailureListener{exception ->
        exception.printStackTrace()
    }


}