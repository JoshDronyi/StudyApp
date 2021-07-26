package com.example.studyapp.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.viewmodel.QuestionsViewModel

@Composable
fun WeekQuestions(currentWeek: String, questions: List<Question>, navigation: (Question) -> Unit) {

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
            text = currentWeek,
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            modifier = Modifier
                .layoutId("weekText")
                .padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier
                .layoutId("questionList")
                .fillMaxSize()
        ) {
            items(questions) { question ->
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
                                navigation.invoke(question)
                            }
                            .fillMaxSize()) {
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

