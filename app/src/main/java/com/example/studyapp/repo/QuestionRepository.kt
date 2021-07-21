package com.example.studyapp.repo

import com.example.studyapp.data.local.QuestionDAO
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    private val databaseDao: QuestionDAO,
    private val firebaseDatabase : FirebaseDatabase
) : RepositoryInterface {

    override fun getQuestionsByWeek(week: String) {

    }

    override fun setQuestions() {

    }
}