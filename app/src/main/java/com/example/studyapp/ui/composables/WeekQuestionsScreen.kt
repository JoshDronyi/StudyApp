package com.example.studyapp.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.viewmodel.QuestionsViewModel
import com.example.studyapp.util.QuestionStatus
import com.example.studyapp.util.Screens
import com.example.studyapp.util.formatWeekString


@Composable
fun WeekQuestions(
    navController: NavController,
    questions: List<Question>,
    questionsViewModel: QuestionsViewModel
) {

    val constraints = ConstraintSet {
        val topText = createRefFor("weekText")
        val listOfQuestions = createRefFor("questionList")
        val progressText = createRefFor("progressText")

        constrain(topText) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(progressText.start)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }

        constrain(progressText) {
            top.linkTo(parent.top)
            start.linkTo(topText.end)
            end.linkTo(parent.end)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }

        createHorizontalChain(topText, progressText, chainStyle = ChainStyle.SpreadInside)

        constrain(listOfQuestions) {
            top.linkTo(topText.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }

    }

    ConstraintLayout(constraintSet = constraints, Modifier.fillMaxSize()) {
        val progress by questionsViewModel.currentProgress.observeAsState()

        questionsViewModel.currentWeek.value?.let {
            Text(
                text = formatWeekString(it),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                modifier = Modifier
                    .layoutId("weekText")
                    .padding(16.dp)
            )
        }

        progress?.let {
            Text(
                text = "${it.correctAnswers}/${it.totalQuestions}",
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                modifier = Modifier
                    .layoutId("progressText")
                    .padding(16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .layoutId("questionList")
                .fillMaxSize()
        ) {

            items(questions.size) { questionIndex ->
                val question = questions[questionIndex]
                Log.e(
                    "WEEK QUESTIONS",
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
                Card(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        Modifier
                            .clickable {
                                questionsViewModel.setCurrentQuestion(question = question)
                                navController.navigate(Screens.QuestionScreen.route)
                            }
                            .fillMaxSize()
                            .background(backgroundColor)
                    ) {
                        Text(
                            text = "Question number ${question.questionNumber}",
                            modifier = Modifier.padding(
                                16.dp
                            )
                        )
                    }
                }
            }
        }
    }
}