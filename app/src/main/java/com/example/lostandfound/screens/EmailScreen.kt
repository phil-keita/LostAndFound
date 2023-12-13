package com.example.lostandfound.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lostandfound.LafViewModel
import com.example.lostandfound.ui.theme.md_theme_dark_secondary
import com.example.lostandfound.ui.theme.md_theme_light_onPrimary
import com.example.lostandfound.ui.theme.md_theme_light_onSecondary
import com.example.lostandfound.ui.theme.md_theme_light_primary
import com.example.lostandfound.ui.theme.md_theme_light_secondary
import com.example.lostandfound.ui.theme.md_theme_light_tertiaryContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Email(VM : LafViewModel) {
    val context = LocalContext.current
    if (VM.claimItem.value!!){
        var emailSubject by remember { mutableStateOf("") }
        var emailBody by remember { mutableStateOf("") }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Notify the holder of your item",
                    style = MaterialTheme.typography.titleLarge,
                    color = md_theme_light_primary,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = emailSubject,
                    onValueChange = { emailSubject = it },
                    label = { Text("Email Subject") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = md_theme_light_secondary,
                        unfocusedBorderColor = md_theme_light_onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                OutlinedTextField(
                    value = emailBody,
                    onValueChange = { emailBody = it },
                    label = { Text("Email Body") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = md_theme_light_secondary,
                        unfocusedBorderColor = md_theme_light_onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(100.dp),
                    maxLines = 3
                )

                Button(
                    onClick = { /* Handle submit action here */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Submit")
                }
            }

        }
    }else if(VM.foundItem.value!!){
        var emailSubject by remember { mutableStateOf("") }
        var emailBody by remember { mutableStateOf("") }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Notify ${VM.nameOfPoster.value} has your ${VM.nameOfItem.value}",
                    style = MaterialTheme.typography.titleLarge,
                    color = md_theme_light_primary,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = emailSubject,
                    onValueChange = { emailSubject = it },
                    label = { Text("Email Subject") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = md_theme_light_secondary,
                        unfocusedBorderColor = md_theme_light_onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                OutlinedTextField(
                    value = emailBody,
                    onValueChange = { emailBody = it },
                    label = { Text("Email Body") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = md_theme_light_secondary,
                        unfocusedBorderColor = md_theme_light_onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(100.dp),
                    maxLines = 3
                )

                Button(
                    onClick = {
                        val i = Intent(Intent.ACTION_SEND)

                        val emailAddress = arrayOf(VM.posterEmail.value)
                        i.putExtra(Intent.EXTRA_EMAIL,emailAddress)
                        i.putExtra(Intent.EXTRA_SUBJECT,emailSubject)
                        i.putExtra(Intent.EXTRA_TEXT,emailBody)

                        i.setType("message/rfc822")

                        context.startActivity(Intent.createChooser(i,"Choose an Email client : "))
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Submit")
                }
            }

        }
    }else{
        Box(
            modifier = Modifier.fillMaxSize()
                .background(md_theme_dark_secondary),
            contentAlignment = Alignment.Center

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Found Something? Report it!",
                    style = MaterialTheme.typography.titleLarge,
                    color = md_theme_light_primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Lost Something? Claim it!",
                    style = MaterialTheme.typography.titleLarge,
                    color = md_theme_light_primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}