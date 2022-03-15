package com.example.studyapp.util

import android.content.Context
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.User

sealed class SideEffects {

    open class LoginScreenSideEffects : SideEffects() {
        data class SetLoginType(val signInMethod: SignInOptions) : LoginScreenSideEffects()
        data class SetCurrentUser(val user: User) : LoginScreenSideEffects()
        data class EmailLoginAttempt(val email: String, val password: String) :
            LoginScreenSideEffects()

        object ClearError : LoginScreenSideEffects()
        data class ToggleItems(
            val toggleable: Toggleable,
            val verification: VerificationOptions?
        ) :
            LoginScreenSideEffects()

        data class OnSignUpAttempt(
            val newUser: User,
            val passwordText: String,
            val verifyPWText: String,
            val context: Context
        ) : LoginScreenSideEffects()

        data class Navigate(val target: Screens) : LoginScreenSideEffects()

    }

    open class HomeScreenSideEffects : SideEffects() {
        data class SetCurrentWeek(var currentweek: String) : HomeScreenSideEffects()
        data class GoToQuestionSet(val questions: List<Question>) : HomeScreenSideEffects()
        data class Navigate(val target: Screens) : HomeScreenSideEffects()
    }

    open class QuestionListScreenSideEffects : SideEffects() {
        data class SetCurrentQuestion(var currentQuestion: Question) :
            QuestionListScreenSideEffects()

        data class Navigate(val target: Screens) : QuestionListScreenSideEffects()
    }

    open class QuestionScreenSideEffects : SideEffects() {
        data class SetSelectedAnswer(var index: Int) : QuestionScreenSideEffects()
        data class Navigate(val target: Screens) : QuestionScreenSideEffects()
    }


}
