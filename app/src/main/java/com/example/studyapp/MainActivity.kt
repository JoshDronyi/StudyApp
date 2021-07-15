package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.ui.constants.LEFT
import com.example.studyapp.ui.constants.RIGHT
import com.example.studyapp.ui.theme.StudyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigator()
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "mainView") {
        composable("mainView") { MyApp(navController = navController) }
        composable(
            "weekQuestions/{week}",
            arguments = listOf(navArgument("week") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("week")?.let { week ->
                WeekQuestions(week = week)
            }
        }
    }
}

@Composable
fun WeekQuestions(week : String) {
    Text(text = week)
}

@Composable
fun MyApp(navController : NavController) {
    StudyAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            val scaffoldState = rememberScaffoldState()
            Scaffold(
                scaffoldState = scaffoldState
            ) {
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
                            scaffoldState = scaffoldState,
                            modifier = modifier,
                            LEFT,
                            navController
                        )
                        Divider(modifier = Modifier.width(3.dp))
                        ButtonColumn(
                            start = 4,
                            finish = 6,
                            scaffoldState = scaffoldState,
                            modifier = modifier,
                            id = RIGHT,
                            navController
                        )
                    }
                }
            }
        }


    }
}


@Composable
fun ButtonColumn(
    start: Int,
    finish: Int,
    scaffoldState: ScaffoldState,
    modifier: Modifier,
    id: String,
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.layoutId(id)
    ) {
        for (i in start..finish) {
            WeekButton(weekNumber = i, padding = 8.dp, modifier = Modifier.layoutId(i)) {
                val text = "Week$i"
                navController.navigate("weekQuestions/$text")
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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppNavigator()
}