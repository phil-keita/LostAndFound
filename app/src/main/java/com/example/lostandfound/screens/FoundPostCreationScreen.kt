package com.example.lostandfound.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lostandfound.LafViewModel
import com.example.lostandfound.NavScreens
import com.example.lostandfound.R
import com.example.lostandfound.model.FoundPost
import com.example.lostandfound.model.LostPost
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.example.lostandfound.model.Location

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun foundPostCreationForm(
    VM: LafViewModel,
    cancelCreation: () -> Unit) {

    //List of all predefined locations
    val locations: List<Map<String, Any>> by VM.locations.observeAsState(initial = emptyList())

    // Form values
    var item by remember {
        mutableStateOf("")
    }
    var location by remember {
        mutableStateOf("")
    }
    var otherLocation by remember {
        mutableStateOf("")
    }
    var locationCoordinates by remember {
        mutableStateOf<LatLng>(LatLng(41.155298,-80.079247))
    }
    var predefinedCoordinates by remember {
        mutableStateOf<LatLng>(LatLng(41.155298,-80.079247))
    }
    var isOther by remember{
        mutableStateOf(false)
    }
    var expanded by remember{
        mutableStateOf(false)
    }
    var additionalInfo by remember{
        mutableStateOf("")
    }
    var showMap by remember{
        mutableStateOf(false)
    }
    var imgBitmap by remember{
        mutableStateOf<Bitmap?>(null)
    }

    // UI constants
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val textFieldSize = (0.75*screenWidth).dp

    // Launch Permission  request for Map View
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted ->
            if(isGranted){
                // Permission is granted, then update Location
                getCurrentLocation(context){lat, lgt ->
                    locationCoordinates = LatLng(lat,lgt)
                }
            }

        }
    )

    // Form
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){

        // Image Upload
        // The accent color
        val accentColor = MaterialTheme.colorScheme.primary

        // Camera launcher
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview(),
            onResult = { newImage ->
                imgBitmap = newImage
            })
        // Permission Launcher
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch()
            }
        }
        // Button
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp, 250.dp)
                    .border(width = 2.dp, color = accentColor),
                contentAlignment = Alignment.Center
            ) {
                if (imgBitmap == null) {
                    Icon(
                        painterResource(id = R.drawable.baseline_image_24),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp, 50.dp),
                        tint = accentColor
                    )
                } else {
                    Image(
                        bitmap = imgBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(300.dp, 250.dp),
                        contentScale = ContentScale.Crop
                    )
                }

            }
            Button(
                onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already granted
                        cameraLauncher.launch()
                    } else {
                        //Launches permission request
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Icon(
                    painterResource(id = R.drawable.baseline_camera_alt_24),
                    contentDescription = null
                )
            }
        }

        // Item TextField
        OutlinedTextField(value = item,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences),
            label = { Text(text = "Item") },
            placeholder = { Text(text = "Flask",color = Color.Gray) },
            onValueChange = {item = it},
            leadingIcon = { Icon(painterResource(id = R.drawable.baseline_devices_other_24), contentDescription = null) },
            modifier = Modifier.width(textFieldSize)
        )
        // Location Dropdown <--
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {expanded  = it }
        ){
            OutlinedTextField(
                value = location,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Location") },
                leadingIcon = { Icon(painterResource(id = R.drawable.baseline_location_on_24),contentDescription = null) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .width(textFieldSize))
            // The dropdown itself
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                for (loc in locations) {
                    DropdownMenuItem(
                        text = { Text("${loc[Location.LOCATION_NAME]}") },
                        onClick ={
                            location = loc[Location.LOCATION_NAME] as String
                            var coor = loc[Location.LOCATION] as GeoPoint
                            predefinedCoordinates = LatLng(coor.latitude, coor.longitude)
                            isOther = false
                            expanded = false
                        })
                    Divider()
                }
                DropdownMenuItem(
                    text = { Text("Other") },
                    onClick ={
                        location = "Other"
                        expanded = false
                        isOther = true
                    })
            }
        }

        // Show Map Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){
            Checkbox(checked = showMap, onCheckedChange = {
                showMap = it
            })
            Text("Pin location on Map (Click on location)")
        }

        // Camera position
        var cameraPosition = CameraPositionState(
            position = CameraPosition.fromLatLngZoom(locationCoordinates, 17f)
        )
        // Marker state (keeps track of where marker is)
        var markerState = MarkerState(position = locationCoordinates)

        // Map
        if(showMap && isOther){
            if(hasLocationPermission(context)){
                // Permission already granted, update the location
                getCurrentLocation(context) { lat, lgt ->
                    locationCoordinates = LatLng(lat,lgt)
                }
            }else{
                // Request location permission
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }

            // Updating values for map
            cameraPosition = CameraPositionState(
                position = CameraPosition.fromLatLngZoom(locationCoordinates, 17f)
            )
            markerState = MarkerState(position = locationCoordinates)

            // The actual map
            GoogleMap(
                modifier = Modifier
                    .height(300.dp),
                cameraPositionState = cameraPosition,
                onMapClick = {coordinates ->
                    cameraPosition.position = CameraPosition.fromLatLngZoom(coordinates, 17f)
                    markerState.position = coordinates
                }
            ){
                Marker(
                    state = markerState,
                    draggable = true,
                    title = "Found here",
                    snippet = "The item was found here",
                )

            }
        }
        else if(showMap){
            // Updating values for map
            cameraPosition = CameraPositionState(
                position = CameraPosition.fromLatLngZoom(predefinedCoordinates, 17f)
            )
            markerState = MarkerState(position = predefinedCoordinates)

            // The actual map
            GoogleMap(
                modifier = Modifier
                    .height(300.dp),
                cameraPositionState = cameraPosition,
                onMapClick = {coordinates ->
                    cameraPosition.position = CameraPosition.fromLatLngZoom(coordinates, 17f)
                    markerState.position = coordinates
                }
            ){
                Marker(
                    state = markerState,
                    draggable = true,
                    title = "Found here",
                    snippet = "The item was found here",
                )

            }
        }

        // Other location TextField
        OutlinedTextField(value = otherLocation,
            singleLine = true,
            enabled = isOther,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences),
            label = { Text("Other Location") },
            placeholder = { Text(text="Hopeman Lawn",color = Color.Gray) },
            onValueChange = {otherLocation = it},
            modifier = Modifier.width(textFieldSize)
        )
        // Additional Information TextFIeld
        OutlinedTextField(
            value = additionalInfo,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences),
            label = { Text(text = "Additional Information (Optional)") },
            maxLines = 3,
            onValueChange = {additionalInfo = it},
            modifier = Modifier.width(textFieldSize)
        )
        // Submit and Cancel buttons
        Row(modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround){
            Button(onClick = {cancelCreation()},
                modifier = Modifier.padding(16.dp)
            ){
                Text("Cancel")
            }
            Button(onClick = {
                if (isOther){
                    location = otherLocation
                }
                if (item.isNotEmpty() && additionalInfo.isNotEmpty() && imgBitmap != null) {
                    var imgRef = VM.updateFoundPostImage(imgBitmap!!)
                    VM.updateFoundPost(
                        hashMapOf(
                            FoundPost.ITEM to item,
                            FoundPost.ADDITIONAL_INFO to additionalInfo,
                            FoundPost.LOCATION to GeoPoint(markerState.position.latitude, markerState.position.longitude),
                            FoundPost.LOCATION_NAME to if (location != "") location else "Unknown",
                            FoundPost.IMG_SRC to imgRef
                        )
                    )
                    VM.createFoundPost()
                    cancelCreation()
                } else {
                    // Show an error message or a dialog here
                }
            },
                modifier = Modifier.padding(16.dp)){
                Text("Post")
            }
        }
    }

}

private fun getCurrentLocation(context: Context, callback: (Double, Double)->Unit){
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {

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

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}