package com.example.lostandfound.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lostandfound.data.DummyPosts
import com.example.lostandfound.model.FindPost
import com.example.lostandfound.model.LostPost

@Composable
fun LostThread(){
    val postList: List<LostPost> = DummyPosts().getDummyLostPosts()
    LazyColumn(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
//            postList.forEach(){ post ->
//                showPost(post = post)
//            }
        items(postList){ post ->
            showLostPost(post = post)
        }
    }
}


@Composable
fun showLostPost(post: LostPost){
    Card(
        modifier = Modifier.padding(20.dp)
    ){
        Column(){
//            Image(painte)
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
        }
    }
}
@Preview
@Composable
fun LostThreadPreview() {
    LostThread()
}