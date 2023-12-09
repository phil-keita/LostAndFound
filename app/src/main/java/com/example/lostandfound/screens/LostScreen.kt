package com.example.lostandfound.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lostandfound.LafViewModel
import com.example.lostandfound.R
import com.example.lostandfound.data.DummyPosts
import com.example.lostandfound.model.LostPost




@Composable
fun LostThread(VM : LafViewModel, navToCreate: () -> Unit){
    val lostpost: Map<String, Any> by VM.lostpost.observeAsState(initial = emptyMap())
    val lostposts: List<Map<String, Any>> by VM.lostposts.observeAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            items(lostposts){ post ->
                showLostPost(post = post)
            }
        }
        Button(
            onClick = { navToCreate() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 30.dp)

        ) {
            Icon(painterResource(id = R.drawable.baseline_edit_note_24),
                contentDescription = null)
            Text(text="Post")
        }
    }

}

@Composable
fun showLostPost(post: Map<String, Any>){
    Card(
        modifier = Modifier.padding(20.dp)
    ){
        Column(){
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Text(text = post[LostPost.SENT_ON].toString(),
                    color = Color.Gray,
                    fontSize = 15.sp)
                Text(text = post[LostPost.POST_BY].toString(),
                    color = Color.Gray)
            }
            Row(modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.Center){
                Text(text= post[LostPost.ITEM].toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
            Row(modifier = Modifier.padding(8.dp)){
                Text(text= post[LostPost.DESCRIPTION].toString())
            }
            Row(modifier = Modifier.padding(8.dp)){
                Column(){
                    Text("Location: ${post[LostPost.LOCATION]}",
                        fontSize = 15.sp)
                    Text("Time frame: ${post[LostPost.TIMEFRAME]}",
                        fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {}){
                    Text("Found it!")
                }
            }
        }
    }
}






















//@Composable
//fun LostThread(VM : LafViewModel, navToCreate: () -> Unit){
//    val postList: List<LostPost> = DummyPosts().getDummyLostPosts()
//
//    Box(modifier = Modifier.fillMaxSize()){
//        LazyColumn(modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally){
////            postList.forEach(){ post ->
////                showPost(post = post)
////            }
//            items(postList){ post ->
//                showLostPost(post = post)
//            }
//        }
//        Button(
//            onClick = { navToCreate() },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp),
//            elevation = ButtonDefaults.buttonElevation(defaultElevation = 30.dp)
//
//        ) {
//            Icon(painterResource(id = R.drawable.baseline_edit_note_24),
//                contentDescription = null)
//            Text(text="Post")
//        }
//    }
//
//}
//
//@Composable
//fun showLostPost(post: LostPost){
//    Card(
//        modifier = Modifier.padding(20.dp)
//    ){
//        Column(){
//            Row(modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically){
//                Text(text = "8h ago",
//                    color = Color.Gray,
//                    fontSize = 15.sp)
//                Text(text = "Jon doe",
//                    color = Color.Gray)
//            }
//            Row(modifier = Modifier.padding(8.dp),
//                horizontalArrangement = Arrangement.Center){
//                Text(text= post.item,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//            Row(modifier = Modifier.padding(8.dp)){
//                Text(text= post.description)
//            }
//            Row(modifier = Modifier.padding(8.dp)){
//                Column(){
//                    Text("Location: ${post.location}",
//                        fontSize = 15.sp)
//                    Text("Time frame: ${post.time}",
//                        fontSize = 15.sp)
//                }
//                Spacer(modifier = Modifier.weight(1f))
//                Button(onClick = {}){
//                    Text("Found it!")
//                }
//            }
//        }
//    }
//}
//@Preview
//@Composable
//fun FindThreadPreview() {
//    LostThread()
//}
