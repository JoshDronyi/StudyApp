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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.data.model.ApiState
import com.example.studyapp.ui.composables.MyApp
import com.example.studyapp.ui.composables.QuestionContent
import com.example.studyapp.ui.composables.WeekQuestions
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.QuestionsViewModel
import com.example.studyapp.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val questionsViewModel: QuestionsViewModel by viewModels()

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
                    WeekQuestionList(navController)
                }
            }
            composable(
                Screens.QuestionScreen.route
            ) {
                ExampleAnimation {
                    QuestionDetail()
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
        val currentQuestion by questionsViewModel.apiState.observeAsState()
        MyApp { week ->
            when (week) {
                WK1 -> {
                    questionsViewModel.getQuestions(WK1)
                }
                WK2 -> {
                    questionsViewModel.getQuestions(WK2)
                }
                WK3 -> {
                    questionsViewModel.getQuestions(WK3)
                }
                WK4 -> {
                    questionsViewModel.getQuestions(WK4)
                }
                WK5 -> {
                    questionsViewModel.getQuestions(WK5)
                }
                WK6 -> {
                    questionsViewModel.getQuestions(WK6)
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

        currentQuestion?.let {
            when (it) {
                is ApiState.Success -> {
                    questionsViewModel.changeState(ApiState.Sleep)
                    questionsViewModel.setQuestions(it.data)
                    Log.e("JOSH", "Success loading questions.")
                    navController.navigate(Screens.WeekQuestionsScreen.route)
                }
                is ApiState.Sleep -> {
                    Log.e("STATE", it.toString())
                }
            }
        }
    }

    @Composable
    fun WeekQuestionList(navController: NavController) {
        questionsViewModel.changeState(ApiState.Sleep)
        val questions by questionsViewModel.questions.observeAsState()
        questions?.let {
            WeekQuestions(
                questions = it,
                currentWeek = questionsViewModel.currentWeek.value.toString()
            ) { question ->
                questionsViewModel.setCurrentQuestion(question = question)
                navController.navigate(Screens.QuestionScreen.route)
            }
        }
    }
    @Composable
    fun QuestionDetail() {
        val currentQuestion by questionsViewModel.currentQuestion.observeAsState()
        currentQuestion?.let {
            QuestionContent(question = it) {
                questionsViewModel.getNewQuestion()
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    //AppNavigator()
}