package com.example.studyapp.ui.sharedcomposables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studyapp.data.model.Question

@Composable
fun QuestionCard(question:Question,backgroundColor: Color, onQuestionSelected :(Question) -> Unit){
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
                    onQuestionSelected.invoke(question)
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