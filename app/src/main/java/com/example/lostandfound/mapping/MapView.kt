package com.example.lostandfound.mapping

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState



@Composable
fun ShowMap(modifier: Modifier = Modifier, coordinates:LatLng = LatLng(41.155298, -80.079247)){
//
//    val VM: LocationViewModel = viewModel(factory = LocationViewModelFactory(context = LocalContext.current))
//    var locations = VM.getLocationData().observeAsState()
////    VM.startLocationUpdates()
//
//    val lat = locations.value?.latitude?.toDoubleOrNull()
//    val lgt = locations.value?.longitude?.toDoubleOrNull()
//
//    // Location to show
//    val userLoc = LatLng(41.155298, -80.079247)
//    if(lat !=null && lgt != null ){
//        val userLoc = LatLng(lat, lgt)
//    }
//
//    // Request access to position if not granted yet
////    LocScreenRequirePermission()
//
//    // User Location
//    val location = LatLng(userLoc.latitude,userLoc.longitude)

    // Show map with user location
    val cameraPosition = rememberCameraPositionState(){
        position = CameraPosition.fromLatLngZoom(coordinates, 17f)
    }

    GoogleMap(
        modifier = modifier
            .height(300.dp),
        cameraPositionState = cameraPosition
    ){
        Marker(
            state = rememberMarkerState(position = coordinates),
            draggable = true,
            title = "Grove City College",
            snippet = "Marker in GCC",
//            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
//            icon = bitmapDescriptorFromVector(LocalContext.current, R.drawable.babybowser, 100, 100)
        )
    }
}
//
//@SuppressLint("RememberReturnType")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LocScreenRequirePermission() {
//
//    val context = LocalContext.current
////    var location by remember{ mutableStateOf("Your Location") }
//    var userLoc by remember {mutableStateOf(LatLng(51.509865,-0.118092))}
//
//    // TODO: Create a permission launcher
//    val requestPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted ->
//            if(isGranted){
//                // Permission is granted, then update Location
//                getCurrentLocation(context){lat, lgt ->
////                    location = "Latitude: $lat, Longitude: $lgt"
//                    userLoc = LatLng(lat, lgt)
//                }
//            }
//
//        }
//    )
//
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally) {
//
//        Button(
//            onClick = {
//                if(hasLocationPermission(context)){
//                    // Permission already granted, update the location
//                    getCurrentLocation(context){lat, lgt->
////                        location = "Latitude: $lat, Longitude: $lgt"
//                        userLoc = LatLng(lat, lgt)
//                    }
//
//                    //Same thing as
////                        getCurrentLocation(context, callback = {lat, lgt->
////                            location = "Latitude: $lat, Longitude: $lgt"
////                        })
//                }else{
//                    // Request location permission
//                    requestPermissionLauncher.launch(
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    )
//                }
//            }
//        ){
//            Text("Allow")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("${userLoc.longitude}, ${userLoc.latitude}")
//
//        var cameraPosition = rememberCameraPositionState(){
//            position = CameraPosition.fromLatLngZoom(userLoc, 17f)
//        }
//        GoogleMap(
//            modifier = Modifier
//                .height(300.dp),
//            cameraPositionState = cameraPosition
//        ){
//            Marker(
//                state = rememberMarkerState(position = userLoc),
//                draggable = true,
//                title = "Grove City College",
//                snippet = "Marker in GCC",
////            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
////            icon = bitmapDescriptorFromVector(LocalContext.current, R.drawable.babybowser, 100, 100)
//            )
//        }
//    }
//}
//
//private fun hasLocationPermission(context: Context): Boolean {
//    return ContextCompat.checkSelfPermission(
//        context,
//        android.Manifest.permission.ACCESS_FINE_LOCATION
//    ) == PackageManager.PERMISSION_GRANTED
//}
//
//private fun getCurrentLocation(context: Context, callback: (Double, Double)->Unit){
//    // TODO:
//    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//    if (ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        // TODO: Consider calling
//        //    ActivityCompat#requestPermissions
//        // here to request the missing permissions, and then overriding
//        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//        //                                          int[] grantResults)
//        // to handle the case where the user grants the permission. See the documentation
//        // for ActivityCompat#requestPermissions for more details.
//        return
//    }
//    fusedLocationClient.lastLocation.addOnSuccessListener{ location ->
//        if(location!=null){
//            callback(location.latitude, location.longitude)
//        }
//    }.addOnFailureListener{exception ->
//        exception.printStackTrace()
//    }
//
//
//}
