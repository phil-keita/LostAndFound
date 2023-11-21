package com.example.lostandfound

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lostandfound.presentation.sign_in.GoogleAuthUiClient
import com.example.lostandfound.screens.Chat
import com.example.lostandfound.screens.FindThread
import com.example.lostandfound.screens.FoundThread
import com.example.lostandfound.screens.Profile
import com.example.lostandfound.screens.SignInScreen
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch


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
fun LAFApp(modifier: Modifier = Modifier, context : Context) {
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    val navController = rememberNavController()
    val VM = viewModel<LafViewModel>()
    val vmState by VM.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val items = listOf(NavScreens.Find, NavScreens.Found, NavScreens.Chat, NavScreens.Profile)
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
                                        NavScreens.Found.route -> Icon(Icons.Filled.LocationOn, contentDescription = null)
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
        NavHost(navController, startDestination = NavScreens.SignUp.route, Modifier.padding(innerPadding)) {
            composable(NavScreens.SignUp.route){
                LaunchedEffect(key1 = Unit) {
                    if(googleAuthUiClient.getSignedInUser() != null) {
                        navController.navigate("profile")
                    }
                }
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if(result.resultCode == RESULT_OK) {
                            coroutineScope.launch {
                                val signInResult = googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                VM.onSignInResult(signInResult)
                            }
                        }
                    }
                )
                LaunchedEffect(key1 = vmState.isSignInSuccessful) {
                    if(vmState.isSignInSuccessful) {
                        Toast.makeText(
                            context,
                            "Sign in successful",
                            Toast.LENGTH_LONG
                        ).show()

                        navController.navigate(NavScreens.Profile.route)
                        VM.resetState()
                    }
                }
                SignInScreen(state = vmState,
                    onSignInClick = {
                        coroutineScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    }
                )
            }
            composable(NavScreens.Find.route) { FindThread()  }
            composable(NavScreens.Found.route) { FoundThread()}
            composable(NavScreens.Chat.route) { Chat() }
            composable(NavScreens.Profile.route) { Profile()}
        }
    }
}