package com.example.lostandfound.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lostandfound.LafViewModel
import com.example.lostandfound.R
import java.sql.Date
import java.sql.Time

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun lostPostCreationForm(
    VM: LafViewModel,
    cancelCreation: () -> Unit) {

    // Form values
    var item by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }
    var location by remember {
        mutableStateOf("")
    }
    var date by remember {
        mutableStateOf<Date?>(null)
    }
    var time by remember {
        mutableStateOf<Time?>(null)
    }
    var unknown by remember {
        mutableStateOf(false)
    }

    // UI constants
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val textFieldSize = (0.75*screenWidth).dp

    // Form
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){

        // Item TextField
        OutlinedTextField(value = item,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences),
            label = { Text(text = "Item") },
            placeholder = { Text(text = "Flask",color = Color.Gray) },
            onValueChange = {item = it},
            leadingIcon = { Icon(painterResource(id = R.drawable.baseline_devices_other_24), contentDescription = null) },
            modifier = Modifier.width(textFieldSize)
        )
        // Description TextField
        OutlinedTextField(
            value = description,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences),
            label = { Text(text = "Item Description") },
            maxLines = 3,
            onValueChange = {description = it},
            modifier = Modifier.width(textFieldSize)
        )
        // Location TextField
        OutlinedTextField(value = location,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences),
            label = { Text(text = "Item") },
            placeholder = { Text(text = "Flask",color = Color.Gray) },
            onValueChange = {item = it},
            leadingIcon = { Icon(painterResource(id = R.drawable.baseline_location_on_24), contentDescription = null) },
            modifier = Modifier.width(textFieldSize),
            enabled = !unknown
        )

        // Location unknown checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){
            Checkbox(checked = unknown, onCheckedChange = {unknown = it})
            Text("Unknown")
        }


        // Sumbit and Cancel buttons
        Row(modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround){
            Button(onClick = {cancelCreation()},
                modifier = Modifier.padding(16.dp)
            ){
                Text("Cancel")
            }
            Button(onClick = {VM.createLostPost(
                item = item,
                description = description,
                location = location,
                date = date,
                time = time
            )
                             cancelCreation()},
                modifier = Modifier.padding(16.dp)){
                Text("Post")
            }
        }
    }

}