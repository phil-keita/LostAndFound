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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lostandfound.screens.Chat
import com.example.lostandfound.screens.FindThread
import com.example.lostandfound.screens.LostThread
import com.example.lostandfound.screens.Profile


sealed class NavScreens(val route: String) {
    object Login : NavScreens(route = "Login")
    object SignUp : NavScreens(route = "SignUp")
    object Find : NavScreens(route = "Find")
    object Lost : NavScreens(route = "Lost")
    object Chat : NavScreens(route = "Chat")
    object Profile : NavScreens(route = "Profile")
}


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
fun LAFApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val items = listOf(NavScreens.Find, NavScreens.Lost, NavScreens.Chat, NavScreens.Profile)
    var state by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            BottomAppBar {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                    TabRow(selectedTabIndex = state, modifier = Modifier.fillMaxSize()) {
                        items.forEachIndexed { index, screen ->
                            Tab(
                                selected = state == index,
                                onClick = {
                                    state = index
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    when(screen.route){
                                        NavScreens.Find.route -> Icon(Icons.Filled.Search, contentDescription = null)
                                        NavScreens.Lost.route -> Icon(Icons.Filled.LocationOn, contentDescription = null)
                                        NavScreens.Chat.route -> Icon(Icons.Filled.Email, contentDescription = null)
                                        NavScreens.Profile.route -> Icon(Icons.Filled.Person, contentDescription = null)
                                    }
                                },
                                text = { Text(text = screen.route) }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = NavScreens.Find.route, Modifier.padding(innerPadding)) {
            composable(NavScreens.Find.route) { FindThread()  }
            composable(NavScreens.Lost.route) { LostThread()}
            composable(NavScreens.Chat.route) { Chat() }
            composable(NavScreens.Profile.route) { Profile()}
        }
    }
}