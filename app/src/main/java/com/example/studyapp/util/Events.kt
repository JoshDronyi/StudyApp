package com.example.studyapp.util

import android.content.Context
import android.media.metrics.Event
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.User

sealed class Events {
    open class LoginScreenEvents : Events() {
        data class OnEmailLoginAttempt(val email: String, val password: String) :
            LoginScreenEvents()

        data class OnSignUpAttempt(
            val newUser: User,
            val passwordText: String,
            val verifyPWText: String,
            val context: Context
        ) : LoginScreenEvents()

        data class OnLoginMethodSwitch(val signInMethod: SignInOptions) : LoginScreenEvents()
        data class OnToggleOption(
            val toggleable: Toggleable,
            val verification: VerificationOptions?
        ) :
            LoginScreenEvents()

        object OnGoogleLoginAttempt : LoginScreenEvents()
        object OnClearError : LoginScreenEvents()
        object OnComplete : LoginScreenEvents()
        object OnStart
            : LoginScreenEvents()

        data class OnValidationError(val error: StudyAppError, val value: String) :
            LoginScreenEvents()
    }

    open class HomeScreenEvents : Events() {
        data class OnWeekSelected(val selectedWeek: String) : HomeScreenEvents()
        data class GoToSelectedWeek(val questionList: List<Question>) : HomeScreenEvents()
        object ClearApiState : HomeScreenEvents()
    }

    open class QuestionListScreenEvents : Events() {
        data class OnQuestionSelected(val question: Question) : QuestionListScreenEvents()
        data class OnNewWeekSelected(var direction: ButtonOptions) : QuestionListScreenEvents()
        object OnAddNewQuestion : QuestionListScreenEvents()
    }

    open class QuestionScreenEvents : Events() {
        data class OnAnswerSelected(val selectedIndex: Int) : QuestionScreenEvents()
    }
}