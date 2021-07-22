package com.example.studyapp.repo

import com.example.studyapp.model.ApiState
import com.example.studyapp.model.Question
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getQuestionsByWeek(week:String): Flow<List<Question>>
    suspend fun saveQuestionsInDatabase(questions : List<Question>)
    suspend fun getQuestionsByWeekOnDatabase(week: String) : Flow<ApiState.Success<List<Question>>>
    fun setQuestions()

}