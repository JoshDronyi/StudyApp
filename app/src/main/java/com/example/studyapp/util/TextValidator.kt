package com.example.studyapp.util

object TextValidator {
    fun isValidEmail(email: String?): Boolean {
        return false
    }

    fun isValidPassword(passwordText: String?): Boolean {
        return false
    }

    fun verifyPassword(validPassword: String, verifyPWText: String): Boolean {
        return false
    }

    fun isValidName(name: String?): Boolean {
        return name?.contains("%d") ?: false
    }

    fun isValidAlias(alias: String?): Boolean {
        return alias?.isBlank() == true || alias?.isEmpty() == true
    }
}