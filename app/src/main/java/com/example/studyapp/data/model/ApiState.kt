package com.example.studyapp.data.model

sealed class ApiState<out T> {
    data class Success(val questionList: List<Question>) : ApiState<List<Question>>()
    data class Error(val msg: String) : ApiState<Nothing>()
    object Loading : ApiState<Nothing>()
    object Sleep : ApiState<Nothing>()
}