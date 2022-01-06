package com.example.studyapp.ui.composables.screens.homescreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.ui.composables.sharedcomposables.MainTextCard
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.*
import com.example.studyapp.util.State.ApiState
import kotlinx.coroutines.ExperimentalCoroutinesApi

//helpful variable. Should be raised.
private const val CHECK_TAG = "CheckApiState function"
const val TAG = "HomeScreen"

@ExperimentalCoroutinesApi
@Composable
fun MyAppScreen(
    questionListViewModel: QuestionListViewModel = viewModel()
) {
    val TAG = "My App Screen"
    val apiState by questionListViewModel.apiState.observeAsState()
    Log.e(TAG, "MyAppScreen: Drawing MyApp Screen. State is $apiState")
    val context = LocalContext.current

    Column {
        MyApp { week ->
            Log.e(TAG, "MyAppScreen: Changing current week to $week")
            when (week) {
                WK1, WK2, WK3, WK4, WK5, WK6 -> {
                    Log.e(
                        TAG,
                        "changeCurrentWeek: Changing currentWeek in questionListViewModel. for week $week"
                    )
                    questionListViewModel.currentWeek.value = week
                    questionListViewModel.getQuestions(week)
                }
                else -> {
                    Toast.makeText(
                        context,
                        "Please select questions from weeks 1-6",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
fun MyApp(onWeekSelect: (weekNumber: String) -> Unit) {
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

            WeekSelectionCard(weeks = listOf(WK1, WK2, WK3, WK4, WK5, WK6)) { weekNumber ->
                onWeekSelect.invoke(weekNumber)
            }
        }
    }
}

@Composable
fun WeekSelectionCard(weeks: List<String>, onWeekSelect: (weekNumber: String) -> Unit) {
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
            for (week in weeks.size downTo 0 step 2) {
                if (week >= 0 && (week + 1) < weeks.size)
                    ButtonRow(weeks = listOf(weeks[week], weeks[week + 1])) { weekNo ->
                        onWeekSelect.invoke(weekNo)
                    }
            }
        }
    }
}

@Composable
fun ButtonRow(weeks: List<String>, onWeekSelect: (weekNumber: String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        weeks.forEach { week ->
            WeekButton(
                weekNumber = week,
                modifier = Modifier
                    .layoutId(week)
                    .padding(16.dp)
            ) { weekNumber -> onWeekSelect.invoke(weekNumber) }
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
        shape = RoundedCornerShape(20),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = modifier
            .widthIn(min = 120.dp, max = 200.dp)
            .heightIn(min = 60.dp, max = 120.dp)
            .border(2.dp, MaterialTheme.colors.primaryVariant, shape = RoundedCornerShape(20))
            .shadow(8.dp)
            .clickable {
                clickReaction.invoke(weekNumber)
            }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = weekNumber,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colors.onSurface
            )
        }
    }

}

private fun checkApiState(
    questionListState: ApiState<*>,
    questionListViewModel: QuestionListViewModel
) {
    Log.e(CHECK_TAG, "checkApiState: Checking the state $questionListState")
    with(questionListState) {
        when (this) {
            is ApiState.Success.QuestionApiSuccess -> {
                Log.e(CHECK_TAG, "MyAppScreen: Success: $this, setting questionList")
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