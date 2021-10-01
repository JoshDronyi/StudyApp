package com.example.studyapp.ui.composables.screens.weekquestionsscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.ui.composables.sharedcomposables.ProgressBanner
import com.example.studyapp.ui.composables.sharedcomposables.QuestionCard
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.QuestionStatus
import com.example.studyapp.util.Screens
import com.example.studyapp.util.generateStudentProgress

const val TAG = "WEEK_QUESTIONS_SCREEN"

@Composable
fun QuestionListScreen(
    navController: NavController = rememberNavController(),
    questionListViewModel: QuestionListViewModel = viewModel()
) {
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
fun WeekQuestions(
    questions: List<Question>,
    progress: StudentProgress?,
    currentWeek: String?,
    onQuestionSelected: (Question) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .background(MaterialTheme.colors.background)
    ) {

        currentWeek?.let { week ->
            progress?.let { prog ->
                ProgressBanner(
                    currentWeek = week,
                    progress = prog
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

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
                items(questions.size) { questionIndex ->
                    val question = questions[questionIndex]
                    Log.e(
                        TAG,
                        "Week Questions  was \n ${question.questionText} \n ${question.correctAnswer} \n ${question.questionStatus}"
                    )

                    var backgroundColor by remember {
                        mutableStateOf(Color.White)
                    }
                    backgroundColor = when (question.questionStatus) {
                        QuestionStatus.CORRECT_ANSWER.ordinal -> Color.Green
                        QuestionStatus.WRONG_ANSWER.ordinal -> Color.Red
                        QuestionStatus.NOT_ANSWERED.ordinal -> Color.White
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
    }
}