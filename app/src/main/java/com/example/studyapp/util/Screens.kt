package com.example.studyapp.util

sealed class Screens(val route:String){
    object MainScreen :Screens("mainView")
    object WeekQuestionsScreen :Screens("weekQuestions")
    object QuestionScreen :Screens("questionView")
}
