package com.example.studyapp.repo

import com.example.studyapp.model.Question
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getQuestionsByWeek(week:String): Flow<List<Question>>
    fun setQuestions()

}