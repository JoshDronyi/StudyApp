package com.example.studyapp.model

data class Question(
    var id: Int,
    val question: String,
    val correctAnswer: String,
    val wrongAnswer1: String,
    val wrongAnswer2: String,
    val wrongAnswer3: String
) {
    fun mixQuestions() = listOf<String>(
        correctAnswer,
        wrongAnswer1,
        wrongAnswer2,
        wrongAnswer3
    ).shuffled()
}