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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.data.model.ApiState
import com.example.studyapp.ui.screens.homescreen.MyApp
import com.example.studyapp.ui.screens.QuestionContent
import com.example.studyapp.ui.screens.weekquestionsscreen.WeekQuestions
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.CurrentQuestionViewModel
import com.example.studyapp.ui.viewmodel.MainViewModel
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val questionListViewModel: QuestionListViewModel by viewModels()
    private val currentQuestionViewModel: CurrentQuestionViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppNavigator()
                }
            }
        }
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Screens.MainScreen.route) {
            composable(Screens.MainScreen.route) {
                ExampleAnimation {
                    MyAppScreen(navController = navController)
                }
            }
            composable(Screens.WeekQuestionsScreen.route) {
                ExampleAnimation {
                    QuestionListScreen(navController)
                }
            }
            composable(
                Screens.QuestionScreen.route
            ) {
                ExampleAnimation {
                    QuestionScreen()
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

    @Composable
    fun MyAppScreen(navController: NavController) {
        val TAG = "My App Screen"
        val apiState by mainViewModel.apiState.observeAsState()
        MyApp { week ->
            Log.e(
                TAG, " On Click lambda, week was $week, " +
                        "comparison strings in the format of $WK1"
            )
            when (week) {
                WK1 -> {
                    mainViewModel.getQuestions(WK1)
                }
                WK2 -> {
                    mainViewModel.getQuestions(WK2)
                }
                WK3 -> {
                    mainViewModel.getQuestions(WK3)
                }
                WK4 -> {
                    mainViewModel.getQuestions(WK4)
                }
                WK5 -> {
                    mainViewModel.getQuestions(WK5)
                }
                WK6 -> {
                    mainViewModel.getQuestions(WK6)
                }
                else -> {
                    Toast
                        .makeText(
                            navController.context,
                            "Please select questions from weeks 1-6",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }
            }
        }

        Log.e(TAG, "Api state was $apiState")

        apiState?.let {
            when (it) {
                is ApiState.Success -> {
                    Log.e(TAG, "MyAppScreen: Success: $it")
                    questionListViewModel.setQuestionList(it.questionList)
                    navController.navigate(Screens.WeekQuestionsScreen.route)
                }
                is ApiState.Sleep -> {
                    Log.e(TAG, "STATE : ${it})")
                }
                is ApiState.Loading -> {
                    Log.e(TAG, "STATE : ${it})")
                }
                else -> {
                    Log.e(TAG, "STATE ERROR: Unrecognized Api State.")
                }
            }
        }
    }

    @Composable
    fun QuestionListScreen(navController: NavController) {
        // mainViewModel.changeState()
        val questions by questionListViewModel.questions.observeAsState()
        val progress by questionListViewModel.currentProgress.observeAsState()
        val currentWeek by questionListViewModel.currentWeek.observeAsState()

        questions?.let {
            WeekQuestions(
                questions = it,
                progress = progress,
                currentWeek = currentWeek
            ) { question ->
                currentQuestionViewModel.setCurrentQuestion(question)
                navController.navigate(Screens.QuestionScreen.route)
            }
            questionListViewModel.setCurrentProgress(it.generateStudentProgress())
        }
    }

    @Composable
    fun QuestionScreen() {
        val currentQuestion by currentQuestionViewModel.currentQuestion.observeAsState()
        currentQuestion?.let {
            QuestionContent(question = it, questionListViewModel) { processCompleted ->
                /*if (!processCompleted) {
                    onBackPressed()
                }*/
            }
        }
    }


    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        val question = questionListViewModel.questions.value?.first()
        question?.let {
            QuestionContent(it, questionListViewModel = questionListViewModel) {

            }
        }
    }
}