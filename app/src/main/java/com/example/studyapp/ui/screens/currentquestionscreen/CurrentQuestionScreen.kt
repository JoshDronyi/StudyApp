package com.example.studyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.QuestionStatus

@Composable
fun QuestionContent(
    question: Question,
    questionListViewModel: QuestionListViewModel,
    navigate: (Boolean) -> Unit
) {
    val constraints = ConstraintSet {
        val questionNumber = createRefFor("questionsNumber")
        val questionText = createRefFor("questionText")
        val buttonAnswer1 = createRefFor("answer0")
        val buttonAnswer2 = createRefFor("answer1")
        val buttonAnswer3 = createRefFor("answer2")
        val buttonAnswer4 = createRefFor("answer3")

        val guideline = createGuidelineFromTop(0.5f)

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
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }
        constrain(buttonAnswer2) {
            top.linkTo(guideline)
            start.linkTo(buttonAnswer1.end)
            end.linkTo(parent.end)
            bottom.linkTo(buttonAnswer4.top)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }
        constrain(buttonAnswer3) {
            top.linkTo(buttonAnswer1.bottom)
            start.linkTo(parent.start)
            end.linkTo(buttonAnswer4.start)
            bottom.linkTo(parent.bottom)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }
        constrain(buttonAnswer4) {
            top.linkTo(buttonAnswer2.bottom)
            start.linkTo(buttonAnswer3.end)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }

        createHorizontalChain(buttonAnswer1, buttonAnswer2, chainStyle = ChainStyle.Spread)
        createHorizontalChain(buttonAnswer3, buttonAnswer4, chainStyle = ChainStyle.Spread)
    }

    ConstraintLayout(
        constraintSet = constraints,
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .layoutId("questionsNumber")
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${question.questionNumber}",
                textAlign = TextAlign.Center,
                fontSize = 40.sp
            )
        }

        Card(
            elevation = 8.dp,
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .layoutId("questionText")
                .fillMaxWidth(.75f)
                .fillMaxHeight(.4f)
                .border(3.dp, MaterialTheme.colors.primaryVariant, RoundedCornerShape(25.dp)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = question.questionText,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            }
        }

        //Makes 4 Answer buttons.
        question.mixAnswers().forEachIndexed { index, answer ->
            AnswerButton(
                text = answer,
                "answer$index",
                question,
                questionListViewModel,
                navigate
            )
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    layoutId: String,
    question: Question,
    questionListViewModel: QuestionListViewModel,
    navigate: (Boolean) -> Unit
) {
    val backgroundColor = remember {
        mutableStateOf(Color.Unspecified)
    }
    Button(
        onClick = {
            if (text == question.correctAnswer) {
                questionListViewModel.updateQuestionStatus(question.apply {
                    questionStatus = QuestionStatus.CORRECT_ANSWER.ordinal
                })
            } else {
                questionListViewModel.updateQuestionStatus(question.apply {
                    questionStatus = QuestionStatus.WRONG_ANSWER.ordinal
                })
            }
            if (!questionListViewModel.getNewQuestion())
                navigate.invoke(false)

            questionListViewModel.getNewQuestion()

            navigate.invoke(true)
        },
        modifier = Modifier
            .layoutId(layoutId)
            .fillMaxHeight(.10f)
            .defaultMinSize(minWidth = 150.dp, minHeight = 50.dp)
            .fillMaxWidth(.3f),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor.value)
    ) {
        Text(text = text)
    }
}

