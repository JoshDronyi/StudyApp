package com.example.studyapp.model

sealed class ApiState<out T> {
    object Success: ApiState<Nothing>()
    //data class Error(val msg: String) : ApiState<Nothing>()
    //object Loading : ApiState<Nothing>()
    object Sleep : ApiState<Nothing>()
}