package com.example.studyapp.util

sealed class Screens(val route: String) {
    object LoginScreen : Screens("loginScreen")
    object MainScreen : Screens("mainScreen")
    object WeekQuestionsScreen : Screens("weekQuestionsScreen")
    object QuestionScreen : Screens("questionScreen")
}
