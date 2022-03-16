package com.example.studyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Question(
    @PrimaryKey
    var id: String = "",
    var questionNumber: String = "0",
    var questionText: String = "",
    var correctAnswer: String = "",
    var answer1: String = "",
    var answer2: String = "",
    var answer3: String = "",
    var topic: String = "",
    var questionStatus: String = "0",
    var week: String = "0"
) {
    fun mixAnswers() = listOf(
        correctAnswer,
        answer1,
        answer2,
        answer3
    ).shuffled()

    companion object {
        fun newBlankInstance(): Question {
            return Question("", "", "", "", "", "", "", "", "", "")
        }
    }
}