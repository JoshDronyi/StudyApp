package com.example.studyapp

import androidx.compose.runtime.Composable
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension

@Composable
fun QuestionScreen() {
    val constraints = ConstraintSet {
        val questionNumber = createRefFor("questionsNumber")
        val questionText = createRefFor("questionText")
        val buttonAnswer1 = createRefFor("answer1")
        val buttonAnswer2 = createRefFor("answer2")
        val buttonAnswer3 = createRefFor("answer3")
        val buttonAnswer4 = createRefFor("answer4")

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
}