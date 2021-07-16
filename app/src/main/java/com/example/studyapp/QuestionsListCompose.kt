package com.example.studyapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.example.studyapp.model.Question
import com.example.studyapp.ui.theme.Shapes
import kotlin.math.round

@Preview(showBackground = true)
@Composable
fun PreviewWeekList() {
    WeekQuestions(week = "Test")
}


@Composable
fun WeekQuestions(week: String) {

    val questions = listOf<Question>(
        Question(
            1,
            "How to get a job?",
            "Be good at it",
            "Ask for the Gov",
            "I don't know",
            "They should just Hire me"
        ),
        Question(
            2,
            "How to get a job?",
            "Be good at it",
            "Ask for the Gov",
            "I don't know",
            "They should just Hire me"
        ),
        Question(
            3,
            "How to get a job?",
            "Be good at it",
            "Ask for the Gov",
            "I don't know",
            "They should just Hire me"
        ),
        Question(
            4,
            "How to get a job?",
            "Be good at it",
            "Ask for the Gov",
            "I don't know",
            "They should just Hire me"
        ),
        Question(
            5,
            "How to get a job?",
            "Be good at it",
            "Ask for the Gov",
            "I don't know",
            "They should just Hire me"
        ),
        Question(
            6,
            "How to get a job?",
            "Be good at it",
            "Ask for the Gov",
            "I don't know",
            "They should just Hire me"
        ),
        Question(
            7,
            "How to get a job?",
            "Be good at it",
            "Ask for the Gov",
            "I don't know",
            "They should just Hire me"
        ),
        Question(
            8,
            "How to get a job?",
            "Be good at it",
            "Ask for the Gov",
            "I don't know",
            "They should just Hire me"
        )
    )

    val constraints = ConstraintSet {
        val topText = createRefFor("weekText")
        val listOfQuestions = createRefFor("questionList")

        constrain(topText) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }

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
        Text(
            text = week,
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            modifier = Modifier
                .background(
                    Color.Blue,
                )
                .layoutId("weekText")
                .padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier
                .layoutId("questionList")
                .fillMaxSize()
        ) {
            items(questions) { question ->
                Card(Modifier.padding(6.dp).fillMaxWidth(), elevation = 10.dp,shape = Shapes.large) {
                    Text(
                        text = "Question number ${question.id}", modifier = Modifier.padding(
                            16.dp
                        )
                    )
                }
            }
        }
    }
}

