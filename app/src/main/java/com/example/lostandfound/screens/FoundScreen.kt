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
import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.shadow
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
import com.example.lostandfound.mapping.ShowMap
import com.example.lostandfound.model.FoundPost
import java.util.Objects

@Composable
fun FoundThread(){
    val postList: List<FoundPost> = DummyPosts().getDummyFoundPosts()
    var openForm by remember{mutableStateOf(false)}


    if(openForm){
        // Form for lost post creation
        foundPostCreationForm()
    } else{
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

@Composable
fun FoundPostDetails(post: FoundPost){
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center){
                Text(
                    text="${post.item}",
                    textAlign = TextAlign.Center)
            }
            },
        text = {
            Column(){
                Row(modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painterResource(id = R.drawable.baseline_location_on_24), contentDescription = null)
                    Text("${post.location}")
                }
                ShowMap()
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
                Text(text = "The Earbuds were by the last booth on the right side of the main floor.",
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
fun showFoundPost(post: FoundPost){
    var showPostDetails by remember{mutableStateOf(false)}

    if(showPostDetails){
        FoundPostDetails(post)
    }
    Card(
        modifier = Modifier.padding(20.dp)
    ){
        Column(){
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
                    Text(text = "found 8h ago",
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
                Text(text= "${post.item}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                // Location
                Row(modifier = Modifier
                    .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painterResource(id = R.drawable.baseline_location_on_24), contentDescription = null)
                    Text("${post.location}")
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
@Preview
@Composable
fun FoundThreadPreview() {
    FoundThread()
}