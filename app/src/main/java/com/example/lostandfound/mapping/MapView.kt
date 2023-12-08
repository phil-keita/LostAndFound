package com.example.lostandfound.mapping

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun ShowMap(modifier: Modifier = Modifier): LatLng{

    val VM: LocationViewModel = viewModel(factory = LocationViewModelFactory(context = LocalContext.current))
    var locations = VM.getLocationData().observeAsState()

    val lat = locations.value?.latitude?.toDoubleOrNull()
    val lgt = locations.value?.longitude?.toDoubleOrNull()

    // Location to show
    val userLoc = LatLng(41.155298, -80.079247)
    if(lat !=null && lgt != null ){
        val userLoc = LatLng(lat, lgt)
    }
    val cameraPosition = rememberCameraPositionState(){
        position = CameraPosition.fromLatLngZoom(userLoc, 15f)
    }

    GoogleMap(
        modifier = modifier
            .height(300.dp),
        cameraPositionState = cameraPosition
    ){
        Marker(
            state = rememberMarkerState(position = userLoc),
            draggable = true,
            title = "Grove City College",
            snippet = "Marker in GCC",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
//            icon = bitmapDescriptorFromVector(LocalContext.current, R.drawable.babybowser, 100, 100)
        )
    }

    return userLoc

}

//fun bitmapDescriptorFromVector(context: Context, iconResourceId: Int, width: Int? = null, height: Int? = null): BitmapDescriptor? {
//
//    val drawable = ContextCompat.getDrawable(context, iconResourceId) ?: return null
//    val drawWidth = width ?: drawable.intrinsicWidth
//    val drawHeight = height ?: drawable.intrinsicHeight
//    drawable.setBounds(0, 0, drawWidth, drawHeight)
//    val bm = Bitmap.createBitmap(
//        drawWidth,
//        drawHeight,
//        Bitmap.Config.ARGB_8888
//    )
//
//    val canvas = android.graphics.Canvas(bm)
//    drawable.draw(canvas)
//    return BitmapDescriptorFactory.fromBitmap(bm)
//}