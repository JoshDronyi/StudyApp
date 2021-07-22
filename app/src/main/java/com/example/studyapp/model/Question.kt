package com.example.studyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.studyapp.util.QuestionStatus

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
    var questionStatus : Int,
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
    return StudentProgress(
        week = get(0).week,
        totalQuestions = size,
        answeredQuestions = count { it.questionStatus != QuestionStatus.NOT_ANSWERED.ordinal },
        correctAnswers = count { it.questionStatus == QuestionStatus.CORRECT_ANSWER.ordinal }
    )
}
