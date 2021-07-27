package com.example.studyapp.util

import android.util.Log
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress

fun formatWeekString(week: String): String {
    Log.e("TESTE", QuestionStatus.WRONG_ANSWER.ordinal.toString())
    return "Week ${week.filter { it.isDigit() }}"
}

fun formatWeekStringToInt(week: String): Int {
    return week.filter { it.isDigit() }.toInt()
}


fun List<Question>.generateStudentProgress(): StudentProgress {
    return StudentProgress(
        week = get(0).week,
        totalQuestions = size,
        answeredQuestions = count { it.questionStatus != QuestionStatus.NOT_ANSWERED.ordinal },
        correctAnswers = count { it.questionStatus == QuestionStatus.CORRECT_ANSWER.ordinal }
    )
}


enum class QuestionStatus {
    NOT_ANSWERED,
    CORRECT_ANSWER,
    WRONG_ANSWER
}