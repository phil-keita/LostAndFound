package com.example.lostandfound.screens

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun ShowMap(modifier: Modifier = Modifier, coordinates:LatLng = LatLng(41.155298, -80.079247)){

    // Show map with user location
    val cameraPosition = rememberCameraPositionState {
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
            title = "Found Here",
            snippet = "The item was found here",
        )
    }
}
