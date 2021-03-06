package com.example.studyapp.ui.composables.screens.weekquestionsscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.model.User
import com.example.studyapp.ui.composables.sharedcomposables.ProgressBanner
import com.example.studyapp.ui.composables.sharedcomposables.QuestionCard
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.Events.QuestionListScreenEvents
import com.example.studyapp.util.Events.QuestionListScreenEvents.*
import com.example.studyapp.util.QuestionStatus
import com.example.studyapp.util.Screens
import com.example.studyapp.util.generateStudentProgress
import com.example.studyapp.util.navigateToScreen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

const val TAG = "WEEK_QUESTIONS_SCREEN"

@DelicateCoroutinesApi
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@Composable
fun QuestionListScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    questionListViewModel: QuestionListViewModel = viewModel(),
) {
    val questionListContract by questionListViewModel.questionListContract.collectAsState()
    val loginContract by userViewModel.loginScreenContract.collectAsState()

    Column {
        with(questionListContract.screenState) {
            WeekQuestions(
                questions = questionList,
                progress = progress,
                currentWeek = currentWeek,
                user = loginContract.screenState.currentUser
            ) { event ->
                when (event) {
                    is OnAddNewQuestion -> {
                        navController.navigateToScreen(Screens.NewQuestionScreen)
                    }
                    is OnNewWeekSelected -> {
                        // use the direction object given to u in order to go
                        // to the next week or the previous week
                    }
                    is OnQuestionSelected -> {
                        questionListViewModel.setCurrentQuestion(event.question)
                        navController.navigateToScreen(Screens.QuestionScreen)
                    }
                }
            }


            questionListViewModel.setCurrentProgress(
                questionList.generateStudentProgress()
            )
        }
    }
}

@Composable
fun WeekQuestions(
    questions: List<Question>,
    progress: StudentProgress?,
    currentWeek: String?,
    user: User? = User.newBlankInstance(),
    onWeekQuestionEventOccurred: (QuestionListScreenEvents) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(24.dp)
            .background(MaterialTheme.colors.background)
    ) {

        currentWeek?.let { week ->
            progress?.let { progress ->
                ProgressBanner(
                    currentWeek = week,
                    progress = progress
                ) { buttonOption ->
                    onWeekQuestionEventOccurred.invoke(
                        OnNewWeekSelected(buttonOption)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        Box {
            Surface(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colors.surface
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    state = rememberLazyListState()
                ) {
                    items(questions.count()) { questionIndex ->
                        val question = questions[questionIndex]
                        question.questionNumber = questionIndex.toString()
                        Log.e(
                            TAG,
                            "Week Questions  was \n Text:${question.questionText} \n ans:${question.correctAnswer} \n count: ${questions.count()}"
                        )

                        var backgroundColor by remember {
                            mutableStateOf(Color.White)
                        }
                        backgroundColor = when (question.questionStatus) {
                            QuestionStatus.CORRECT_ANSWER.ordinal.toString() -> Color.Green
                            QuestionStatus.WRONG_ANSWER.ordinal.toString() -> Color.Red
                            QuestionStatus.NOT_ANSWERED.ordinal.toString() -> Color.White
                            else -> {
                                Color.White
                            }
                        }
                        QuestionCard(
                            question = question,
                            backgroundColor = backgroundColor
                        ) { selectedQuestion ->
                            onWeekQuestionEventOccurred.invoke(
                                OnQuestionSelected(selectedQuestion)
                            )
                        }
                    }
                }
            }

            user?.role?.let { role ->
                if (role == "admin") {
                    FloatingActionButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "New Question Button hit",
                                Toast.LENGTH_SHORT
                            ).show()
                            onWeekQuestionEventOccurred.invoke(
                                OnAddNewQuestion
                            )
                        },
                        modifier = Modifier
                            .zIndex(.8f)
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = "Floating button to add new questions"
                        )
                    }
                }
            }

        }

    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun QuestionListPreview() {
    /*  WeekQuestions(questions = listOf(), progress = null, currentWeek = null) {

      }*/
}