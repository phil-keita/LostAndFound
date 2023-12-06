package com.example.lostandfound.screens

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.lostandfound.R
import com.example.lostandfound.data.DummyPosts
import com.example.lostandfound.model.FoundPost
import java.util.Objects

@Composable
fun FoundThread(){
    val postList: List<FoundPost> = DummyPosts().getDummyFoundPosts()
    var openForm by remember{mutableStateOf(false)}

    if(openForm){
        // Form for lost post creation
        foundPostCreationForm()
    }else{
        // Found Posts Cards
        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                items(postList){ post ->
                    showFoundPost(post = post)
                }
            }
            // Opens post creation screen
            Button(
                onClick = { openForm = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 30.dp)

            ) {
                Icon(painterResource(id = R.drawable.baseline_add_24), contentDescription = null)
                Text(text="Report")
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun foundPostCreationForm(){
//
//    // Form values
//    var item by remember {
//        mutableStateOf("")
//    }
//    var location by remember {
//        mutableStateOf("")
//    }
//    var otherLocation by remember {
//        mutableStateOf("")
//    }
//    var isOther by remember{
//        mutableStateOf(false)
//    }
//    var expanded by remember{
//        mutableStateOf(false)
//    }
//    var additionalInfo by remember{
//        mutableStateOf("")
//    }
//    var ShowMap by remember{
//        mutableStateOf(false)
//    }
//
//    // UI constants
//    val scrollState = rememberScrollState()
//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp
//    val screenHeight = configuration.screenHeightDp
//    val textFieldSize = (0.75*screenWidth).dp
//
//    // Form
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .padding(16.dp)
//        .verticalScroll(scrollState),
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.CenterHorizontally){
//        //Title
////        Text(
////            text = "What was found?",
////            fontSize = 30.sp,
////            fontWeight = FontWeight.Bold
////            )
//
//        // Image Upload
//        cameraButton()
//
//        // Item TextField
//        OutlinedTextField(value = item,
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Text,
//                imeAction = ImeAction.Next,
//                capitalization = KeyboardCapitalization.Sentences),
//            label = { Text(text = "Item")},
//            placeholder = {Text(text = "Flask",color = Color.Gray)},
//            onValueChange = {item = it},
//            leadingIcon = {Icon(painterResource(id = R.drawable.baseline_devices_other_24), contentDescription = null)},
//            modifier = Modifier.width(textFieldSize)
//        )
//        // Location Dropdown <--
//        ExposedDropdownMenuBox(
//            expanded = expanded,
//            onExpandedChange = {expanded  = it }
//        ){
//            OutlinedTextField(
//                value = location,
//                onValueChange = {},
//                readOnly = true,
//                label = {Text(text = "Location")},
//                leadingIcon = { Icon(painterResource(id = R.drawable.baseline_location_on_24),contentDescription = null)},
//                trailingIcon = {
//                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//                },
//                colors = ExposedDropdownMenuDefaults.textFieldColors(),
//                modifier = Modifier
//                    .menuAnchor()
//                    .width(textFieldSize))
//            // The dropdown itself
//            ExposedDropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }) {
//                DropdownMenuItem(
//                    text = {Text("HBL")},
//                    onClick ={
//                        location = "HBL"
//                        expanded = false
//                    })
//                Divider()
//                DropdownMenuItem(
//                    text = {Text("SHAL")},
//                    onClick ={
//                        location = "SHAL"
//                        expanded = false
//                    })
//                Divider()
//                DropdownMenuItem(
//                    text = {Text("STEM")},
//                    onClick ={
//                        location = "STEM"
//                        expanded = false
//                    })
//                Divider()
//                DropdownMenuItem(
//                    text = {Text("PLC")},
//                    onClick ={
//                        location = "PLC"
//                        expanded = false
//                    })
//                Divider()
//                DropdownMenuItem(
//                    text = {Text("PEW")},
//                    onClick ={
//                        location = "PEW"
//                        expanded = false
//                    })
//                Divider()
//                DropdownMenuItem(
//                    text = {Text("Other")},
//                    onClick ={
//                        location = "Other"
//                        expanded = false
//                        isOther = true
//                    })
//                Divider()
//            }
//        }
//        // Show Map Checkbox
//        Row(
//            modifier = Modifier
//            .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically){
//            Checkbox(checked = ShowMap, onCheckedChange = {ShowMap = it})
//            Text("Pin location on Map")
//        }
//        // Other location TextField
//        OutlinedTextField(value = otherLocation,
//            singleLine = true,
//            enabled = isOther,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Text,
//                imeAction = ImeAction.Done,
//                capitalization = KeyboardCapitalization.Sentences),
//            label = {Text("Other Location")},
//            placeholder = {Text(text="Hopeman Lawn",color = Color.Gray)},
//            onValueChange = {otherLocation = it},
//            modifier = Modifier.width(textFieldSize)
//        )
//        // Additional Information TextFIeld
//        OutlinedTextField(
//            value = additionalInfo,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Text,
//                imeAction = ImeAction.Done,
//                capitalization = KeyboardCapitalization.Sentences),
//            label = {Text(text = "Additional Information (Optional)")},
//            maxLines = 3,
//            onValueChange = {additionalInfo = it},
//            modifier = Modifier.width(textFieldSize)
//            )
//    }
//
//}
//
//@Composable
//fun cameraButton(){
//    // The accent color
//    val accentColor = MaterialTheme.colorScheme.primary
//
//    // Will store the image
//    var bitmap by remember {
//        mutableStateOf<Bitmap?>(null)
//    }
//    // Camera launcher
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicturePreview(),
//        onResult = {newImage ->
//            bitmap = newImage
//        })
//    // Permission Launcher
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ){isGranted ->
//        if(isGranted){
//            cameraLauncher.launch()
//        }
//    }
//    // Button
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ){
//        Box(modifier = Modifier
//            .size(300.dp, 250.dp)
//            .border(width = 2.dp, color = accentColor),
//            contentAlignment = Alignment.Center){
//            if (bitmap == null){
//                Icon(
//                    painterResource(id = R.drawable.baseline_image_24),
//                    contentDescription = null,
//                    modifier = Modifier.size(60.dp,50.dp),
//                    tint =  accentColor
//                )
//            }else{
//                Image(bitmap = bitmap!!.asImageBitmap(),
//                    contentDescription = null,
//                    modifier = Modifier.size(300.dp, 250.dp),
//                    contentScale = ContentScale.Crop)
//            }
////            bitmap?.let{
////                Image(bitmap = it.asImageBitmap(),
////                    contentDescription = null,
////                    contentScale = ContentScale.Crop)
////            }
//
//        }
//        val context = LocalContext.current
//        Button(
//            onClick = {
//                val permissionCheckResult =
//                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
//                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED){
//                    // Permission is already granted
//                    cameraLauncher.launch()
//                }else{
//                    //Launches permission request
//                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
//                }
//            }
//        ){
//            Icon(painterResource(id = R.drawable.baseline_camera_alt_24),
//                contentDescription = null)
//        }
//    }
//}


@Composable
fun showFoundPost(post: FoundPost){
    Card(
        modifier = Modifier.padding(20.dp)
    ){
        Column(){
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Text(text = "8h ago",
                    color = Color.Gray,
                    fontSize = 15.sp)
                Text(text = "Jon doe",
                    color = Color.Gray)
            }
            Image(painter = painterResource(id = R.drawable.earbuds), contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop)
            Row(modifier = Modifier.padding(8.dp)){
                Text(text= "Found: ${post.item}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Row(modifier = Modifier.padding(8.dp)){
                Text("Location: ${post.location}")
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {}){
                    Text("Claim")
                }
            }

        }
    }
}
@Preview
@Composable
fun FoundThreadPreview() {
    FoundThread()
}