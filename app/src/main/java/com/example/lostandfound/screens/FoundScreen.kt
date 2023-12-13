package com.example.lostandfound.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.lostandfound.LafViewModel
import com.example.lostandfound.R
import com.example.lostandfound.data.DummyPosts
import com.example.lostandfound.model.FoundPost
import com.example.lostandfound.model.LostPost
import com.google.firebase.firestore.GeoPoint
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
fun FoundThread(VM : LafViewModel, navToCreate: () -> Unit){
    val foundposts: List<Map<String, Any>> by VM.foundposts.observeAsState(initial = emptyList())

        // Found Posts Cards
        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                items(foundposts){ post ->
                    showFoundPost(VM = VM, post = post)
                }
            }
            // Opens post creation screen
            Button(
                onClick = { navToCreate() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 30.dp)

            ) {
                Icon(painterResource(id = R.drawable.baseline_add_24), contentDescription = null)
                Text(text="Report")
            }
        }
//    }
}

@Composable
fun showFoundPost(VM : LafViewModel, post: Map<String, Any>){
    var showPostDetails by remember{mutableStateOf(false)}

    val currentTimeMillis = remember { mutableStateOf(System.currentTimeMillis()) }
    var username by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    LaunchedEffect(key1 = currentTimeMillis) {
        while (true) {
            delay(1000)  // Update every second
            currentTimeMillis.value = System.currentTimeMillis()
        }
    }
    //get username of the poster
    LaunchedEffect(key1 = post[LostPost.POST_BY]) {
        username = VM.getUsernameByUid(post[LostPost.POST_BY].toString())
    }

    LaunchedEffect(key1 = post[LostPost.POST_BY]) {
        email = VM.getEmailByUid(post[LostPost.POST_BY].toString())
    }
    //pull time of post and reflect and format
    val postTimeMillis = post[FoundPost.SENT_ON].toString().toLong()
    val timeDiffHours = (currentTimeMillis.value - postTimeMillis) / (1000 * 60 * 60)
    val timeDiffDays = timeDiffHours / 24
    val timeIndicator = when {
        timeDiffHours < 1 -> "recent"
        timeDiffHours in 1 until 24 -> if (timeDiffHours == 1L) "1 hour ago" else "$timeDiffHours hours ago"
        timeDiffDays == 1L -> "1 day ago"
        else -> "$timeDiffDays days ago"
    }

    if(showPostDetails){
        AlertDialog(
            onDismissRequest = { showPostDetails = false },
            title = {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center){
                    Text(
                        text="${post[FoundPost.ITEM]}",
                        textAlign = TextAlign.Center)
                }
            },
            text = {
                Column {
                    Row(modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(painterResource(id = R.drawable.baseline_location_on_24), contentDescription = null)
                        Text("${post[FoundPost.LOCATION_NAME]}")
                    }
                    val geo: GeoPoint = post[FoundPost.LOCATION] as GeoPoint
                    val location = LatLng(geo.latitude, geo.longitude)
                    val currentTimeMillis = remember { mutableStateOf(System.currentTimeMillis()) }

                    LaunchedEffect(key1 = currentTimeMillis) {
                        while (true) {
                            delay(1000)  // Update every second
                            currentTimeMillis.value = System.currentTimeMillis()
                        }
                    }

                    //pull time of post and reflect and format
                    val postTimeMillis = post[FoundPost.SENT_ON].toString().toLong()
                    val timeDiffHours = (currentTimeMillis.value - postTimeMillis) / (1000 * 60 * 60)
                    val timeDiffDays = timeDiffHours / 24
                    val timeIndicator = when {
                        timeDiffHours < 1 -> "recent"
                        timeDiffHours in 1 until 24 -> if (timeDiffHours == 1L) "1 hour ago" else "$timeDiffHours hours ago"
                        timeDiffDays == 1L -> "1 day ago"
                        else -> "$timeDiffDays days ago"
                    }
                    ShowMap(coordinates = location)
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = "$timeIndicator",
                            color = Color.Gray,
                            fontSize = 15.sp)
                        Text(text = username.toString(),
                            color = Color.Gray)
                    }
                    Text(text = "${post[FoundPost.ADDITIONAL_INFO]}",
                        modifier = Modifier.padding(16.dp))

                }
            },
            dismissButton = {
                TextButton(onClick = {showPostDetails = false}) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    VM.userFoundItem()
                    VM.nameOfPoster.value = username
                    VM.nameOfItem.value = post[FoundPost.ITEM].toString()
                    VM.posterEmail.value = email
                    showPostDetails = false
                    Toast.makeText(context, "Click on the Email Tab to claim your ${post[FoundPost.ITEM].toString()}!", Toast.LENGTH_LONG).show()
                }) {
                    Text(text = "Text ${username.toString()}")
                }
            })
    }
    Card(
        modifier = Modifier.padding(20.dp)
    ){
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)){
                // Item Image
                var baseurl = "https://firebasestorage.googleapis.com/v0/b/login-register-firebase-86e06.appspot.com/o/"
                var url = ""
                if(post[FoundPost.IMG_SRC] != null) {
                    url = "$baseurl${post[FoundPost.IMG_SRC]}"
                }
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop)

            }
            // Poster name and time
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Text(text = timeIndicator,
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier
                )
                Text(text = username?:"loading",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier
                )
            }
            // Item name
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Text(text= "${post[FoundPost.ITEM]}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

            }
            // Additional information
            Text(text = post[FoundPost.ADDITIONAL_INFO] as String,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp))
            // Location
            Row(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(painterResource(id = R.drawable.baseline_location_on_24), contentDescription = null)
                Text("${post[FoundPost.LOCATION_NAME]}")
            }
            // Claim button
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {showPostDetails = true},
                    modifier = Modifier.padding(8.dp)){
                    Text("Claim")
                }

            }
        }
    }
}