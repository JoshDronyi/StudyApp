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
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.composables.screens.currentquestionscreen.CurrentQuestionContent
import com.example.studyapp.ui.composables.screens.homescreen.MyApp
import com.example.studyapp.ui.composables.screens.weekquestionsscreen.WeekQuestions
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.MainViewModel
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val mainViewModel: MainViewModel by viewModels()
    val questionListViewModel: QuestionListViewModel by viewModels()

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
                    MyAppScreen(navController)
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
                    QuestionScreen(navController)
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
            when (week) {
                WK1, WK2, WK3, WK4, WK5, WK6 -> {
                    questionListViewModel.currentWeek.value = week
                    mainViewModel.getQuestions(week)
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
                is ApiState.Sleep, ApiState.Loading -> {
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
        mainViewModel.changeState()
        val questions by questionListViewModel.questions.observeAsState()
        val progress by questionListViewModel.currentProgress.observeAsState()
        val currentWeek by questionListViewModel.currentWeek.observeAsState()

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

    @Composable
    fun QuestionScreen(navController: NavController) {
        val currentQuestion by questionListViewModel.currentQuestion.observeAsState()
        currentQuestion?.let {
            CurrentQuestionContent(question = it) { text, question ->
                if (!checkButtonAnswer(text, question)) {
                    navController.navigateUp()
                }
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


