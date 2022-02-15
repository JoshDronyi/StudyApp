package com.example.studyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavigatorState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.example.studyapp.ui.composables.screens.currentquestionscreen.QuestionScreen
import com.example.studyapp.ui.composables.screens.homescreen.HomeScreen
import com.example.studyapp.ui.composables.screens.loginscreen.LoginScreen
import com.example.studyapp.ui.composables.screens.settingscreens.ProfileScreen
import com.example.studyapp.ui.composables.screens.weekquestionsscreen.NewQuestionScreen
import com.example.studyapp.ui.composables.screens.weekquestionsscreen.QuestionListScreen
import com.example.studyapp.ui.composables.sharedcomposables.NavDrawer
import com.example.studyapp.ui.composables.sharedcomposables.StudyTopAppBar
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.ButtonOptions
import com.example.studyapp.util.Screens.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ExperimentalCoilApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val questionListViewModel: QuestionListViewModel by viewModels()
    private val tag = "MainActivity"

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StudyAppTheme {
                AppNavigator(userViewModel, questionListViewModel)
            }
        }
    }

    @Composable
    fun AppNavigator(
        userViewModel: UserViewModel = viewModel(),
        questionListVM: QuestionListViewModel = viewModel()
    ) {
        val scope = rememberCoroutineScope()
        val state = rememberScaffoldState()
        navController = rememberNavController()


        val loginContract by userViewModel.loginScreenContract.collectAsState()

        Scaffold(
            backgroundColor = MaterialTheme.colors.background,
            drawerContent = {
                NavDrawer(
                    navController,
                    user = loginContract.screenState.currentUser
                ) { shouldClose ->
                    scope.launch {
                        if (shouldClose) {
                            state.drawerState.close()
                        } else {
                            state.drawerState.open()
                        }
                    }
                }
            },
            drawerElevation = 8.dp,
            drawerBackgroundColor = MaterialTheme.colors.surface,
            scaffoldState = state,
            topBar = {
                StudyTopAppBar(
                    text = this@MainActivity.tag,
                    navController = navController,
                    state = state,
                    onMenuClick = { options, isOpen ->
                        onMenuClick(options, isOpen) {
                            when (state.drawerState.currentValue) {
                                DrawerValue.Closed -> {
                                    scope.launch {
                                        state.drawerState.open()
                                    }
                                }
                                DrawerValue.Open -> {
                                    scope.launch {
                                        state.drawerState.close()
                                    }
                                }
                            }
                        }
                    }
                )
            }
        ) {
            Log.e(tag, "AppNavigator: Current screen is ${navController.currentDestination?.route}")
            NavHost(navController = navController, startDestination = LoginScreen.route) {
                composable(LoginScreen.route) { LoginScreen(navController, userViewModel) }
                composable(HomeScreen.route) { HomeScreen(navController, questionListViewModel) }
                composable(QuestionListScreen.route) {
                    QuestionListScreen(
                        navController,
                        userViewModel,
                        questionListViewModel
                    )
                }
                composable(QuestionScreen.route) {
                    QuestionScreen(
                        navController,
                        questionListViewModel
                    )
                }
                composable(ProfileScreen.route) { ProfileScreen(userViewModel) }
                composable(NewQuestionScreen.route) {
                    NewQuestionScreen(navController) { week, question ->
                        Log.e(tag, "AppNavigator: Question was $question for week $week")
                        questionListVM.addNewQuestion(week, question)
                        navController.navigateUp()
                    }
                }
            }
        }
    }

    private fun onMenuClick(
        option: ButtonOptions,
        isOpen: Boolean,
        onDrawerToggle: (isOpen: Boolean) -> Unit
    ) {
        when (option) {
            ButtonOptions.BACK -> {
                navController.navigateUp()
            }
            ButtonOptions.MENU -> {
                onDrawerToggle.invoke(isOpen)
            }
            ButtonOptions.SETTINGS -> {
                navController.navigate(SettingsScreen.route) {
                    launchSingleTop = true
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
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        AppNavigator()
    }

}


