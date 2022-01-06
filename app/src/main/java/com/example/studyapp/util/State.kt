package com.example.studyapp.util

import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.User

sealed class State {

    sealed class ApiState<out T>() : State() {
        open class Success<out A>(val data: A) : ApiState<A>() {
            data class QuestionApiSuccess(val questionList: List<Question>) :
                Success<List<Question>>(questionList)

            data class UserApiSuccess<out User>(private val user: User) : Success<User>(user)
            data class DefaultUserSuccess<out User>(private val user: User) : Success<User>(user)
        }

        data class Error(val data: StudyAppError) : ApiState<StudyAppError>()
        object Loading : ApiState<Nothing>()
        object Sleep : ApiState<Nothing>()

    }

    sealed class ScreenState : State() {
        data class LoginScreenState(
            var loginOption: VerificationOptions = VerificationOptions.EMAIL_PASSWORD,
            var isSignUp: Boolean = false,
            var showDatePicker: Boolean = false,
            var error: StudyAppError? = null,
            var apiState: ApiState<*> = ApiState.Sleep
        ) : ScreenState()

        data class HomeScreenState(val currentUser: User) : State()

        data class QuestionListScreenState(
            val questionList: List<Question>
        ) : ScreenState()

        data class QuestionScreenState(
            val currentQuestion: Question
        ) : ScreenState()
    }


}