package com.example.studyapp.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

private const val LOGIN_SCREEN = "loginScreen"
private const val MAIN_SCREEN = "mainScreen"
private const val WEEK_QUESTION_SCREEN = "weekQuestionsScreen"
private const val QUESTION_SCREEN = "questionScreen"

sealed class Screens(val route: String = "") {
    object LoginScreen : Screens(LOGIN_SCREEN)
    object MainScreen : Screens(MAIN_SCREEN)
    object WeekQuestionsScreen : Screens(WEEK_QUESTION_SCREEN)
    object QuestionScreen : Screens(QUESTION_SCREEN)
}

object Navigator {
    val currentScreen: MutableState<Screens> = mutableStateOf(Screens.LoginScreen)
    private val previousScreens: MutableState<MutableList<Screens>> = mutableStateOf(mutableListOf())
    fun navigateTo(destination: Screens) {
        previousScreens.value.add(currentScreen.value)
        currentScreen.value = destination
    }

    fun navigateUp(){
        currentScreen.value = previousScreens.value.removeLast()
    }
}
