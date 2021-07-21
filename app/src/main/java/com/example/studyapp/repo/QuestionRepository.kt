package com.example.studyapp.repo

import android.renderscript.Sampler
import android.util.Log
import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.model.Question
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    private val databaseDao: QuestionDAO,
    private val firebaseDatabase : FirebaseDatabase
) : RepositoryInterface {

    override suspend fun getQuestionsByWeek(week: String) = callbackFlow {
        try {
            val ref = firebaseDatabase.getReference("/$week")
            Log.e("Firebase Week",ref.database.toString())
            val questions = mutableListOf<Question>()

            val callback = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e("DataSnapshot",snapshot.key.toString())
                    snapshot.children.forEach {
                        val question = it.getValue(Question::class.java) ?: return
                        Log.e("Firebase Question",question.correctAnswer)
                        questions.add(question)
                    }
                    trySend(questions)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            ref.addValueEventListener(callback)
            awaitClose {
                ref.removeEventListener(callback)
            }
        }catch (e : IllegalStateException){
            Log.e("EXCEPTION",e.message.toString())
        }
    }

    override suspend fun saveQuestionsInDatabase(questions: List<Question>) = flow {
        databaseDao.insertQuestions(*questions.toTypedArray())
        //val weekNumber = week.filter { it.isDigit() }.toInt()
        //Log.e("Week Number", weekNumber.toString())
        databaseDao.getQuestionsByWeek(questions[0].week).collect { data ->
            data.forEach {
                Log.e("Question",it.questionText)
            }
            emit(data)
        }
    }


    override fun setQuestions() {

    }
}