package com.example.studyapp.util

import java.lang.Exception

data class StudyAppError(
    var data: Exception?,
    var message: String,
    var errorType: ErrorType,
    var shouldShow: Boolean
) {
    companion object {
        fun newBlankInstance(): StudyAppError {
            return StudyAppError(null, "Default error message", ErrorType.DEFAULT, false)
        }
    }
}

enum class ErrorType {
    NETWORK, LOGIN, TEST, DEFAULT, CANCELLED, VALIDATION
}