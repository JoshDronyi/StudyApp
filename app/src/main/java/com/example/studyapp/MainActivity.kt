package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.studyapp.viewmodel.QuestionsViewModel
import com.google.gson.Gson

class MainActivity : ComponentActivity() {

    private val viewModel: QuestionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppNavigator(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigator(viewModel: QuestionsViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "mainView") {
        composable("mainView") { MyApp(navController = navController,viewModel = viewModel) }
        composable("weekQuestions") {
            WeekQuestions(navController, viewModel)
        }
        composable(
            "questionView"
        ) {
            QuestionScreen(viewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    //AppNavigator()
}