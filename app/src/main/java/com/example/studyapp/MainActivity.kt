package com.example.studyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.data.model.ApiState
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.QuestionsViewModel
import com.example.studyapp.util.LEFT
import com.example.studyapp.util.RIGHT
import com.example.studyapp.util.Screens
import com.example.studyapp.util.formatWeekString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val questionsViewModel: QuestionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppNavigator()
                }
            }
        }
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.MainScreen.route) {
            composable(Screens.MainScreen.route) {
                ExampleAnimation {
                    MyAppScreen(navController = navController)
                }
            }
            composable(Screens.WeekQuestionsScreen.route) {
                ExampleAnimation {
                    WeekQuestionsScreen(navController)
                }
            }
            composable(
                Screens.QuestionScreen.route
            ) {
                ExampleAnimation {
                    QuestionScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ExampleAnimation(content: @Composable () -> Unit) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(initialAlpha = 0.3f),
            exit = fadeOut(),
            content = content,
            initiallyVisible = false
        )
    }

    @Composable
    fun MyAppScreen(navController: NavController) {
        val currentQuestion by questionsViewModel.apiState.observeAsState()
        MyApp()
        currentQuestion?.let {
            when (it) {
                is ApiState.Success -> {
                    questionsViewModel.changeState(ApiState.Sleep)
                    questionsViewModel.setQuestions(it.data)
                    navController.navigate("weekQuestions")
                }
                is ApiState.Sleep -> {
                    Log.e("STATE", it.toString())
                }
            }
        }
    }

    @Composable
    fun MyApp() {
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
                            LEFT
                        )
                        Divider(modifier = Modifier.width(3.dp))
                        ButtonColumn(
                            start = 4,
                            finish = 6,
                            modifier = modifier,
                            id = RIGHT
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun WeekQuestionsScreen(navController: NavController) {
        questionsViewModel.changeState(ApiState.Sleep)
        val questions by questionsViewModel.questions.observeAsState()
        questions?.let {
            WeekQuestions(questions = it, navController = navController)
        }
    }

    @Composable
    fun WeekQuestions(navController: NavController, questions: List<Question>) {

        val constraints = ConstraintSet {
            val topText = createRefFor("weekText")
            val listOfQuestions = createRefFor("questionList")

            constrain(topText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(listOfQuestions) {
                top.linkTo(topText.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        }
        ConstraintLayout(constraintSet = constraints, Modifier.fillMaxSize()) {
            questionsViewModel.currentWeek.value?.let {
                Text(
                    text = formatWeekString(it),
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .layoutId("weekText")
                        .padding(16.dp)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .layoutId("questionList")
                    .fillMaxSize()
            ) {
                items(questions) { question ->
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
                                    questionsViewModel.setCurrentQuestion(question = question)
                                    navController.navigate(Screens.QuestionScreen.route)
                                }
                                .fillMaxSize()) {
                            Text(
                                text = "Question number ${question.questionNumber}",
                                modifier = Modifier.padding(
                                    16.dp
                                )
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
        modifier: Modifier,
        id: String
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.layoutId(id)
        ) {
            for (i in start..finish) {
                WeekButton(weekNumber = i, padding = 8.dp, modifier = Modifier.layoutId(i)) {
                    questionsViewModel.getQuestions("week$i")
                    //questionsViewModel.currentWeek.postValue("Week $i")
                    //navController.navigate("weekQuestions")
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
    fun QuestionScreen() {
        val currentQuestion by questionsViewModel.currentQuestion.observeAsState()
        currentQuestion?.let {
            QuestionContent(question = it) {
                questionsViewModel.getNewQuestion()
            }
        }
    }

    @Composable
    fun QuestionContent(question: Question, newQuestionChange: () -> Unit) {
        val constraints = ConstraintSet {
            val questionNumber = createRefFor("questionsNumber")
            val questionText = createRefFor("questionText")
            val buttonAnswer1 = createRefFor("answer0")
            val buttonAnswer2 = createRefFor("answer1")
            val buttonAnswer3 = createRefFor("answer2")
            val buttonAnswer4 = createRefFor("answer3")

            val guideline = createGuidelineFromTop(0.6f)

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
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(buttonAnswer2) {
                top.linkTo(guideline)
                start.linkTo(buttonAnswer1.end)
                end.linkTo(parent.end)
                bottom.linkTo(buttonAnswer4.top)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(buttonAnswer3) {
                top.linkTo(buttonAnswer1.bottom)
                start.linkTo(parent.start)
                end.linkTo(buttonAnswer4.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(buttonAnswer4) {
                top.linkTo(buttonAnswer2.bottom)
                start.linkTo(buttonAnswer3.end)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        }

        ConstraintLayout(constraintSet = constraints, Modifier.fillMaxSize()) {
            Text(
                text = "Question ${question.questionNumber}",
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                modifier = Modifier.layoutId("questionsNumber")
            )
            Text(
                text = question.questionText,
                textAlign = TextAlign.Center,
                fontSize = 50.sp,
                modifier = Modifier
                    .layoutId("questionText")
            )
            question.mixAnswers().forEachIndexed { index, answer ->
                AnswerButton(
                    text = answer,
                    "answer$index",
                    //answer == question.correctAnswer,
                    newQuestionChange
                )
            }
        }
    }

    @Composable
    fun AnswerButton(
        text: String,
        layoutId: String,
        //isCorrect: Boolean,
        newQuestionChange: () -> Unit
    ) {
        val backgroundColor = remember {
            mutableStateOf(Color.Unspecified)
        }
        Button(
            onClick = {
                newQuestionChange.invoke()
            },
            modifier = Modifier
                .layoutId(layoutId)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor.value)
        ) {
            Text(text = text)
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    //AppNavigator()
}