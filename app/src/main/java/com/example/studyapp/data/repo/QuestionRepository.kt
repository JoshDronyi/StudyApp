package com.example.studyapp.data.repo

import android.util.Log
import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.remote.FirebaseDatabaseDataSource
import com.example.studyapp.util.*
import com.example.studyapp.util.State.ApiState.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class QuestionRepository @Inject constructor(
    override val firebaseDatabase: FirebaseDatabaseDataSource,
    private val localDao: QuestionDAO
) : RepositoryInterface {

    private val TAG = "Question Repository"
    private val localDataCache: MutableMap<String, List<Question>> = mutableMapOf()
    private val remoteDataCache: MutableMap<String, List<Question>> = mutableMapOf()

    suspend fun getQuestionsByWeek(week: String) = channelFlow {
        //Get the data from the local question set.
        send(Loading)
        val localAsync = async {
            getQuestionsByWeekOnDatabase(week).collect { localWKQuestion ->
                localDataCache[week] = localWKQuestion
                Log.e(
                    TAG,
                    "getQuestionsByWeek: local data cache was for week $week was $localWKQuestion",
                )
                if (localWKQuestion.isNullOrEmpty()) return@collect
                send(
                    Success.QuestionApiSuccess(localWKQuestion)
                )
            }
        }
        val remoteAsync = async {
            firebaseDatabase.getQuestionsByWeek(week).collect { internetQuestions ->
                Log.e(TAG, "getQuestionsByWeek: got questions by week $internetQuestions")
                when (internetQuestions) {
                    is Success.QuestionApiSuccess -> {
                        remoteDataCache[week] = internetQuestions.questionList
                        if (localDataCache[week].isNullOrEmpty()) {
                            send(internetQuestions)
                        }
                    }
                    else -> {
                        //Pass the error to be displayed
                        send(internetQuestions)
                    }
                }
            }
        }
        //Get the data from the remote data set.
        remoteAsync.await()
        localAsync.await()
        Log.e(
            TAG,
            "getQuestionsByWeek: after Await, remoteAsync: ${remoteDataCache[week]} \n localAsync:${localDataCache[week]}"
        )
        compareRemoteQuestionSet(
            remoteDataCache[week]!!,
            localDataCache[week]!!
        ).collect { comparedQuestions ->
            send(comparedQuestions)
        }
    }

    private suspend fun compareRemoteQuestionSet(
        dataFromTheInternet: List<Question>,
        localQuestionList: List<Question>
    ): Flow<State.ApiState<*>> = channelFlow {
        val state = if (localQuestionList.isNotEmpty()) {
            val state = mergeLocalAndRemoteQuestionSets(
                localQuestionList,
                dataFromTheInternet
            )
            state
        } else {
            Log.e(
                TAG,
                "getQuestions: DB Questions: $localQuestionList \n internetQuestions: $dataFromTheInternet"
            )
            val state = if (dataFromTheInternet.isNotEmpty()) {
                Log.e(
                    TAG,
                    "compareRemoteQuestionSet: setting homeState to $dataFromTheInternet",
                )
                saveQuestionsInDatabase(dataFromTheInternet)
                Success.QuestionApiSuccess(dataFromTheInternet)
            } else {
                Log.e(
                    TAG,
                    "compareRemoteQuestionSet: empty question set from the internet"
                )

                Error(
                    StudyAppError.newBlankInstance().apply {
                        this.data =
                            Exception("No questions found in this path. Checked localDB and remote.")
                        this.errorType = ErrorType.NETWORK
                        this.message =
                            "There were no questions stored under this path."
                        this.shouldShow = true
                    }
                )
            }
            state
        }
        send(state)
    }

    private suspend fun mergeLocalAndRemoteQuestionSets(
        localQuestionList: List<Question>,
        dataFromTheInternet: List<Question>
    ): State.ApiState<*> {
        val finalList: MutableList<Question> = mutableListOf()
        val unsavedQuestions: MutableList<Question> = mutableListOf()

        Log.e(TAG, "getQuestions: Questions from the database is not empty")

        dataFromTheInternet.forEachIndexed { index, internetQuestion ->
            Log.e(
                TAG,
                "getQuestions: index was $index, \n questionList was ${localQuestionList[index]} \n internetQuestion was $internetQuestion "
            )
            if (localQuestionList.contains(internetQuestion)) {
                val questionIndex = localQuestionList.indexOf(internetQuestion)
                internetQuestion.questionStatus =
                    localQuestionList[questionIndex].questionStatus
            } else {
                unsavedQuestions.add(internetQuestion)
            }
            finalList.add(internetQuestion)
        }

        if (!unsavedQuestions.isNullOrEmpty()) {
            Log.e(
                TAG,
                "compareRemoteQuestionSet: unsavedQuestions were $unsavedQuestions",
            )
            saveQuestionsInDatabase(unsavedQuestions)
        }

        finalList.addAll(unsavedQuestions)
        Log.e(
            TAG,
            "compareRemoteQuestionSet: Setting APIState -> Final list was $finalList"
        )
        return Success.QuestionApiSuccess(finalList)
    }


    @ExperimentalCoroutinesApi
    fun addNewQuestionToWeek(week: String, question: Question) =
        firebaseDatabase.addNewQuestionToWeek(week, question, ResultType.QUESTION)


    private suspend fun saveQuestionsInDatabase(questions: List<Question>) =
        localDao.insertQuestions(*questions.toTypedArray())

    suspend fun saveQuestionsToDB(questions: List<Question>) = saveQuestionsInDatabase(questions)


    private fun getQuestionsByWeekOnDatabase(week: String) = localDao.getQuestionsByWeek(
        formatWeekStringToInt(week)
    )


    override fun onDestroy() {
        //no references to destroy
    }
}

