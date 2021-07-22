package com.example.studyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Question(
    @PrimaryKey
    var id : String = "",
    val questionNumber : Int = 0,
    val questionText: String = "",
    val correctAnswer: String = "",
    val wrongAnswer1: String = "",
    val wrongAnswer2: String = "",
    val wrongAnswer3: String = "",
    var questionStatus : Int = 0,
    val week : Int = 0
) {
    fun mixAnswers() = listOf(
        correctAnswer,
        wrongAnswer1,
        wrongAnswer2,
        wrongAnswer3
    ).shuffled()
}

fun List<Question>.generateStudentProgress() : StudentProgress {
    var correct = 0
    var week = 0
    var answeredQuestions = 0
    forEach {
        week = it.week
        if(it.questionStatus==1)
            correct++
        if(it.questionStatus==0){
            answeredQuestions++
        }
    }
    return StudentProgress(week = week,totalQuestions = size,answeredQuestions = answeredQuestions,correctAnswers = correct)
}
