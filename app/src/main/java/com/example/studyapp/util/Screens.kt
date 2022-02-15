package com.example.studyapp.util

private const val LOGIN_SCREEN = "loginScreen"
private const val MAIN_SCREEN = "mainScreen"
private const val WEEK_QUESTION_SCREEN = "weekQuestionsScreen"
private const val QUESTION_SCREEN = "questionScreen"
private const val SETTINGS_SCREEN = "settingsScreen"
private const val PROFILE_SCREEN = "profileScreen"
private const val NEW_QUESTION_SCREEN = "newQuestion"

sealed class Screens(val route: String = "") {
    object LoginScreen : Screens(LOGIN_SCREEN)
    object HomeScreen : Screens(MAIN_SCREEN)
    object QuestionListScreen : Screens(WEEK_QUESTION_SCREEN)
    object QuestionScreen : Screens(QUESTION_SCREEN)
    object SettingsScreen : Screens(SETTINGS_SCREEN)
    object ProfileScreen : Screens(PROFILE_SCREEN)
    object NewQuestionScreen : Screens(NEW_QUESTION_SCREEN)
}