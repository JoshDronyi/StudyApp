package com.example.studyapp.util

import com.example.studyapp.data.model.Question

sealed class State {

    sealed class ApiState<out T>() : State() {
        open class Success<out A>(val data: A) : ApiState<A>() {
            class QuestionApiSuccess(val questionList: List<Question>) :
                Success<List<Question>>(questionList)

            class UserApiSuccess<out User>(private val user: User) : Success<User>(user)
            class DefaultUserSuccess<out User>(private val user: User) : Success<User>(user)
        }

        data class Error(val data: StudyAppError) : ApiState<StudyAppError>()
        object Loading : ApiState<Nothing>()
        object Sleep : ApiState<Nothing>()

    }

    sealed class ScreenState : State() {
         class LoginScreenState() : ScreenState()

        object MainScreenState : State()
        data class QuestionListScreenState(
            val questionList: List<Question>
        ) : ScreenState()

        data class QuestionScreenState(
            val currentQuestion: Question
        ) : ScreenState()
    }


}