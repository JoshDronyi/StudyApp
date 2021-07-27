package com.example.studyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StudentProgress(
    @PrimaryKey
    val week : Int,
    val totalQuestions : Int,
    val answeredQuestions : Int,
    val correctAnswers : Int
)