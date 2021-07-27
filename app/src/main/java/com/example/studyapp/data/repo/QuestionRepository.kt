package com.example.studyapp.data.repo

import android.util.Log
import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.data.model.Question
import com.example.studyapp.util.formatWeekStringToInt
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import javax.security.auth.callback.Callback

@Singleton
class QuestionRepository @Inject constructor(
    private val databaseDao: QuestionDAO,
    private val firebaseDatabase: FirebaseDatabase
) : RepositoryInterface {

    @ExperimentalCoroutinesApi
    override suspend fun getQuestionsByWeek(week: String) = callbackFlow {
        try {
            val ref = firebaseDatabase.getReference("/$week")
            val questions = mutableListOf<Question>()
            val callback = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val question = it.getValue(Question::class.java) ?: return
                        questions.add(question)
                    }
                    trySend(questions)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        "Firebase Question",
                        "Request for questions from firebase is cancelled due to error: ${
                            error.toException().printStackTrace()
                        }"
                    )
                }
            }
            ref.addValueEventListener(callback)
            awaitClose {
               removeReference(ref,callback)
            }
        } catch (e: IllegalStateException) {
            Log.e("EXCEPTION", e.message.toString())
        }
    }

    private fun removeReference(ref:DatabaseReference, callback: ValueEventListener){
        ref.removeEventListener(callback)
    }

    override suspend fun saveQuestionsInDatabase(questions: List<Question>) {
        databaseDao.insertQuestions(*questions.toTypedArray())
    }

    override suspend fun getQuestionsByWeekOnDatabase(week: String) = flow {
        emit(databaseDao.getQuestionsByWeek(formatWeekStringToInt(week)).first())
    }


    override fun setQuestions() {

    }
}