package com.example.studyapp.ui.screens.weekquestionsscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.ui.sharedcomposables.ProgressBanner
import com.example.studyapp.ui.sharedcomposables.QuestionCard
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.QuestionStatus
import com.example.studyapp.util.formatWeekString

val TAG = "WEEK_QUESTIONS_SCREEN"

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
                    .padding(8.dp)
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