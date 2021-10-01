package com.example.studyapp.ui.composables.screens.currentquestionscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.composables.sharedcomposables.MainTextCard
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.QuestionStatus

@Composable
fun QuestionScreen(
    navController: NavController = rememberNavController(),
    questionListViewModel: QuestionListViewModel = viewModel()
) {
    val currentQuestion by questionListViewModel.currentQuestion.observeAsState()

    currentQuestion?.let {
        CurrentQuestionContent(question = it) { text, question ->
            if (!checkButtonAnswer(text, question, questionListViewModel)) {
                navController.navigateUp()
            }
        }
    }
}


@Composable
fun CurrentQuestionContent(
    question: Question,
    onAnswerButtonClicked: (String, Question) -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        val mixedAnswers = question.mixAnswers()
        Column(
            modifier = Modifier
                .fillMaxWidth(.90f)
                .fillMaxHeight(.99f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${question.questionNumber}",
                    textAlign = TextAlign.Center,
                    fontSize = 40.sp
                )
            }
            val cornerRadius = 15
            MainTextCard(
                modifier = Modifier
                    .heightIn(min = 200.dp)
                    .fillMaxWidth(),
                text = question.questionText,
                shape = RoundedCornerShape(cornerRadius)
            )

            Card(
                Modifier
                    .fillMaxHeight(.60f),
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (i in 0..3 step 2) {
                        val answer1 = mixedAnswers[i]
                        val answer2 = mixedAnswers[i + 1]
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 70.dp, max = 100.dp)
                        ) {
                            AnswerButton(
                                text = answer1,
                                question = question,
                                modifier = Modifier,
                                onAnswerButtonClicked = onAnswerButtonClicked
                            )
                            AnswerButton(
                                text = answer2,
                                question = question,
                                modifier = Modifier,
                                onAnswerButtonClicked = onAnswerButtonClicked
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    question: Question,
    modifier: Modifier,
    onAnswerButtonClicked: (String, Question) -> Unit
) {

    Button(
        onClick = { onAnswerButtonClicked.invoke(text, question) },
        shape = RoundedCornerShape(5.dp),
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .requiredHeight(60.dp)
                .requiredWidth(100.dp)
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                softWrap = true
            )
        }
    }
}

private fun checkButtonAnswer(
    text: String,
    question: Question,
    questionListViewModel: QuestionListViewModel
): Boolean {
    if (text == question.correctAnswer) {
        questionListViewModel.updateQuestionStatus(question.apply {
            questionStatus = QuestionStatus.CORRECT_ANSWER.ordinal
        })
    } else {
        questionListViewModel.updateQuestionStatus(question.apply {
            questionStatus = QuestionStatus.WRONG_ANSWER.ordinal
        })
    }
    return questionListViewModel.getNewQuestion()
}

