package com.example.studyapp.repo

import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.data.remote.QuestionsAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    private val databaseDao: QuestionDAO
) : RepositoryInterface {

    override fun getQuestionsByWeek(week: String) {

    }

    override fun setQuestions() {

    }
}