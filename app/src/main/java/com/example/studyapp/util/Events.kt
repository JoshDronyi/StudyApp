package com.example.studyapp.util

import android.content.Context
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.User

sealed class Events {
    open class LoginScreenEvents : Events() {
        data class onEmailLoginAttempt(val email: String, val password: String) :
            LoginScreenEvents()

        data class onSignUpAttempt(
            val newUser: User,
            val passwordText: String,
            val verifyPWText: String,
            val context: Context
        ) : LoginScreenEvents()

        data class onLoginMethodSwitch(val signInMethod: SignInOptions) : LoginScreenEvents()
        data class onToggleOption(
            val toggleable: Toggleable,
            val verification: VerificationOptions?
        ) :
            LoginScreenEvents()

        object onGoogleLoginAttempt : LoginScreenEvents()
        object onClearError : LoginScreenEvents()
        data class onValidationError(val error: StudyAppError, val value: String) :
            LoginScreenEvents()
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