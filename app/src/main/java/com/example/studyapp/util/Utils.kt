package com.example.studyapp.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.model.User
import com.google.firebase.auth.FirebaseUser

fun formatWeekString(week: String): String {
    Log.e("TESTE", QuestionStatus.WRONG_ANSWER.ordinal.toString())
    return "Week ${week.filter { it.isDigit() }}"
}

fun formatWeekStringToInt(week: String): Int {
    return week.filter { it.isDigit() }.toInt()
}

/**
 * Email is valid if :
 * - it matches the regex
 * - it contains no empty spaces
 */
fun String.validateEmail(): Boolean {
    return this.apply {
        this.trim()
            .trimIndent()
    }.matches(EMAIL_REGEX.toRegex())
}

/**
 * Password is valid if:
 * - it has at least  the minimun characters (6 characters)
 * - it contains at least one number
 */
fun String.validatePassword(): Boolean = this.let { theString: String ->
    Log.e("Utils", "validatePassword: theString: $theString")
    return when {
        theString.length < MIN_PW_CHARS -> {
            Log.e("Utils", "validatePassword: Not enough characters")
            false
        }
        theString.count {
            it.isDigit()
        } == 0 -> {
            Log.e("Utils", "validatePassword: Count was wrong")
            false // will be false after testing is done
        }
        else -> true
    }
}


fun verifyText(
    context: Context,
    verificationOption: VerificationOptions,
    email: String,
    password: String
): Boolean {
    return when (verificationOption) {
        VerificationOptions.EmailPassword -> {
            Toast.makeText(
                context,
                "Email/Password chosen \nEmail:$email\nPassword:$password",
                Toast.LENGTH_SHORT
            ).show()
            true
        }
        else -> {
            false
        }
    }
}


fun List<Question>.generateStudentProgress(): StudentProgress {
    return StudentProgress(
        week = get(0).week,
        totalQuestions = size,
        answeredQuestions = count { it.questionStatus != QuestionStatus.NOT_ANSWERED.ordinal },
        correctAnswers = count { it.questionStatus == QuestionStatus.CORRECT_ANSWER.ordinal }
    )
}

fun FirebaseUser.asUser(): User {
    val user = User(
        uid = this.uid,
        phoneNumber = this.phoneNumber,
        name = this.displayName,
        email = this.email,
        photoUrl = this.photoUrl,
        isDefault = this.isAnonymous
    )
    Log.e("asUserHelper", "Creating new User: $user")
    return user
}


enum class QuestionStatus {
    NOT_ANSWERED,
    CORRECT_ANSWER,
    WRONG_ANSWER
}