package com.example.studyapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.example.studyapp.data.model.Question
import com.example.studyapp.ui.composables.screens.currentquestionscreen.CurrentQuestionContent
import com.example.studyapp.ui.composables.screens.homescreen.MyApp
import com.example.studyapp.ui.composables.screens.weekquestionsscreen.WeekQuestions
import com.example.studyapp.ui.composables.sharedcomposables.DrawerImage
import com.example.studyapp.ui.composables.sharedcomposables.DrawerItem
import com.example.studyapp.ui.composables.sharedcomposables.LoginScreenContent
import com.example.studyapp.ui.composables.sharedcomposables.StudyTopAppBar
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.ui.viewmodel.MainViewModel
import com.example.studyapp.ui.viewmodel.QuestionListViewModel
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.*
import com.example.studyapp.util.State.QuestionApiState
import com.example.studyapp.util.State.UserApiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val questionListViewModel: QuestionListViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.observeRepo()
        setContent {
            StudyAppTheme {
                AppNavigator()
            }
        }
    }

    @ExperimentalCoilApi
    @Composable
    fun AppNavigator(
        userViewModel: UserViewModel = viewModel()
    ) {
        val scope = rememberCoroutineScope()
        val state = rememberScaffoldState()
        val navController = rememberNavController()
        val currentUserState by userViewModel.loginScreenState.collectAsState(initial = UserApiState.Sleep())


        Scaffold(
            backgroundColor = MaterialTheme.colors.background,
            drawerContent = {
                NavDrawer(screenState = currentUserState, state = state, scope = scope)
            },
            drawerElevation = 8.dp,
            drawerBackgroundColor = MaterialTheme.colors.surface,
            scaffoldState = state
        ) {
            NavHost(
                navController = navController,
                startDestination = Screens.LoginScreen.route
            ) {
                composable(Screens.LoginScreen.route) {
                    ExampleAnimation {
                        Column {
                            StudyTopAppBar(
                                text = "",
                                destination = navController.currentDestination
                            ) {
                                handleButtonOptions(it, state, navController, scope)
                            }
                            LoginScreen(currentUserState) { userState ->
                                handleUserState(userState, navController)
                            }
                        }
                    }
                }
                composable(Screens.MainScreen.route) {
                    ExampleAnimation {
                        Column {
                            StudyTopAppBar(
                                text = "Android Study App",
                                navController.currentDestination
                            ) {
                                handleButtonOptions(it, state, navController, scope)
                            }
                            MyAppScreen(navController)
                        }
                    }
                }
                composable(Screens.WeekQuestionsScreen.route) {
                    ExampleAnimation {
                        Column {
                            StudyTopAppBar(
                                text = "Question List",
                                destination = navController.currentDestination
                            ) {
                                handleButtonOptions(it, state, navController, scope)
                            }
                            QuestionListScreen(navController)
                        }
                    }
                }
                composable(Screens.QuestionScreen.route) {
                    ExampleAnimation {
                        Column {
                            StudyTopAppBar(
                                text = "Question Display",
                                navController.currentDestination
                            ) {
                                handleButtonOptions(it, state, navController, scope)
                            }
                            QuestionScreen(navController)
                        }
                    }
                }
            }
        }
    }

    private fun handleUserState(userState: UserApiState<Any>, navController: NavController) {
        val TAG = "MainActivity"
        when (userState) {
            is UserApiState.Loading, is UserApiState.Sleep -> {
                Toast.makeText(this, "State is $userState", Toast.LENGTH_LONG).show()
                Log.e(
                    TAG,
                    "LoginScreen: User changed but not valid yet. Loading or asleep."
                )
            }
            is UserApiState.Success -> {
                val user = userState.user
                if (!user.isDefault) {
                    Toast.makeText(
                        this,
                        "Got a valid user object. Username: ${user.name} email: ${user.email}",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.navigate(Screens.MainScreen.route)
                } else {
                    Toast.makeText(this, "Default User received.", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Default User retrieved. Ignoring.")
                }
            }
            is UserApiState.Error -> {
                val message = userState.message
                Toast
                    .makeText(
                        this,
                        "Sorry, had trouble getting profile Info. Message: $message",
                        Toast.LENGTH_LONG
                    )
                    .show()
                Log.e(TAG, "AppNavigator: Error message: $message")
            }
        }
    }


    @ExperimentalCoilApi
    @Composable
    fun NavDrawer(
        screenState: UserApiState<Any>,
        navController: NavController = rememberNavController(),
        scope: CoroutineScope = rememberCoroutineScope(),
        state: ScaffoldState = rememberScaffoldState()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (screenState) {
                is UserApiState.Success -> {
                    with(screenState.user) {
                        DrawerImage(
                            imageID = R.drawable.ic_account_circle,
                            description = "Image of account holder",
                            imageUrl = photoUrl,
                            name = name
                        )
                    }

                }
                else -> {
                    DrawerImage(
                        imageID = R.drawable.ic_account_circle,
                        description = "Image of account holder",
                        imageUrl = null
                    )

                }
            }

            Divider()
            DrawerItem(text = DrawerOptions.HOME) {
                closeDrawer(state, scope)
                handleDrawerSelection(it, navController)
            }
            Divider()
            DrawerItem(text = DrawerOptions.SCOREBOARD) {
                closeDrawer(state, scope)
                handleDrawerSelection(it, navController)
            }
        }
    }

    private fun handleDrawerSelection(option: DrawerOptions, navController: NavController) {
        when (option) {
            DrawerOptions.HOME -> {
                navController.navigate(Screens.MainScreen.route)
            }
            DrawerOptions.SCOREBOARD -> {
                Toast.makeText(
                    navController.context,
                    "Leader Board not yet created.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openDrawer(state: ScaffoldState, scope: CoroutineScope) =
        scope.launch(Dispatchers.Main) {
            state.drawerState.open()
        }

    private fun closeDrawer(state: ScaffoldState, scope: CoroutineScope) =
        scope.launch(Dispatchers.Main) {
            state.drawerState.close()
        }

    private fun handleButtonOptions(
        option: ButtonOptions,
        state: ScaffoldState,
        navController: NavController,
        scope: CoroutineScope
    ) {
        when (option) {
            ButtonOptions.BACK -> {
                navController.navigateUp()
            }
            ButtonOptions.MENU -> {
                when (state.drawerState.currentValue) {
                    DrawerValue.Closed -> {
                        openDrawer(state, scope)
                    }
                    DrawerValue.Open -> {
                        closeDrawer(state, scope)
                    }
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

    //Screen Composable

    @Composable
    fun LoginScreen(
        loginScreenState: UserApiState<Any>,
        userViewModel: UserViewModel = viewModel(),
        onLoginSuccess: (UserApiState<Any>) -> Unit
    ) {
        val TAG = "LOGIN_SCREEN"

        var isNewUserSignUp by remember { mutableStateOf(false) }

        LoginScreenContent(isNewUserSignUp) { verificationOption, email, password ->
            Log.e(
                TAG,
                "exiting screen content. \n VerificationOption:$verificationOption \n Email:$email \n Password:$password"
            )
            when (verificationOption) {
                VerificationOptions.EmailPassword -> {
                    Log.e(TAG, "LoginScreen: in Verification option email password.")
                    userViewModel.signInWithEmail(email, password)
                    Log.e(
                        TAG,
                        "LoginScreen: ViewModel called. currentUser is $loginScreenState"
                    )

                }
                VerificationOptions.NewUser -> {
                    if (isNewUserSignUp) {
                        Log.e(
                            TAG,
                            "LoginScreen: New User sign up: $isNewUserSignUp, going to viewModel"
                        )
                        userViewModel.signUpWithEmail(email, password)
                    } else {
                        Log.e(
                            TAG,
                            "LoginScreen: New User sign up: $isNewUserSignUp, changing value for recomposition."
                        )
                        //change the value to recompose LoginScreenContent.
                        isNewUserSignUp = true
                    }
                }
                VerificationOptions.Back -> {
                    Log.e(
                        TAG,
                        "LoginScreen: New User sign up: $isNewUserSignUp, changing value for recomposition."
                    )
                    //change the value to recompose LoginScreenContent.
                    isNewUserSignUp = false
                }
            }
        }

        SideEffect {
            Log.e(TAG, "LoginScreen: LAUNCHING EFFECT!!!")
            when (loginScreenState) {
                is UserApiState.Sleep -> {
                    Log.e(TAG, "LoginScreen: Invoking Login Sleep. $loginScreenState")
                    Toast.makeText(this, "Not now, State is sleeping.", Toast.LENGTH_SHORT).show()
                }
                is UserApiState.Success -> {
                    val user = loginScreenState.user
                    if (!user.isDefault) {
                        Log.e(TAG, "LoginScreen: Invoking Login Success. $loginScreenState")
                        onLoginSuccess.invoke(loginScreenState)
                        Toast.makeText(this, "New User secured ${user.uid}", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Got the default user back", Toast.LENGTH_SHORT).show()
                        Log.e(
                            TAG,
                            "LoginScreen: Default user was captured. Still not ready. $user"
                        )
                    }
                }
                is UserApiState.Loading -> {
                    Toast.makeText(this, "Resource is currently loading", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "LoginScreen: Invoking Loading screen. $loginScreenState")
                }
                is UserApiState.Error -> {
                    Toast.makeText(
                        this,
                        "OOps there was an error!!! ${loginScreenState.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "LoginScreen: ERROR: ${loginScreenState.message}")
                }
            }

        }

    }

    @Composable
    fun MyAppScreen(navController: NavController) {
        val TAG = "My App Screen"
        val apiState = mainViewModel.apiState.observeAsState()

        Column {
            MyApp { week ->
                changeCurrentWeek(week, navController)
            }
        }

        Log.e(TAG, "Api state was $apiState")

        SideEffect {
            apiState.value?.let {
                checkApiState(it) { route ->
                    navController.navigate(route)
                }
            }
        }
    }


    @Composable
    fun QuestionListScreen(navController: NavController) {
        mainViewModel.changeState()
        val questions by questionListViewModel.questions.observeAsState()
        val progress by questionListViewModel.currentProgress.observeAsState()
        val currentWeek by questionListViewModel.currentWeek.observeAsState()

        Column {
            questions?.let {
                WeekQuestions(
                    questions = it,
                    progress = progress,
                    currentWeek = currentWeek
                ) { question ->
                    questionListViewModel.setCurrentQuestion(question)
                    navController.navigate(Screens.QuestionScreen.route)
                }
                questionListViewModel.setCurrentProgress(it.generateStudentProgress())
            }
        }
    }

    @Composable
    fun QuestionScreen(navController: NavController) {
        val currentQuestion by questionListViewModel.currentQuestion.observeAsState()

        currentQuestion?.let {
            CurrentQuestionContent(question = it) { text, question ->
                if (!checkButtonAnswer(text, question)) {
                    navController.navigateUp()
                }
            }
        }
    }


    //helpful variable. Should be raised.
    private val CHECK_TAG = "CheckApiState function"

    //Helper functions
    private fun checkApiState(
        questionListState: QuestionApiState<List<Question>>,
        navigate: (String) -> Unit
    ) {
        with(questionListState) {
            when (this) {
                is QuestionApiState.Success -> {
                    Log.e(CHECK_TAG, "MyAppScreen: Success: $this")
                    questionListViewModel.setQuestionList(this.questionList)
                    navigate.invoke(Screens.WeekQuestionsScreen.route)
                }
                is QuestionApiState.Sleep, is QuestionApiState.Loading -> {
                    Log.e(CHECK_TAG, "STATE : ${this})")
                }
                else -> {
                    Log.e(CHECK_TAG, "STATE ERROR: Unrecognized Api State.")
                }
            }
        }

    }

    private fun changeCurrentWeek(week: String, navController: NavController) {
        when (week) {
            WK1, WK2, WK3, WK4, WK5, WK6 -> {
                questionListViewModel.currentWeek.value = week
                mainViewModel.getQuestions(week)
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

    private fun checkButtonAnswer(text: String, question: Question): Boolean {
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

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
    }

}


