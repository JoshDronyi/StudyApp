package com.example.studyapp.util

import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.User

sealed class SideEffects {

    open class LoginScreenSideEffects : SideEffects() {
        data class SetLoginType(val verification: VerificationOptions) : LoginScreenSideEffects()
        data class SetCurrentUser(val user: User) : LoginScreenSideEffects()
    }

    open class HomeScreenSideEffects : SideEffects() {
        data class SetCurrentWeek(var currentweek: String) : HomeScreenSideEffects()
    }

    open class QuestionListScreenSideEffects : SideEffects() {
        data class SetCurrentQuestion(var currentQuestion: Question) :
            QuestionListScreenSideEffects()
    }

    open class QuestionScreenSideEffects : SideEffects() {
        data class SetSelectedAnswer(var index: Int) : QuestionScreenSideEffects()
    }


}
