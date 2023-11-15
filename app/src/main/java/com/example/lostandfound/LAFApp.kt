package com.example.lostandfound

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow

sealed class NavScreens(val route: String) {
    object Login : NavScreens(route = "Login")
    object SignUp : NavScreens(route = "SignUp")
    object Find : NavScreens(route = "Find")
    object Found : NavScreens(route = "Found")
    object Chat : NavScreens(route = "Chat")
    object Profile : NavScreens(route = "Profile")
}


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
fun LAFApp(modifier: Modifier = Modifier) {
    //val navController: NavHostController = rememberNavController()
    //val backStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = backStackEntry?.destination?.route
    val items = listOf(
        "Find",
        "Found",
        "Chat",
        "Profile"
    )
    var state by remember { mutableStateOf(0) }

    Scaffold(
           
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(selectedTabIndex = state, modifier = Modifier.fillMaxSize()) {
                            items.forEachIndexed { index, title ->
                                Tab(
                                    selected = state == index,
                                    onClick = { state = index },
                                    text = { Text(text = title) }
                                )
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(text = "Test")
            }
        }


}
