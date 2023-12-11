package com.example.lostandfound.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun FoundPostDetails(post: Map<String, Any>){
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
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
                    Text(text = "Jon doe",
                        color = Color.Gray)
                }
                Text(text = "${post[FoundPost.ADDITIONAL_INFO]}",
                    modifier = Modifier.padding(16.dp))

            }
        },
        dismissButton = {
            TextButton(onClick = {}) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {}) {
                Text(text = "Text Jon")
            }
        })
}

@Composable
fun showFoundPost(VM : LafViewModel, post: Map<String, Any>){
    var showPostDetails by remember{mutableStateOf(false)}

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

    if(showPostDetails){
        FoundPostDetails(post)
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
                Image(painter = painterResource(id = R.drawable.earbuds), contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop)
                // Timestamp and username
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically){
                    Text(text = "timeIndicator",
                        color = Color.Gray,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .background(color = Color.White.copy(alpha = 0.5f),)
                            .shadow(4.dp))
                    Text(text = "Jon doe",
                        color = Color.Gray,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .background(color = Color.White.copy(alpha = 0.5f))
                            .shadow(4.dp))
                }
            }
            // Item name
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Text(text= "${post[FoundPost.ITEM]}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                // Location
                Row(modifier = Modifier
                    .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painterResource(id = R.drawable.baseline_location_on_24), contentDescription = null)
                    Text("${post[FoundPost.LOCATION_NAME]}")
                }
            }
            // Additional information
            Text(text = "The Earbuds were by the last booth on the right side of the main floor.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal= 16.dp))
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
//@Preview
//@Composable
//fun FoundThreadPreview() {
//    FoundThread()
//}