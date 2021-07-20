package com.example.studyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Question(
    @PrimaryKey
    var id: Int,
    val question: String,
    val correctAnswer: String,
    val wrongAnswer1: String,
    val wrongAnswer2: String,
    val wrongAnswer3: String,
    val week : Int
) {
    fun mixAnswers() = listOf(
        correctAnswer,
        wrongAnswer1,
        wrongAnswer2,
        wrongAnswer3
    ).shuffled()
    companion object{
        fun emptyQuestion() = Question(0,"","","","","",0)
    }
}
