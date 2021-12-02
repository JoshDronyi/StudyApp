package com.example.studyapp.data.repo

import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.remote.FirebaseDatabaseDataSource
import com.example.studyapp.util.ResultType
import com.example.studyapp.util.formatWeekStringToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class QuestionRepository @Inject constructor(
    override val firebaseDatabase: FirebaseDatabaseDataSource,
    private val localDao: QuestionDAO
) : RepositoryInterface {

    private val TAG = "Question Repository"

    fun getQuestionsByWeek(week: String) =
        firebaseDatabase.getQuestionsByWeek(week)


    @ExperimentalCoroutinesApi
    fun addNewQuestionToWeek(week: String, question: Question) =
        firebaseDatabase.addNewQuestionToWeek(week, question, ResultType.QUESTION)


    suspend fun saveQuestionsInDatabase(questions: List<Question>) =
        localDao.insertQuestions(*questions.toTypedArray())


    fun getQuestionsByWeekOnDatabase(week: String) = localDao.getQuestionsByWeek(
        formatWeekStringToInt(week)
    )


    override fun onDestroy() {
        //no references to destroy
    }
}

