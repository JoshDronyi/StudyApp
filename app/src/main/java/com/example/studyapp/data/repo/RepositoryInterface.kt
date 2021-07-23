package com.example.studyapp.data.repo

import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.ApiState
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getQuestionsByWeek(week: String): Flow<List<Question>>
    suspend fun saveQuestionsInDatabase(questions: List<Question>)
    suspend fun getQuestionsByWeekOnDatabase(week: String): Flow<ApiState.Success<List<Question>>>
    fun setQuestions()

}