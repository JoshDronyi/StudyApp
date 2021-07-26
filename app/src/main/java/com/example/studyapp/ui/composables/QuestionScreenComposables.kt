package com.example.studyapp.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.viewmodel.QuestionsViewModel

@Composable
fun QuestionContent(question: Question, newQuestionChange: () -> Unit) {
    val constraints = ConstraintSet {
        val questionNumber = createRefFor("questionsNumber")
        val questionText = createRefFor("questionText")
        val buttonAnswer1 = createRefFor("answer0")
        val buttonAnswer2 = createRefFor("answer1")
        val buttonAnswer3 = createRefFor("answer2")
        val buttonAnswer4 = createRefFor("answer3")

        val guideline = createGuidelineFromTop(0.6f)

        constrain(questionNumber) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
        }

        constrain(questionText) {
            top.linkTo(questionNumber.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(guideline)
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
        }
        constrain(buttonAnswer1) {
            top.linkTo(guideline)
            start.linkTo(parent.start)
            end.linkTo(buttonAnswer2.start)
            bottom.linkTo(buttonAnswer3.top)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
        constrain(buttonAnswer2) {
            top.linkTo(guideline)
            start.linkTo(buttonAnswer1.end)
            end.linkTo(parent.end)
            bottom.linkTo(buttonAnswer4.top)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
        constrain(buttonAnswer3) {
            top.linkTo(buttonAnswer1.bottom)
            start.linkTo(parent.start)
            end.linkTo(buttonAnswer4.start)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
        constrain(buttonAnswer4) {
            top.linkTo(buttonAnswer2.bottom)
            start.linkTo(buttonAnswer3.end)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
    }

    ConstraintLayout(constraintSet = constraints, Modifier.fillMaxSize()) {
        Text(
            text = "Question ${question.questionNumber}",
            textAlign = TextAlign.Center,
            fontSize = 26.sp,
            modifier = Modifier.layoutId("questionsNumber")
        )
        Text(
            text = question.questionText,
            textAlign = TextAlign.Center,
            fontSize = 50.sp,
            modifier = Modifier
                .layoutId("questionText")
        )
        question.mixAnswers().forEachIndexed { index, answer ->
            AnswerButton(
                text = answer,
                layoutId = "answer$index",
                isCorrect = answer == question.correctAnswer,
                newQuestionChange
            )
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    layoutId: String,
    isCorrect: Boolean,
    newQuestionChange: () -> Unit
) {
    val backgroundColor = remember {
        mutableStateOf(Color.Unspecified)
    }
    Button(
        onClick = {
            if (isCorrect) backgroundColor.value = Color.Green else backgroundColor.value =
                Color.Red
            newQuestionChange.invoke()
        },
        modifier = Modifier
            .layoutId(layoutId)
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor.value)
    ) {
        Text(text = text)
    }
}
