package com.example.studyapp.ui.composables.screens.homescreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.ui.composables.sharedcomposables.MainTextCard
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.studyapp.util.State.ApiState

//helpful variable. Should be raised.
private const val CHECK_TAG = "CheckApiState function"
const val TAG = "HomeScreen"

@ExperimentalCoroutinesApi
@Composable
fun MyAppScreen(
    questionListViewModel: QuestionListViewModel = viewModel(),
    navController: NavController = rememberNavController()
) {
    val TAG = "My App Screen"
    val apiState by questionListViewModel.apiState.observeAsState()
    Log.e(TAG, "MyAppScreen: Drawing MyApp Screen. State is $apiState")

    Column {
        MyApp { week ->
            Log.e(TAG, "MyAppScreen: Changing current week to $week")
            changeCurrentWeek(week, questionListViewModel, navController)
        }
    }

    Log.e(TAG, "Api state was $apiState")

    SideEffect {
        Log.e(TAG, "MyAppScreen: Side Effect Launched!")
        apiState?.let {
            Log.e(TAG, "MyAppScreen: Checking state. $it")
            checkApiState(it, questionListViewModel)
        }
    }
}


@Composable
fun MyApp(navigation: (String) -> Unit) {
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize(.999f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            MainTextCard(
                text = "Android Quiz",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(.20f),
                shape = RoundedCornerShape(25.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            Card(
                elevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(.9f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.heightIn(min = 350.dp, max = 600.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WeekButton(
                            weekNumber = WK1,
                            modifier = Modifier
                                .layoutId(WK1)
                        ) { weekNumber -> navigation.invoke(weekNumber) }
                        WeekButton(
                            weekNumber = WK2,
                            modifier = Modifier
                                .layoutId(WK2)
                        ) { weekNumber -> navigation.invoke(weekNumber) }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WeekButton(
                            weekNumber = WK3,
                            modifier = Modifier
                                .layoutId(WK3)
                        ) { weekNumber -> navigation.invoke(weekNumber) }
                        WeekButton(
                            weekNumber = WK4,
                            modifier = Modifier
                                .layoutId(WK4)
                        ) { weekNumber -> navigation.invoke(weekNumber) }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WeekButton(
                            weekNumber = WK5,
                            modifier = Modifier
                                .layoutId(WK5)
                        ) { weekNumber -> navigation.invoke(weekNumber) }
                        WeekButton(
                            weekNumber = WK6,
                            modifier = Modifier
                                .layoutId(WK6)
                        ) { weekNumber -> navigation.invoke(weekNumber) }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyApp {
        Log.e("JOSH", "Preview log. $it")
    }
}


@Composable
fun WeekButton(
    weekNumber: String,
    modifier: Modifier,
    clickReaction: (String) -> Unit
) {

    Card(
        modifier = modifier
            .padding(16.dp)
            .width(120.dp)
            .height(60.dp)
            .border(2.dp, MaterialTheme.colors.primaryVariant, shape = RoundedCornerShape(20))
            .clickable {
                clickReaction.invoke(weekNumber)
            }
            .shadow(8.dp),
        shape = RoundedCornerShape(20),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = modifier.width(IntrinsicSize.Max),
                text = weekNumber,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colors.onSurface
            )
        }
    }

}

private fun checkApiState(
    questionListState: ApiState<Any>,
    questionListViewModel: QuestionListViewModel
) {
    Log.e(CHECK_TAG, "checkApiState: Checking the state $questionListState")
    with(questionListState) {
        when (this) {
            is ApiState.Success.QuestionApiSuccess -> {
                Log.e(CHECK_TAG, "MyAppScreen: Success: $this")
                questionListViewModel.setQuestionList(this.questionList)
                Navigator.navigateTo(Screens.WeekQuestionsScreen)
            }
            is ApiState.Sleep, is ApiState.Loading -> {
                Log.e(CHECK_TAG, "STATE : ${this})")
            }
            else -> {
                Log.e(CHECK_TAG, "STATE ERROR: Unrecognized Api State. $this")
            }
        }
    }

}


@ExperimentalCoroutinesApi
private fun changeCurrentWeek(
    week: String,
    questionListViewModel: QuestionListViewModel,
    navController: NavController
) {
    when (week) {
        WK1, WK2, WK3, WK4, WK5, WK6 -> {
            Log.e(
                TAG,
                "changeCurrentWeek: Changing currentWeek in questionListViewModel. getting questions in MainView Model. for week $week"
            )
            questionListViewModel.currentWeek.value = week
            questionListViewModel.getQuestions(week)
        }
        else -> {
            Toast.makeText(
                navController.context,
                "Please select questions from weeks 1-6",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}







