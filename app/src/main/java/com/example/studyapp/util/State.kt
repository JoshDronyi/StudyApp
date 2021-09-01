package com.example.studyapp.util

import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.User

sealed interface ApiState<out T> {
    open class Success(val result: Any) : State()
    open class Error(val msg: String) : State()
    open class Loading : State()
    open class Sleep : State()
}

sealed class State {


    sealed class QuestionApiState<out T>() : ApiState<T> {
        data class Success(val questionList: List<Question>) : QuestionApiState<List<Question>>()
        data class Error(val message: String) : QuestionApiState<String>()
        class Loading : QuestionApiState<Nothing>()
        class Sleep : QuestionApiState<Nothing>()
    }

    sealed class UserApiState<out T>() : ApiState<T> {
        data class Success(val user: User) : UserApiState<User>()
        data class Error(val message: String) : UserApiState<String>()
        class Loading : UserApiState<Nothing>()
        class Sleep : UserApiState<Nothing>()
    }


    sealed class ScreenState<out T> : State() {
        data class LoginScreenState<out T>(
            var userLoginState: UserApiState<Any>
        ) : ScreenState<T>()

        object MainScreenState : State()
        data class QuestionListScreenState<out T>(
            val questionList: List<Question>
        ) : ScreenState<T>()

        data class QuestionScreenState<out T>(
            val currentQuestion: Question
        ) : ScreenState<T>()
    }


}