package com.example.studyapp.ui.composables.screens.homescreen

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.example.studyapp.ui.composables.screen_contracts.HomeContract
import com.example.studyapp.ui.composables.sharedcomposables.ErrorDialog
import com.example.studyapp.ui.composables.sharedcomposables.MainTextCard
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.util.*
import com.example.studyapp.util.Events.HomeScreenEvents
import com.example.studyapp.util.SideEffects.HomeScreenSideEffects.*
import com.example.studyapp.util.State
import com.example.studyapp.util.State.ApiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

const val TAG = "HomeScreen"

@ExperimentalCoroutinesApi
@Composable
fun HomeScreen(
    navController: NavController,
    questionListViewModel: QuestionListViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val homeScreenContract by questionListViewModel.homeScreenContract.collectAsState(HomeContract())
    val theEffect = rememberUpdatedState(newValue = homeScreenContract.screenSideEffects)
    val theState = rememberUpdatedState(newValue = homeScreenContract.screenState.apiState)

    LaunchedEffect(theEffect.value, theState.value) {
        when (val effect = theEffect.value) {
            is SetCurrentWeek -> {
                questionListViewModel.getQuestions(effect.currentweek)
                questionListViewModel.clearSideEffects()
            }
            is GoToQuestionSet -> {
                questionListViewModel.setQuestionList(effect.questions)
                navController.navigateToScreen(Screens.QuestionListScreen)
                questionListViewModel.clearSideEffects()
                questionListViewModel.clearHomeApiState()
            }
            else -> {
                Log.e(TAG, "HomeScreen: the unchecked effect was $effect")
            }
        }
        Log.e(TAG, "MyAppScreen: Side Effect Launched!")
        Log.e(TAG, "MyAppScreen: Checking state. $theState")
    }

    Column {
        MyApp(navController, homeScreenContract.screenState) { event ->
            scope.launch {
                questionListViewModel.setHomeScreenEvent(event)
            }
        }
    }
}

@Composable
fun MyApp(
    navController: NavController,
    theState: State.ScreenState.HomeScreenState,
    onWeekSelect: (event: HomeScreenEvents) -> Unit
) {
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
            when (val apiState = theState.apiState) {
                is ApiState.Success.QuestionApiSuccess -> {
                    onWeekSelect.invoke(
                        HomeScreenEvents.GoToSelectedWeek(apiState.questionList)
                    )
                }
                is ApiState.Sleep -> {
                    Log.e(
                        TAG,
                        "MyAppScreen: Success: $apiState, setting questionList"
                    )

                    Spacer(modifier = Modifier.height(80.dp))

                    WeekSelectionCard(weeks = listOf(WK1, WK2, WK3, WK4, WK5, WK6)) { weekNumber ->
                        onWeekSelect.invoke(
                            HomeScreenEvents.OnWeekSelected(weekNumber)
                        )
                    }
                }
                is ApiState.Loading -> {
                    Log.e(TAG, "STATE : $apiState)")
                    //Show circular progress bar.
                    CircularProgressIndicator()
                }
                is ApiState.Error -> {
                    ErrorDialog(
                        navController = navController,
                        data = apiState.data,
                        title = "Error!!",
                        shouldShow = apiState.data.shouldShow
                    ) {
                        onWeekSelect.invoke(Events.HomeScreenEvents.ClearApiState)
                        navController.navigateUp()
                        Log.e(TAG, "MyApp: Error Dialog dismissed.")
                    }
                }
                else -> {
                    Log.e(TAG, "STATE ERROR: Unrecognized Api State: $apiState")
                }
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
    MyApp(rememberNavController(), State.ScreenState.HomeScreenState()) {
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