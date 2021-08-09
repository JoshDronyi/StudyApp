package com.example.studyapp.ui.composables.sharedcomposables

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.util.Screens
import com.example.studyapp.util.formatWeekString

@Composable
fun QuestionCard(
    question: Question,
    backgroundColor: Color,
    onQuestionSelected: (Question) -> Unit
) {
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
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ProgressBanner(currentWeek: String, progress: StudentProgress) {
    Surface(
        elevation = 16.dp,
        shape = RoundedCornerShape(35.dp),
        color = MaterialTheme.colors.surface
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = formatWeekString(currentWeek),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
            with(progress) {
                Text(
                    text = "${correctAnswers}/${totalQuestions}",
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }

    }

}

@Composable
fun MainTextCard(cornerRadius: Int, text: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        elevation = 12.dp,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth(.80f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StudyTopAppBar(
    text: String,
    destination: NavDestination?,
    onMenuClick: (ButtonOptions) -> Unit
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colors.background,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (destination == null || destination.route == Screens.MainScreen.route) {
                Image(
                    imageVector = Icons.TwoTone.Menu,
                    contentDescription = "Toggle the drawer menu",
                    Modifier.clickable { onMenuClick.invoke(ButtonOptions.MENU) },
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            } else {
                Image(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Toggle the drawer menu",
                    Modifier.clickable { onMenuClick.invoke(ButtonOptions.BACK) },
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            }
            Text(
                text = text,
                Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

enum class ButtonOptions {
    MENU, BACK
}