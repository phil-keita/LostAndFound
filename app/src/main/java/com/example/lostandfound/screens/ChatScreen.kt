package com.example.lostandfound.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lostandfound.presentation.sign_in.UserData
import com.example.lostandfound.ui.theme.md_theme_dark_onTertiaryContainer
import com.example.lostandfound.ui.theme.md_theme_dark_secondaryContainer
import com.example.lostandfound.ui.theme.md_theme_light_onPrimary
import com.example.lostandfound.ui.theme.md_theme_light_onPrimaryContainer
import com.example.lostandfound.ui.theme.md_theme_light_onSecondary
import com.example.lostandfound.ui.theme.md_theme_light_onSecondaryContainer
import com.example.lostandfound.ui.theme.md_theme_light_outline
import com.example.lostandfound.ui.theme.md_theme_light_secondary
import com.example.lostandfound.ui.theme.md_theme_light_secondaryContainer


//List of implementation
// UI
//--- bottom bar
//------text feild
//------send icon
//------keyboard
// Logic
// create messages and send to db
//
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(userData: UserData?) {
    var textEntered by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
                 TopAppBar(title = {
                     Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                         Text("Lost Locator Chat",
                             color = md_theme_light_secondary,
                             fontSize = 40.sp,
                             fontWeight = FontWeight.Bold,
                             letterSpacing = 2.sp
                         )
                     }
                 }, colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = md_theme_light_secondaryContainer) )
        },
        bottomBar = {
            BottomAppBar(containerColor = md_theme_light_secondaryContainer) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    OutlinedTextField(value = textEntered,
                        onValueChange = { textEntered = it },
                        label = { "" },
                        modifier = Modifier.padding(start = 15.dp,
                            bottom = 8.dp),
                        shape = RoundedCornerShape(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Send,
                            contentDescription = "send",
                            tint = md_theme_light_secondary,
                            modifier = Modifier.size(50.dp))
                    }
                }


            }
        }
    ) { innerpadding ->

    }
}