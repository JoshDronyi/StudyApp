package com.example.studyapp.data.repo

import com.example.studyapp.data.model.Question
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getQuestionsByWeek(week: String): Flow<List<Question>>
    suspend fun saveQuestionsInDatabase(questions: List<Question>)
    suspend fun getQuestionsByWeekOnDatabase(week: String): Flow<List<Question>>
    fun setQuestions()
}