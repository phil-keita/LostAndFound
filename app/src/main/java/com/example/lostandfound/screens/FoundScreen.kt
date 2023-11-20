package com.example.lostandfound.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lostandfound.R
import com.example.lostandfound.data.DummyPosts
import com.example.lostandfound.model.FoundPost

@Composable
fun FoundThread(){
    val postList: List<FoundPost> = DummyPosts().getDummyFoundPosts()
    LazyColumn(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
//            postList.forEach(){ post ->
//                showPost(post = post)
//            }
        items(postList){ post ->
            showFoundPost(post = post)
        }
    }
}


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
                modifier = Modifier.fillMaxWidth().height(100.dp),
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