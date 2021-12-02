package com.example.studyapp.data.remote

import android.util.Log
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.User
import com.example.studyapp.util.*
import com.example.studyapp.util.State.ApiState
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
fun addValueListenerToReference(
    ref: DatabaseReference,
    error: DatabaseError?,
    resultType: ResultType,
    week: String
) = callbackFlow {
    val tag = "addValueListener"
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.e(
                tag,
                "onDataChange: Key:${snapshot.key}, value:${snapshot.value} \n " +
                        "NEED TO MAP FROM SNAPSHOT TO USER OR QUESTION AND BRING TO VIEW."
            )
            when (resultType) {
                ResultType.USER -> {
                    trySend(
                        ApiState.Success.UserApiSuccess(
                            returnSnapShotAsUser(snapshot)
                        )
                    )
                }
                ResultType.QUESTION -> {
                    trySend(
                        ApiState.Success.QuestionApiSuccess(
                            returnSnapShotAsQuestionList(snapshot, week)
                        )
                    )
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(
                tag,
                "addNewUserToRealtimeDatabase: ${error.message}",
            )
            trySend(
                ApiState.Error(
                    returnErrorAsStudyAppError(error)
                )
            )
        }
    }
    error?.let {
        Log.e(
            tag,
            "addNewUserToRealtimeDatabase: ${error}}",
        )
        trySend(
            ApiState
                .Error(returnErrorAsStudyAppError(error))
        )
    }
    ref.addValueEventListener(listener)
    awaitClose {
        ref.removeEventListener(listener)
    }
}


@ExperimentalCoroutinesApi
class FirebaseDatabaseDataSource {
    private val TAG = "Firebase_DB_DS"

    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }


    private fun getReference(path: String) = db.getReference(path)


    @InternalCoroutinesApi
    fun addNewUserToRealtimeDatabase(user: User, resultType: ResultType) = callbackFlow {
        getReference("Users")
            .child(user.uid)
            .setValue(user) { error, ref ->
                CoroutineScope(Dispatchers.IO).launch {
                    addValueListenerToReference(ref, error, resultType, "").collect {
                        trySend(it)
                    }
                }
            }
        awaitClose {
            Log.e(
                TAG,
                "addNewUserToRealtimeDatabase: Closing callback for adding new user to realtime database.",
            )
        }

    }

    fun addNewQuestionToWeek(week: String, question: Question, resultType: ResultType) =
        callbackFlow {
            getReference("Questions")
                .child("/$week")
                .push()
                .setValue(
                    question
                ) { error, ref ->
                    CoroutineScope(Dispatchers.IO).launch {
                        addValueListenerToReference(ref, error, resultType, week).collect {
                            trySend(it)
                        }
                    }
                }
            Log.e(TAG, "addNewQuestionToWeek: Trying to push question[$question] into week [$week]")
            awaitClose {
                Log.e(
                    TAG,
                    "addNewQuestionToWeek: week:$week, question:$question, resultType:$resultType",
                )
            }
        }

    fun getQuestionsByWeek(week: String) = flow {
        val ref = getReference("Questions")
            .child("/$week")

        addValueListenerToReference(ref, error = null, ResultType.QUESTION, week).collectLatest {
            emit(it)
        }
    }

    fun addUserToAdminList(user: User) = getReference("Users")
        .child("admins")
        .child(user.uid)
        .setValue(true)

}