package com.example.studyapp.util

import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
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
            var loginOption: VerificationOptions = VerificationOptions.SIGN_IN,
            var signInOption: SignInOptions = SignInOptions.EMAIL_PASSWORD,
            var showDatePicker: Boolean = false,
            var error: StudyAppError = StudyAppError.newBlankInstance(),
            var email: String = "default",
            var password: String = "passy1",
            var validEmail: Boolean = true,
            var validPassword: Boolean = true,
            var apiState: ApiState<*> = ApiState.Sleep,
            var currentUser: User = User.newBlankInstance()
        ) : ScreenState()

        data class HomeScreenState(
            var apiState: ApiState<*> = ApiState.Sleep
        ) : State()

        data class QuestionListScreenState(
            var questionList: List<Question>,
            var progress: StudentProgress,
            var currentWeek: String = WK1
        ) : ScreenState()

        data class QuestionScreenState(
            val currentQuestion: Question,
            var questionList: List<Question>
        ) : ScreenState()
    }


}