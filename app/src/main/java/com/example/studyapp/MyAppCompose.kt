package com.example.studyapp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.ui.constants.LEFT
import com.example.studyapp.ui.constants.RIGHT
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.viewmodel.QuestionsViewModel

@Preview(showBackground = true)
@Composable
fun Preview() {
    //MyApp(navController = rememberNavController())
}

@Composable
fun MyApp(navController: NavController,viewModel : QuestionsViewModel) {
    StudyAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
                    .padding(top = 60.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(0.75f)
                        .border(2.dp, Color.Black, RoundedCornerShape(15)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Android Quiz",
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
                val modifier = Modifier.width(200.dp)
                Row {
                    ButtonColumn(
                        start = 1,
                        finish = 3,
                        modifier = modifier,
                        LEFT,
                        navController,
                        viewModel = viewModel
                    )
                    Divider(modifier = Modifier.width(3.dp))
                    ButtonColumn(
                        start = 4,
                        finish = 6,
                        modifier = modifier,
                        id = RIGHT,
                        navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


@Composable
fun ButtonColumn(
    start: Int,
    finish: Int,
    modifier: Modifier,
    id: String,
    navController: NavController,
    viewModel : QuestionsViewModel
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.layoutId(id)
    ) {
        for (i in start..finish) {
            WeekButton(weekNumber = i, padding = 8.dp, modifier = Modifier.layoutId(i)) {
                viewModel.currentWeek.postValue("Week $i")
                navController.navigate("weekQuestions")
            }
            Divider(
                modifier = modifier
                    .width(150.dp)
            )
        }
    }

}

@Composable
fun WeekButton(weekNumber: Int, padding: Dp, modifier: Modifier, clickReaction: () -> Unit) {
    Button(
        onClick = {
            clickReaction.invoke()
        },
        modifier = modifier
            .width(150.dp)
            .padding(padding)
    ) {
        Text(
            text = "Week $weekNumber",
            modifier = modifier
                .fillMaxWidth()
                .padding(padding),
            textAlign = TextAlign.Center
        )
    }

}