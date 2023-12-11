package com.example.lostandfound.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.lostandfound.LafViewModel
import com.example.lostandfound.model.LAFMessage
import com.example.lostandfound.presentation.sign_in.UserData
import com.example.lostandfound.ui.theme.md_theme_dark_onTertiaryContainer
import com.example.lostandfound.ui.theme.md_theme_dark_secondaryContainer
import com.example.lostandfound.ui.theme.md_theme_light_onPrimary
import com.example.lostandfound.ui.theme.md_theme_light_onPrimaryContainer
import com.example.lostandfound.ui.theme.md_theme_light_onSecondary
import com.example.lostandfound.ui.theme.md_theme_light_onSecondaryContainer
import com.example.lostandfound.ui.theme.md_theme_light_outline
import com.example.lostandfound.ui.theme.md_theme_light_primary
import com.example.lostandfound.ui.theme.md_theme_light_secondary
import com.example.lostandfound.ui.theme.md_theme_light_secondaryContainer
import com.example.lostandfound.ui.theme.md_theme_light_tertiary
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import com.example.lostandfound.ui.theme.md_theme_light_onTertiary
//import androidx.compose.foundation.layout.PaddingValues
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets


//Chat Screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(VM : LafViewModel) {
    val message: String by VM.message.observeAsState(initial = "")
    val messages: List<Map<String, Any>> by VM.messages.observeAsState(
        initial = emptyList<Map<String, Any>>().toMutableList()
    )
    Scaffold(//Top bar for style and bottom for messaging field
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Lost Locator Chat",
                            color = md_theme_light_secondary,//custom theme
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = md_theme_light_secondaryContainer)
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = md_theme_light_secondaryContainer) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { VM.updateMessage(it) },
                        label = { "" },
                        modifier = Modifier
                            .padding(start = 15.dp, bottom = 8.dp)
                            .fillMaxWidth(0.8f), // Fill 80% of the available width
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = false // Allow the text field to expand vertically but not-
                        //-horizontally
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    IconButton(onClick = { VM.addMessage() }) {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "send",
                            tint = md_theme_light_secondary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        }
    ) { innerpadding ->
        val listState = rememberLazyListState()
        LaunchedEffect(messages.size) {
            listState.animateScrollToItem(index = 0)
        }
        ProvideWindowInsets {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 0.85f, fill = true),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = innerpadding.calculateTopPadding() + 8.dp,
                        bottom = innerpadding.calculateBottomPadding() + 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        val isCurrentUser = message[LAFMessage.IS_CURRENT_USER] as Boolean
                        val messageText = message["message"] as String
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .background(
                                    color = if (isCurrentUser) md_theme_light_onTertiary else md_theme_light_onPrimary,
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentUser) md_theme_light_tertiary else md_theme_light_primary,
                            )
                        ) {
                            Text(
                                text = messageText,
                                textAlign =
                                if (isCurrentUser)
                                    TextAlign.End
                                else
                                    TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                color = if (!isCurrentUser) md_theme_light_onTertiary else md_theme_light_onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}