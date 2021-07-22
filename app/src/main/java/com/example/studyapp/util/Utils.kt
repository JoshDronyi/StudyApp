package com.example.studyapp.util

import android.util.Log

fun formatWeekString(week : String) : String{
        Log.e("TESTE",QuestionStatus.WRONG_ANSWER.ordinal.toString())
        return "Week ${week.filter { it.isDigit() }}"
}

fun formatWeekStringToInt(week : String) : Int{
        return week.filter { it.isDigit() }.toInt()
}

enum class QuestionStatus {
        NOT_ANSWERED,
        CORRECT_ANSWER,
        WRONG_ANSWER
}