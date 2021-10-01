package com.example.studyapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.example.studyapp.data.model.User
import com.example.studyapp.ui.composables.screens.currentquestionscreen.QuestionScreen
import com.example.studyapp.ui.composables.screens.homescreen.MyAppScreen
import com.example.studyapp.ui.composables.screens.loginScreen.LoginScreen
import com.example.studyapp.ui.composables.screens.weekquestionsscreen.QuestionListScreen
import com.example.studyapp.ui.composables.sharedcomposables.DrawerImage
import com.example.studyapp.ui.composables.sharedcomposables.DrawerItem
import com.example.studyapp.ui.composables.sharedcomposables.StudyTopAppBar
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.*
import com.example.studyapp.util.State.ApiState
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
                AppNavigator(userViewModel)
            }
        }
    }

    @ExperimentalCoilApi
    @Composable
    fun AppNavigator(
        userViewModel: UserViewModel = viewModel()
    ) {
        val scope = rememberCoroutineScope()
        val state = rememberScaffoldState()
        val navController = rememberNavController()
        val currentUserState by userViewModel.userLoginState.observeAsState()

        Scaffold(
            backgroundColor = MaterialTheme.colors.background,
            drawerContent = {
                NavDrawer(
                    screenState = currentUserState
                )
            },
            drawerElevation = 8.dp,
            drawerBackgroundColor = MaterialTheme.colors.surface,
            scaffoldState = state
        ) {
            StudyTopAppBar(
                text = this.localClassName,
                destination = Navigator.currentScreen.value,
                onMenuClick = {
                    handleButtonOptions(it, state, navController, scope)
                })
            Crossfade(targetState = Navigator.currentScreen) { screenState ->
                when (screenState.value) {
                    is Screens.LoginScreen -> {
                        LoginScreen(context = this, userViewModel)
                    }
                    is Screens.MainScreen -> {
                        MyAppScreen()
                    }
                    is Screens.WeekQuestionsScreen -> {
                        QuestionListScreen()
                    }
                    is Screens.QuestionScreen -> {
                        QuestionScreen()
                    }
                }

            }
        }
    }

    @ExperimentalCoilApi
    @Composable
    fun NavDrawer(
        screenState: ApiState<Any>?,
        navController: NavController = rememberNavController(),
        scope: CoroutineScope = rememberCoroutineScope(),
        state: ScaffoldState = rememberScaffoldState()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (screenState) {
                is ApiState.Success.UserApiSuccess -> {
                    with(screenState.data as User) {
                        DrawerImage(
                            imageID = R.drawable.ic_account_circle,
                            description = name ?: "Bob the builder",
                            imageUrl = photoUrl
                        )
                    }
                }
                else -> {
                    DrawerImage(
                        imageID = R.drawable.ic_account_circle,
                        description = "Image of account holder",
                        imageUrl = null
                    )

                }
            }

            Divider()
            DrawerItem(text = DrawerOptions.HOME) {
                closeDrawer(state, scope)
                handleDrawerSelection(it, navController)
            }
            Divider()
            DrawerItem(text = DrawerOptions.SCOREBOARD) {
                closeDrawer(state, scope)
                handleDrawerSelection(it, navController)
            }
        }
    }

    private fun handleDrawerSelection(option: DrawerOptions, navController: NavController) {
        when (option) {
            DrawerOptions.HOME -> {
                Log.e(TAG, "handleDrawerSelection: MainScreen Route: ${Screens.MainScreen.route}")
                if (navController.currentDestination?.route != Screens.MainScreen.route) {
                    navController.navigate(Screens.MainScreen.route)
                }
            }
            DrawerOptions.SCOREBOARD -> {
                Toast.makeText(
                    navController.context,
                    "Leader Board not yet created.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openDrawer(state: ScaffoldState, scope: CoroutineScope) =
        scope.launch(Dispatchers.Main) {
            state.drawerState.open()
        }

    private fun closeDrawer(state: ScaffoldState, scope: CoroutineScope) =
        scope.launch(Dispatchers.Main) {
            state.drawerState.close()
        }

    private fun handleButtonOptions(
        option: ButtonOptions,
        state: ScaffoldState,
        navController: NavController,
        scope: CoroutineScope
    ) {
        when (option) {
            ButtonOptions.BACK -> {
                navController.navigateUp()
            }
            ButtonOptions.MENU -> {
                when (state.drawerState.currentValue) {
                    DrawerValue.Closed -> {
                        openDrawer(state, scope)
                    }
                    DrawerValue.Open -> {
                        closeDrawer(state, scope)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ExampleAnimation(content: @Composable () -> Unit) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(initialAlpha = 0.3f),
            exit = fadeOut(),
            content = content,
            initiallyVisible = false
        )
    }

    @ExperimentalCoilApi
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppNavigator()
    }

}


