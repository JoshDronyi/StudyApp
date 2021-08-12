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
    return User(
        uid = this.uid,
        phoneNumber = this.phoneNumber,
        name = this.displayName,
        email = this.email,
        photoUrl = this.photoUrl,
        isAnnonymous = this.isAnonymous
    )
}


enum class QuestionStatus {
    NOT_ANSWERED,
    CORRECT_ANSWER,
    WRONG_ANSWER
}