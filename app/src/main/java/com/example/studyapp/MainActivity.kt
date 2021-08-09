package com.example.studyapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.data.model.ApiState
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.composables.screens.currentquestionscreen.CurrentQuestionContent
import com.example.studyapp.ui.composables.screens.homescreen.MyApp
import com.example.studyapp.ui.composables.screens.weekquestionsscreen.WeekQuestions
import com.example.studyapp.ui.composables.sharedcomposables.ButtonOptions
import com.example.studyapp.ui.composables.sharedcomposables.StudyTopAppBar
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.MainViewModel
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.*
import com.google.android.material.internal.NavigationMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val questionListViewModel: QuestionListViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
                AppNavigator()
            }
        }
    }

    @Composable
    fun NavDrawer() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Home")
            Divider()
            Text(text = "High Scores")
        }
    }

    @Composable
    fun AppNavigator() {
        val scope = rememberCoroutineScope()
        val state = rememberScaffoldState()
        val navController = rememberNavController()
        val currentDestination = remember {
            mutableStateOf(navController.currentDestination)
        }

        Scaffold(
            backgroundColor = MaterialTheme.colors.background,
            drawerContent = {
                NavDrawer()
            },
            drawerElevation = 8.dp,
            drawerBackgroundColor = MaterialTheme.colors.surface,
            scaffoldState = state
        ) {
            NavHost(navController = navController, startDestination = Screens.MainScreen.route) {
                composable(Screens.MainScreen.route) {
                    ExampleAnimation {
                        Column {
                            StudyTopAppBar(text = "Android Study App", currentDestination.value) {
                                handleButtonOptions(it, state, navController, scope)
                            }
                            MyAppScreen(navController)
                        }
                    }
                }
                composable(Screens.WeekQuestionsScreen.route) {
                    ExampleAnimation {
                        Column {
                            StudyTopAppBar(
                                text = "Question List",
                                destination = navController.currentDestination
                            ) {
                                handleButtonOptions(it, state, navController, scope)
                            }
                            QuestionListScreen(navController)
                        }
                    }
                }
                composable(Screens.QuestionScreen.route) {
                    ExampleAnimation {
                        Column {
                            StudyTopAppBar(
                                text = Screens.QuestionScreen.route,
                                navController.currentDestination
                            ) {
                                handleButtonOptions(it, state, navController, scope)
                            }
                            QuestionScreen(navController)
                        }
                    }
                }
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

    //Screen Composables
    @Composable
    fun MyAppScreen(navController: NavController) {
        val TAG = "My App Screen"
        val apiState by mainViewModel.apiState.observeAsState()

        Column {
            MyApp { week ->
                changeCurrentWeek(week, navController)
            }
        }

        Log.e(TAG, "Api state was $apiState")

        SideEffect {
            apiState?.let {
                checkApiState(it) { route ->
                    navController.navigate(route)
                }
            }
        }
    }

    @Composable
    fun QuestionListScreen(navController: NavController) {
        mainViewModel.changeState()
        val questions by questionListViewModel.questions.observeAsState()
        val progress by questionListViewModel.currentProgress.observeAsState()
        val currentWeek by questionListViewModel.currentWeek.observeAsState()

        Column {
            questions?.let {
                WeekQuestions(
                    questions = it,
                    progress = progress,
                    currentWeek = currentWeek
                ) { question ->
                    questionListViewModel.setCurrentQuestion(question)
                    navController.navigate(Screens.QuestionScreen.route)
                }
                questionListViewModel.setCurrentProgress(it.generateStudentProgress())
            }
        }
    }

    @Composable
    fun QuestionScreen(navController: NavController) {
        val currentQuestion by questionListViewModel.currentQuestion.observeAsState()

        Column {
            currentQuestion?.let {
                CurrentQuestionContent(question = it) { text, question ->
                    if (!checkButtonAnswer(text, question)) {
                        navController.navigateUp()
                    }
                }
            }
        }

    }


    //helpful variable. Should be raised.
    private val CHECK_TAG = "CheckApiState function"

    //Helper functions
    private fun checkApiState(
        questionListState: ApiState<List<Question>>,
        navigate: (String) -> Unit
    ) {
        with(questionListState) {
            when (this) {
                is ApiState.Success -> {
                    Log.e(CHECK_TAG, "MyAppScreen: Success: $this")
                    questionListViewModel.setQuestionList(this.questionList)
                    navigate.invoke(Screens.WeekQuestionsScreen.route)
                }
                is ApiState.Sleep, ApiState.Loading -> {
                    Log.e(CHECK_TAG, "STATE : ${this})")
                }
                else -> {
                    Log.e(CHECK_TAG, "STATE ERROR: Unrecognized Api State.")
                }
            }
        }

    }

    private fun changeCurrentWeek(week: String, navController: NavController) {
        when (week) {
            WK1, WK2, WK3, WK4, WK5, WK6 -> {
                questionListViewModel.currentWeek.value = week
                mainViewModel.getQuestions(week)
            }
            else -> {
                Toast.makeText(
                    navController.context,
                    "Please select questions from weeks 1-6",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkButtonAnswer(text: String, question: Question): Boolean {
        if (text == question.correctAnswer) {
            questionListViewModel.updateQuestionStatus(question.apply {
                questionStatus = QuestionStatus.CORRECT_ANSWER.ordinal
            })
        } else {
            questionListViewModel.updateQuestionStatus(question.apply {
                questionStatus = QuestionStatus.WRONG_ANSWER.ordinal
            })
        }
        return questionListViewModel.getNewQuestion()
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        val question = questionListViewModel.questions.value?.first()
        question?.let {
            CurrentQuestionContent(it) { string, question ->
                Log.e(
                    "PREVIEW",
                    "Got the preview loaded. String was $string question was $question"
                )
            }
        }
    }

}


