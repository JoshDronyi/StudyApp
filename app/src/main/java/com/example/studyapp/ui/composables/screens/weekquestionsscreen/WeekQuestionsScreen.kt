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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.model.User
import com.example.studyapp.ui.composables.sharedcomposables.ProgressBanner
import com.example.studyapp.ui.composables.sharedcomposables.QuestionCard
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.Navigator
import com.example.studyapp.util.QuestionStatus
import com.example.studyapp.util.Screens
import com.example.studyapp.util.generateStudentProgress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

const val TAG = "WEEK_QUESTIONS_SCREEN"

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@Composable
fun QuestionListScreen(
    userViewModel: UserViewModel = viewModel(),
    questionListViewModel: QuestionListViewModel = viewModel()
) {
    val questions by questionListViewModel.questions.observeAsState()
    val progress by questionListViewModel.currentProgress.observeAsState()
    val currentWeek by questionListViewModel.currentWeek.observeAsState()
    val loginScreenState = userViewModel.loginScreenState.observeAsState()

    Column {
        questions?.let {
            WeekQuestions(
                questions = it,
                progress = progress,
                currentWeek = currentWeek,
                user = loginScreenState.value?.currentUser,
                onAddNewQuestionSelect = ::newQuestionSelect
            ) { question ->
                questionListViewModel.setCurrentQuestion(question)
                Navigator.navigateTo(Screens.QuestionScreen)
            }
            questionListViewModel.setCurrentProgress(it.generateStudentProgress())
        }
    }
}

private fun newQuestionSelect() {
    Navigator.navigateTo(Screens.NewQuestionScreen)
}

@Composable
fun WeekQuestions(
    questions: List<Question>,
    progress: StudentProgress?,
    currentWeek: String?,
    user: User? = User.newBlankInstance(),
    onAddNewQuestionSelect: () -> Unit,
    onQuestionSelected: (Question) -> Unit
) {
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
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        val context = LocalContext.current

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
                            backgroundColor = backgroundColor,
                            onQuestionSelected = onQuestionSelected
                        )
                    }
                }
            }

            user?.role?.let { role ->
                if (role == "student") {
                    FloatingActionButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "New Question Button hit",
                                Toast.LENGTH_SHORT
                            ).show()
                            onAddNewQuestionSelect.invoke()
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