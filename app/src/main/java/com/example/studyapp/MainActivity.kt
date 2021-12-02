package com.example.studyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import com.example.studyapp.ui.composables.screens.currentquestionscreen.QuestionScreen
import com.example.studyapp.ui.composables.screens.homescreen.MyAppScreen
import com.example.studyapp.ui.composables.screens.loginscreen.LoginScreen
import com.example.studyapp.ui.composables.screens.settingscreens.ProfileScreen
import com.example.studyapp.ui.composables.screens.weekquestionsscreen.NewQuestionScreen
import com.example.studyapp.ui.composables.screens.weekquestionsscreen.QuestionListScreen
import com.example.studyapp.ui.composables.sharedcomposables.NavDrawer
import com.example.studyapp.ui.composables.sharedcomposables.StudyTopAppBar
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val tag = "MainActivity"

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
        userViewModel: UserViewModel = viewModel(),
        questionListVM: QuestionListViewModel = viewModel()
    ) {
        val scope = rememberCoroutineScope()
        val state = rememberScaffoldState()
        val currentUserState by userViewModel.userLoginState.observeAsState()

        Scaffold(
            backgroundColor = MaterialTheme.colors.background,
            drawerContent = {
                NavDrawer(
                    screenState = currentUserState
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
                    destination = Navigator.currentScreen.value,
                    state = state,
                    onMenuClick = { options, isOpen ->
                        onMenuClick(options, questionListVM, isOpen) {
                            when (state.drawerState.currentValue) {
                                DrawerValue.Closed -> {
                                    scope.launch {
                                        state.drawerState.close()
                                    }
                                }
                                DrawerValue.Open -> {
                                    scope.launch {
                                        state.drawerState.open()
                                    }
                                }
                            }
                        }
                    }
                )
            }
        ) {
            Crossfade(targetState = Navigator.currentScreen) { screenState ->
                when (screenState.value) {
                    is Screens.LoginScreen -> {
                        LoginScreen(userViewModel)
                    }
                    is Screens.MainScreen -> {
                        MyAppScreen(
                            questionListViewModel = questionListVM
                        )
                    }
                    is Screens.WeekQuestionsScreen -> {
                        QuestionListScreen(userViewModel)
                    }
                    is Screens.QuestionScreen -> {
                        QuestionScreen()
                    }
                    is Screens.ProfileScreen -> {
                        ProfileScreen()
                    }
                    is Screens.NewQuestionScreen -> {
                        NewQuestionScreen { week, question ->
                            Log.e(tag, "AppNavigator: Question was $question for week $week")
                            questionListVM.addNewQuestion(week,question)
                        }
                    }
                    else -> {
                        Log.e(
                            tag,
                            "AppNavigator: into the Crossfade else branch ${screenState.value}"
                        )
                    }
                }

            }
        }
    }

    private fun onMenuClick(
        option: ButtonOptions,
        questionListVM: QuestionListViewModel,
        isOpen: Boolean,
        onDrawerToggle: (isOpen: Boolean) -> Unit
    ) {
        when (option) {
            ButtonOptions.BACK -> {
                questionListVM.stopQuestions()
                Navigator.navigateUp()
            }
            ButtonOptions.MENU -> {
                onDrawerToggle.invoke(isOpen)
            }
            ButtonOptions.SETTINGS -> {
                Navigator.navigateTo(Screens.SettingsScreen)
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


