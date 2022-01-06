package com.example.studyapp.util

import com.example.studyapp.data.model.Question

sealed class Events {
    open class LoginScreenEvents : Events() {
        data class onEmailLoginAttempt(val email: String, val password: String) :
            LoginScreenEvents()

        data class onLoginMethodSwitch(val signInMethod: VerificationOptions) : LoginScreenEvents()
        object onGoogleLoginAttempt : LoginScreenEvents()
    }

    open class HomeScreenEvents : Events() {
        data class onWeekSelected(val selectedWeek: String) : HomeScreenEvents()
    }

    open class QuestionListScreenEvents : Events() {
        data class onQuestionSelected(val question: Question) : QuestionListScreenEvents()
    }

    open class QuestionScreenEvents : Events() {
        data class onAnswerSelected(val selectedIndex: Int) : QuestionScreenEvents()
    }
}